package lucky.sky.task.data;

import java.util.List;


public class CreateRequest {

  public String Name;

  public String Alias;

  public String Note;

  public String Executer;

  public List<Trigger> Triggers;

  public List<Arg> Args;
}
