package lucky.sky.db.mongo;

import lucky.sky.util.convert.MethodNameConverter;
import lucky.sky.util.log.Logger;
import lucky.sky.util.log.LoggerManager;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lucky.sky.util.reflect.MethodAccess;


public final class ObjectMethodCache {

  private static final Logger log = LoggerManager.getLogger(ObjectMethodCache.class);

  private static HashMap<String, ObjectMethodUtil> objectMethodUtils = new HashMap<>();

  public ObjectMethodCache() {
  }

  @SuppressWarnings("rawtypes")
  public synchronized static ObjectMethodUtil getObjectMethodUtil(Class clazz) {
    ObjectMethodUtil objectMethodUtil = objectMethodUtils.get(clazz.getName());
    if (objectMethodUtil != null) {
      return objectMethodUtil;
    }
    objectMethodUtil = new ObjectMethodUtil(clazz);
    objectMethodUtils.put(clazz.getName(), objectMethodUtil);
    return objectMethodUtil;
  }

  /**
   * 重构提取出反射公共类，将对反射机制的操作全部封装，减少了和调用类的耦合
   *
   * @author wei.li3 <p> 2015年5月11日
   */
  static class ObjectMethodUtil {

    private MethodAccess methodAccess;
    private Map<String, FieldInfo> fieldInfos = new HashMap<String, FieldInfo>();

    @SuppressWarnings("rawtypes")
    public ObjectMethodUtil(Class clazz) {
      methodAccess = MethodAccess.get(clazz);
      initField(clazz);
    }

    @SuppressWarnings("rawtypes")
    private void initField(Class clazz) {
      Field[] fields = clazz.getDeclaredFields();
      Bson ann = null;
      String key = null;
      for (Field field : fields) {
        ann = field.getAnnotation(Bson.class);
        if (null != ann) {
          key = field.getName();
          if (ann.value().length() > 0) {
            key = ann.value();
          }
          //需要特別注意，目前默认属性的getset方法全部遵照注解规范
          boolean isBoolean = (field.getType() == boolean.class);
          int getIndex = getMethodIndexOfGet(field.getName(), isBoolean);
          int setIndex = getMethodIndexOfSet(field.getName(), isBoolean);
          if (getIndex == -1 || setIndex == -1) {
            continue;
          }
          FieldInfo fi = new FieldInfo(getIndex, setIndex, getFieldClass(field));
          this.fieldInfos.put(key, fi);
        }
      }
    }

    private int getMethodIndexOfGet(String name, boolean isBoolean) {
      return getIndex(MethodNameConverter.toGetterName(name, isBoolean));
    }

    private int getMethodIndexOfSet(String name, boolean isBoolean) {
      return getIndex(MethodNameConverter.toSetterName(name, isBoolean));
    }

    private int getIndex(String methodName) {
      String[] names = methodAccess.getMethodNames();
      for (int i = 0; i < names.length; ++i) {
        if (methodName.equals(names[i])) {
          return i;
        }
      }
      return -1;
    }

    /**
     * 取值
     */
    public Object getBeanValue(Object obj, String key) {
      FieldInfo fi = this.fieldInfos.get(key);
      if (null == fi) {
        return null;
      }
      return methodAccess.invoke(obj, fi.getIndex);
    }

    /**
     * 赋值
     */
    public void setBeanValue(Object obj, String key, Object value) {
      if (null == obj) {
        return;
      }
      FieldInfo fi = this.fieldInfos.get(key);
      if (null == fi) {
        return;
      }
      try {
        methodAccess.invoke(obj, fi.setIndex, value);
      } catch (Exception e) {
        log.error("mongo to bean error class:{}, key:{}, value:{}, error:{}",
            obj.getClass().getName(), key, value, e);
      }
    }


    /**
     * 获取需要转换成mongodb的key-value集合
     */
    public Map<String, Object> getObjectKeyValue(Object obj) {
      Map<String, Object> map = new HashMap<String, Object>();
      for (String key : fieldInfos.keySet()) {
        map.put(key, getBeanValue(obj, key));
      }
      return map;
    }

    private Class<?> getFieldClass(Field field) {
      if (null != field) {
        Class<?> fieldType = field.getType();
        if (fieldType.isAnnotationPresent(Bson.class)) {
          return fieldType;
        }
        if (fieldType.isAssignableFrom(List.class)) {
          Type fc = field.getGenericType();
          if (fc != null && fc instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) fc;
            return (Class<?>) pt.getActualTypeArguments()[0];
          }
        }
      }
      return null;
    }

    /**
     * @param key
     * @return
     */
    public Class<?> getClass(String key) {
      FieldInfo fi = this.fieldInfos.get(key);
      if (null == fi) {
        return null;
      }
      return fi.clazz;
    }

    static class FieldInfo {

      int getIndex;
      int setIndex;
      Class<?> clazz;

      public FieldInfo(int getIndex, int setIndex, Class<?> clazz) {
        this.getIndex = getIndex;
        this.setIndex = setIndex;
        this.clazz = clazz;
      }
    }
  }
}
