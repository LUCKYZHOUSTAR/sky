package lucky.sky.task.data;


public class Result {

  public boolean Success;

  public String ErrorInfo;

  public Result() {
    // for encode/decode
  }

  public Result(boolean success, String error) {
    this.Success = success;
    this.ErrorInfo = error;
  }
}

