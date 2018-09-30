package lucky.sky.db.jsd;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import lucky.sky.db.jsd.annotation.JsdTable;
import lucky.sky.db.jsd.converter.ConverterManager;
import lucky.sky.util.lang.StrKit;
import lucky.sky.util.reflect.MethodAccess;

/**
 * 对象映射辅助类 on 15/5/22.
 */
public final class Mapper {

  private static Map<String, EntityInfo> infos = new ConcurrentHashMap<>();

  public static EntityInfo getEntityInfo(Class<?> clazz) {
    String key = clazz.getTypeName();
    EntityInfo info = infos.get(key);
    if (info != null) {
      return info;
    }

    info = new EntityInfo(clazz);
    if (info.fields.size() == 0) {
      throw new JsdException("clazz is not a valid entity");
    }

    infos.put(key, info);
    return info;
  }

  public static class EntityInfo {

    private MethodAccess method;
    // 实体对应的表名
    String table;
    // 有效字段(有对应的 get/set 方法)
    Map<String, FieldInfo> fields = new HashMap<>();
    // 删除,更新条件字段(带 Id 注解的)
    String[] idColumns;
    // 插入字段(不带 GeneratedValue 注解的)
    String[] insertColumns;
    // 更新字段(不带 Id 注解的)
    String[] updateColumns;
    // 表分片字段
    String[] shardKeys;

    EntityInfo(Class<?> clazz) {
//        Entity anno = clazz.getAnnotation(Entity.class);
//        if (anno == null) throw new JsdException("clazz doesn't has Entity annotation");
      method = MethodAccess.get(clazz);

      JsdTable jsdTable = clazz.getAnnotation(JsdTable.class);
      NameStyle nameStyle;
      if (jsdTable == null) {
        nameStyle = NameStyle.PASCAL;
      } else {
        nameStyle = jsdTable.nameStyle();
        this.shardKeys = jsdTable.shardKeys();
      }

      javax.persistence.Table tableAnno = clazz.getAnnotation(Table.class);
      if (tableAnno == null || StrKit.isBlank(tableAnno.name())) {
        this.table = NameStyle.transform(clazz.getSimpleName(), nameStyle);
      } else {
        this.table = tableAnno.name();
      }

      initField(clazz, nameStyle);
    }

    private void initField(Class<?> clazz, NameStyle nameStyle) {
      Field[] declaredFields = clazz.getDeclaredFields();
      List<String> tempIdColumns = new ArrayList<>();
      for (Field f : declaredFields) {
        String pascalName = StrKit.firstCharToUpperCase(f.getName());

        FieldInfo fi = getFieldInfo(f, pascalName);
        if (fi == null) {
          continue;
        }

        String name = getFieldName(f, pascalName, nameStyle);
        Id idAnno = f.getAnnotation(Id.class);
        if (idAnno != null) {
          fi.key = true;
          tempIdColumns.add(name);
        }

        GeneratedValue gvAnno = f.getAnnotation(GeneratedValue.class);
        if (gvAnno != null) {
          fi.auto = true;
        }

        this.fields.put(name, fi);
      }

      if (!tempIdColumns.isEmpty()) {
        this.idColumns = tempIdColumns.toArray(new String[0]);
      }

      /**
       * 如果有继承关系，需要增加对父类的处理，这里是属于嵌套
       */
      if (clazz.getSuperclass() != Object.class) {
        initField(clazz.getSuperclass(), nameStyle);
      }
    }

    private FieldInfo getFieldInfo(Field f, String pascalName) {
      Transient transientAnno = f.getAnnotation(Transient.class);
      if (transientAnno != null) {
        return null;
      }

      int getIndex = this.getMethodIndex("get" + pascalName);
      if (getIndex == -1) {
        getIndex = this.getMethodIndex("is" + pascalName);
        if (getIndex == -1) {
          return null;
        }
      }

      int setIndex = this.getMethodIndex("set" + pascalName);
      if (setIndex == -1) {
        return null;
      }

      return new FieldInfo(getIndex, setIndex, f.getType());
    }

    private static String getFieldName(Field f, String pascalName, NameStyle nameStyle) {
      String name;

      javax.persistence.Column columnAnno = f.getAnnotation(javax.persistence.Column.class);
      if (columnAnno == null || StrKit.isBlank(columnAnno.name())) {
        name = NameStyle.transform(pascalName, nameStyle);
      } else {
        name = columnAnno.name();
      }

      return name;
    }

    public String[] getShardKeys() {
      return this.shardKeys;
    }

    public Object[] getShardValues(Object obj) {
      if (this.shardKeys.length == 0) {
        throw new JsdException(
            "shard keys aren't specified for class: " + obj.getClass().getName());
      }

      Object[] values = new Object[shardKeys.length];
      for (int i = 0; i < shardKeys.length; i++) {
        values[i] = getValue(obj, shardKeys[i]);
      }
      return values;
    }

    public String[] getInsertColumns() {
      if (this.insertColumns == null) {
        List<String> columns = new ArrayList<>(fields.size());
        Iterator<Map.Entry<String, FieldInfo>> iter = fields.entrySet().iterator();
        while (iter.hasNext()) {
          Map.Entry<String, Mapper.EntityInfo.FieldInfo> fi = iter.next();
          if (!fi.getValue().auto) {
            columns.add(fi.getKey());
          }
        }
        this.insertColumns = columns.toArray(new String[0]);
      }
      return this.insertColumns;
    }

    public Object[] getInsertValues(Object obj) {
      String[] columns = this.getInsertColumns();
      Object[] values = new Object[columns.length];
      for (int i = 0; i < columns.length; i++) {
        values[i] = getValue(obj, columns[i]);
      }
      return values;
    }

    public String[] getUpdateColumns() {
      if (this.updateColumns == null) {
        List<String> columns = new ArrayList<>(fields.size());
        Iterator<Map.Entry<String, FieldInfo>> iter = fields.entrySet().iterator();
        while (iter.hasNext()) {
          Map.Entry<String, Mapper.EntityInfo.FieldInfo> fi = iter.next();
          if (!fi.getValue().key) {
            columns.add(fi.getKey());
          }
        }
        this.updateColumns = columns.toArray(new String[0]);
      }
      return this.updateColumns;
    }

    public Object[] getUpdateValues(Object obj) {
      String[] columns = this.getUpdateColumns();
      Object[] values = new Object[columns.length];
      for (int i = 0; i < columns.length; i++) {
        values[i] = getValue(obj, columns[i]);
      }
      return values;
    }

    public String[] getIdColumns() {
      return this.idColumns;
    }

    public Object[] getIdValues(Object obj) {
      String[] columns = this.getIdColumns();
      Object[] values = new Object[columns.length];
      for (int i = 0; i < columns.length; i++) {
        values[i] = getValue(obj, columns[i]);
      }
      return values;
    }

    public Object getValue(Object obj, String field) {
      FieldInfo fi = fields.get(field);
      if (fi == null) {
        return null;
      }

      return ConverterManager.j2d(method.invoke(obj, fi.getIndex));
    }

    /**
     * 设置对象的值
     *
     * @param field 字段名
     * @param value 值
     */
    public void setValue(Object obj, String field, Object value) {
      if (value == null) {
        return;
      }

      FieldInfo fi = fields.get(field);
      if (fi == null) {
        return;
      }

      method.invoke(obj, fi.setIndex, ConverterManager.d2j(fi.type, value));
    }

    private int getMethodIndex(String name) {
      String[] names = method.getMethodNames();
      for (int i = 0; i < names.length; ++i) {
        if (name.equals(names[i])) {
          return i;
        }
      }
      return -1;
    }

    static class FieldInfo {

      int getIndex;
      int setIndex;
      boolean key;
      boolean auto;
      Class<?> type;

      public FieldInfo(int getIndex, int setIndex, Class<?> type) {
        this.getIndex = getIndex;
        this.setIndex = setIndex;
        this.type = type;
      }
    }
  }

}
