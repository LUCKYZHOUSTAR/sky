package lucky.sky.task;

import lucky.sky.task.data.ExecuteParam;
import lucky.sky.task.data.Arg;
import lucky.sky.util.config.SettingMap;
import lombok.Getter;

import java.util.List;

/**
 *
 */
@Getter
public class TaskContext {

  private String name;
  private String id;
  private String alias;
  private SettingMap args = new SettingMap();

  public TaskContext(ExecuteParam param) {
    this.name = param.getName();
    this.id = param.getId();
    this.alias = param.getAlias();
    List<Arg> list = param.getArgs();
    if (list != null && !list.isEmpty()) {
      list.forEach(arg -> this.args.put(arg.Name, arg.Value));
    }
  }
}
