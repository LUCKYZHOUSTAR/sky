package lucky.sky.util.log;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import lucky.sky.util.config.AppConfig;
import lucky.sky.util.io.Path;
import lucky.sky.util.lang.StrKit;
import org.apache.log4j.FileAppender;
import org.apache.log4j.helpers.CountingQuietWriter;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;


public class DailyFileAppender extends FileAppender {


  protected String dir;

  /**
   * 日志文件最大默认设置 100MB. The default maximum file size is 100MB.
   */
  protected long maxFileSize = 100 * 1024 * 1024L;

  /**
   * 日志备份从1开始
   */
  protected int index = 1;

  public String getDir() {
    return dir;
  }

  public void setDir(String dir) {
    this.dir = dir;
  }

  protected String logName;

  /**
   * 日志路径中对日期的格式化方式 2015-05-18 D:\log\2015-05-18\error.log
   */
  private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

  /**
   * 用来记录下次需要新生成日志文件夹的时间点
   */
  private long nextCheck = System.currentTimeMillis() - 1;

  /**
   * 主要是为了不用每次new Date
   */
  Date now = new Date();

  /**
   * 主要是为了将日期+1，仿照原有实现可优化
   */
  MCalendar rc = new MCalendar();

  public DailyFileAppender() {
    // default ctor
  }

  @Override
  public void activateOptions() {
    logName = fileName;
    fileName = this.getFileName();
    if (fileName != null) {
      super.activateOptions();
    }
  }

  @Override
  protected boolean checkEntryConditions() {
    return (dir == null) || super.checkEntryConditions();
  }

  /**
   * 获得当前时刻使用的日志的绝对路径 为减少配置文件，不对新增属性logName增加显示注入方法，而是初始为File
   */
  private String getFileName() {
    if (null == logName) {
      return null;
    }

    dir = getFileDir();
    if (dir == null) {
      return null;
    }
    return dir + File.separator + sdf.format(now) + File.separator + logName;

  }

  private String getFileDir() {
    String path;
    if (StrKit.isBlank(this.dir)) {
      String logPath = AppConfig.getDefault().getGlobal().getLogPath();
      if (StrKit.isBlank(logPath)) {
        ConsoleLogger.warn(
            "log > [DailyFileAppender] can't find `log.path` in global config, skip this appender");
        return null;
      }

      String appName = AppConfig.getDefault().getAppName();
      if (StrKit.isBlank(appName)) {
        ConsoleLogger.warn(
            "log > [DailyFileAppender] can't find `app_name` in app config, skip this appender");
        return null;
      }
      path = Path.join(logPath, appName);
    } else {
      path = this.dir;
    }

    return path;
  }


  /**
   * log4j中写日志的核心部分 这里增加了当前时刻是否需要更换日期路径的判断和调用
   */
  @Override
  protected void subAppend(LoggingEvent event) {
    // 如果未正确设置日志目录则跳过
    if (dir == null) {
      return;
    }

    long n = System.currentTimeMillis();
    if (n >= nextCheck) {
      now.setTime(n);
      nextCheck = rc.getNextCheckMillis(now);
      rollOverDate();
    }
    super.subAppend(event);

    if (fileName != null && qw != null) {
      long size = ((CountingQuietWriter) qw).getCount();
      if (size >= maxFileSize) {
        rollOverSize();
      }
    }
  }

  @Override
  protected void setQWForFiles(Writer writer) {
    this.qw = new CountingQuietWriter(writer, errorHandler);
  }

  /**
   * 根据日期变化，更改日志文件的方法
   */
  void rollOverDate() {
    String newFilename = getFileName();
    if (newFilename.equals(fileName)) {
      return;
    }
    this.closeFile();
    try {
      this.setFile(newFilename, true, this.bufferedIO, this.bufferSize);
      index = 1;
    } catch (InterruptedIOException e) {
      Thread.currentThread().interrupt();
      LogLog.error("rollOver() failed.", e);
    } catch (IOException e) {
      LogLog.error("rollOver() failed.", e);
    }
  }

  /**
   * 根据日志文件大小更改日志文件的方法
   */
  public void rollOverSize() {
    // Rename fileName to fileName.index
    File target = new File(fileName + "." + index);
    if (target.exists()) {
      target.delete();
    }
    this.closeFile(); // keep windows happy.
    File file = new File(fileName);
    LogLog.debug("Renaming file " + file + " to " + target);
    file.renameTo(target);
    try {
      this.setFile(fileName, true, this.bufferedIO, this.bufferSize);
      index++;
    } catch (InterruptedIOException e) {
      Thread.currentThread().interrupt();
      LogLog.error("setFile(" + fileName + ", false) call failed.", e);
    } catch (IOException e) {
      LogLog.error("setFile(" + fileName + ", false) call failed.", e);
    }
  }


  static class MCalendar extends GregorianCalendar {

    private static final long serialVersionUID = 1L;

    MCalendar() {
      super();
    }

    MCalendar(TimeZone tz, Locale locale) {
      super(tz, locale);
    }

    public long getNextCheckMillis(Date now) {
      return getNextCheckDate(now).getTime();
    }

    public Date getNextCheckDate(Date now) {
      this.setTime(now);
      this.set(Calendar.HOUR_OF_DAY, 0);
      this.set(Calendar.MINUTE, 0);
      this.set(Calendar.SECOND, 0);
      this.set(Calendar.MILLISECOND, 0);
      this.add(Calendar.DATE, 1);
      return getTime();
    }
  }

}
