package lucky.sky.util.log;

import java.util.function.Consumer;
import java.util.function.Supplier;


public interface Logger {

  /**
   * write trace log of 'entry'
   */
  void entry();

  /**
   * write trace log of 'exit'
   */
  void exit();

  String getName();

  boolean isTraceEnabled();

  void trace(String msg);

  void trace(Supplier<String> msg);

  void trace(boolean condition, Supplier<String> msg);

  void trace(String format, Object arg);

  void trace(String format, Object arg1, Object arg2);

  void trace(String format, Object... args);

  void trace(String msg, Throwable t);

  boolean isDebugEnabled();

  void debug(String msg);

  void debug(Supplier<String> msg);

  void debug(boolean condition, Supplier<String> msg);

  void debug(String format, Object arg);

  void debug(String format, Object arg1, Object arg2);

  void debug(String format, Object... args);

  void debug(String msg, Throwable t);

  boolean isInfoEnabled();

  void info(String msg);

  void info(boolean condition, Supplier<String> msg);

  void info(String format, Object arg);

  void info(String format, Object arg1, Object arg2);

  void info(String format, Object... args);

  void info(String msg, Throwable t);

  boolean isWarnEnabled();

  void warn(String msg);

  void warn(boolean condition, Supplier<String> msg);

  void warn(String format, Object arg);

  void warn(String format, Object... args);

  void warn(String format, Object arg1, Object arg2);

  void warn(String msg, Throwable t);

  boolean isErrorEnabled();

  void error(String msg);

  void error(boolean condition, Supplier<String> msg);

  void error(String format, Object arg);

  void error(String format, Object arg1, Object arg2);

  void error(String format, Object... args);

  void error(String msg, Throwable t);

  class LoggerImp implements Logger {

    private org.slf4j.Logger logger;

    public LoggerImp(org.slf4j.Logger logger) {
      this.logger = logger;
    }

    @Override
    public void entry() {
      this.logger.trace("entry");
    }

    @Override
    public void exit() {
      this.logger.trace("exit");
    }

    @Override
    public String getName() {
      return this.logger.getName();
    }

    @Override
    public boolean isTraceEnabled() {
      return this.logger.isTraceEnabled();
    }

    @Override
    public void trace(String msg) {
      this.logger.trace(msg);
    }

    @Override
    public void trace(Supplier<String> msg) {
      if (logger.isTraceEnabled()) {
        write(msg, this.logger::trace);
      }
    }

    @Override
    public void trace(boolean condition, Supplier<String> msg) {
      if (condition) {
        write(msg, this.logger::trace);
      }
    }

    @Override
    public void trace(String format, Object arg) {
      this.logger.trace(format, arg);
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
      this.logger.trace(format, arg1, arg2);
    }

    @Override
    public void trace(String format, Object... args) {
      this.logger.trace(format, args);
    }

    @Override
    public void trace(String msg, Throwable t) {
      this.logger.trace(msg, t);
    }

    @Override
    public boolean isDebugEnabled() {
      return this.logger.isDebugEnabled();
    }

    @Override
    public void debug(String msg) {
      this.logger.debug(msg);
    }

    @Override
    public void debug(Supplier<String> msg) {
      if (this.isDebugEnabled()) {
        write(msg, this.logger::debug);
      }
    }

    @Override
    public void debug(boolean condition, Supplier<String> msg) {
      if (condition) {
        write(msg, this.logger::debug);
      }
    }

    @Override
    public void debug(String format, Object arg) {
      this.logger.debug(format, arg);
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
      this.logger.debug(format, arg1, arg2);
    }

    @Override
    public void debug(String format, Object... args) {
      this.logger.debug(format, args);
    }

    @Override
    public void debug(String msg, Throwable t) {
      this.logger.debug(msg, t);
    }

    @Override
    public boolean isInfoEnabled() {
      return this.logger.isInfoEnabled();
    }

    @Override
    public void info(String msg) {
      this.logger.info(msg);
    }

    @Override
    public void info(boolean condition, Supplier<String> msg) {
      if (condition) {
        write(msg, this.logger::info);
      }
    }

    @Override
    public void info(String format, Object arg) {
      this.logger.info(format, arg);
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
      this.logger.info(format, arg1, arg2);
    }

    @Override
    public void info(String format, Object... args) {
      this.logger.info(format, args);
    }

    @Override
    public void info(String msg, Throwable t) {
      this.logger.info(msg, t);
    }

    @Override
    public boolean isWarnEnabled() {
      return this.logger.isWarnEnabled();
    }

    @Override
    public void warn(String msg) {
      this.logger.warn(msg);
    }

    @Override
    public void warn(boolean condition, Supplier<String> msg) {
      if (condition) {
        write(msg, this.logger::warn);
      }
    }

    @Override
    public void warn(String format, Object arg) {
      this.logger.warn(format, arg);
    }

    @Override
    public void warn(String format, Object... args) {
      this.logger.warn(format, args);
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
      this.logger.warn(format, arg1, arg2);
    }

    @Override
    public void warn(String msg, Throwable t) {
      this.logger.warn(msg, t);
    }

    @Override
    public boolean isErrorEnabled() {
      return this.logger.isErrorEnabled();
    }

    @Override
    public void error(String msg) {
      this.logger.error(msg);
    }

    @Override
    public void error(boolean condition, Supplier<String> msg) {
      if (condition) {
        write(msg, this.logger::error);
      }
    }

    @Override
    public void error(String format, Object arg) {
      this.logger.error(format, arg);
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
      this.logger.error(format, arg1, arg2);
    }

    @Override
    public void error(String format, Object... args) {
      this.logger.error(format, args);
    }

    @Override
    public void error(String msg, Throwable t) {
      this.logger.error(msg, t);
    }

    private void write(Supplier<String> supplier, Consumer<String> writer) {
      try {
        writer.accept(supplier.get());
      } catch (Exception ex) {
        this.logger.warn("fail to supplier.get log message: ", ex);
      }
    }
  }
}
