package lucky.sky.task.data;

import lombok.Getter;
import lombok.Setter;
import lucky.sky.util.lang.EnumValueSupport;

import java.util.List;


@Getter
@Setter
public class ExecuteParam {

  private ExecuteType type;

  private String id;

  private String name;

  private String alias;

  private List<Arg> args;

  public enum ExecuteType implements EnumValueSupport {
    AUTO(0), MANUAL(1);

    private int value;

    ExecuteType(int value) {
      this.value = value;
    }

    @Override
    public int value() {
      return this.value;
    }
  }
}

