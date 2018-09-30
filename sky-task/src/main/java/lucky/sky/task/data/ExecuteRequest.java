package lucky.sky.task.data;

import java.time.LocalDateTime;
import java.util.List;


public class ExecuteRequest {

  public String Name;

  public List<Arg> Args;

  public LocalDateTime Time;

  public static class Arg {

    public String Name;
    public String Value;
  }
}

