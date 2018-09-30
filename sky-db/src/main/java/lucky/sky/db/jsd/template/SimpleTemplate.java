package lucky.sky.db.jsd.template;

import lucky.sky.db.jsd.result.BuildResult;
import lucky.sky.db.jsd.Mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/11/18.
 */
class SimpleTemplate implements SqlTemplate {

  private String sql;
  private String[] args;

  public SimpleTemplate(String tpl) {
    StringBuilder sb = new StringBuilder();
    int start = -1;
    List<String> args = new ArrayList<>();
    for (int i = 0; i < tpl.length(); i++) {
      char c = tpl.charAt(i);
      switch (c) {
        case '{':
          if (start == -1) {
            start = i;
          } else {
            sb.append(c);
          }
          break;
        case '}':
          if (start == -1) {
            sb.append(c);
          } else {
            String arg = tpl.substring(start + 1, i);
            args.add(arg);
            sb.append('?');
            start = -1;
          }
          break;
        default:
          if (start == -1) {
            sb.append(c);
          }
          break;
      }
    }

    this.sql = sb.toString();
    this.args = args.toArray(new String[0]);
  }

  @Override
  public BuildResult execute(Object obj) {
    List<Object> args;
    if (obj instanceof Map) {
      args = executeByMap((Map) obj);
    } else {
      Mapper.EntityInfo info = Mapper.getEntityInfo(obj.getClass());
      args = executeByEntity(info, obj);
    }

    return new BuildResult(sql, args);
  }

  private List<Object> executeByMap(Map obj) {
    List<Object> list = new ArrayList<>(args.length);
    for (String arg : args) {
      list.add(obj.get(arg));
    }
    return list;
  }

  private List<Object> executeByEntity(Mapper.EntityInfo info, Object obj) {
    List<Object> list = new ArrayList<>(args.length);
    for (String arg : args) {
      list.add(info.getValue(obj, arg));
    }
    return list;
  }
}
