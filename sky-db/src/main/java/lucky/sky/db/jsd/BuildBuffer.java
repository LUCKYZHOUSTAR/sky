package lucky.sky.db.jsd;

import lucky.sky.db.jsd.converter.ConverterManager;
import lucky.sky.db.jsd.result.BuildResult;

import java.util.ArrayList;
import java.util.List;

/**
 * .
 */
public final class BuildBuffer {

  private List<Object> args;
  private StringBuilder builder = new StringBuilder();

  String getSql() {
    return builder.toString();
  }

  BuildBuffer addSql(String sql) {
    builder.append(sql);
    return this;
  }

  BuildBuffer addSql(String fmt, Object... args) {
    builder.append(String.format(fmt, args));
    return this;
  }

  BuildBuffer addSql(int repeat, String str, String delimiter) {
    for (int i = 0; i < repeat; i++) {
      if (i > 0) {
        builder.append(delimiter);
      }
      builder.append(str);
    }
    return this;
  }

//    BuildBuffer insertSql(int index, String sql) {
//        builder.insert(index, sql);
//        return this;
//    }

  BuildBuffer addArg(Object arg) {
    if (args == null) {
      args = new ArrayList<>();
    }
    args.add(ConverterManager.j2d(arg));
    return this;
  }

  BuildBuffer addArg(Object... args) {
    if (this.args == null) {
      this.args = new ArrayList<>();
    }
    if (args != null && args.length > 0) {
      for (Object arg : args) {
        this.args.add(ConverterManager.j2d(arg));
      }
    }
    return this;
  }

  BuildBuffer addArg(List<Object> args) {
    if (this.args == null) {
      this.args = new ArrayList<>();
    }
    if (args != null && !args.isEmpty()) {
      for (Object arg : args) {
        this.args.add(ConverterManager.j2d(arg));
      }
    }
    return this;
  }

  BuildResult getResult() {
    if (builder.length() == 0) {
      return null;
    }
    return new BuildResult(builder.toString(), args);
  }
}
