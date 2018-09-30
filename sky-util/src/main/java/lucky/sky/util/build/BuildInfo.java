package lucky.sky.util.build;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;

/**
 * 软件构建信息
 */
@Getter
@Setter
public class BuildInfo {

  /**
   * 软件产品版本号
   */
  private String productVersion;
  /**
   * 源码管理版本号
   */
  private String scmRevision;
  /**
   * 源码管理分支
   */
  private String scmBranch;
  /**
   * 源码最后提交时间
   */
  private String scmTime;
  /**
   * 构建时间
   */
  private String buildAt;

  /**
   * 构建用户
   */
  private String buildBy;

  /**
   * 构建机器
   */
  private String buildOn;

  public static BuildInfo ofJson(String json) {
    return JSON.parseObject(json, BuildInfo.class);
  }

  /**
   * 以紧凑形式表示的字符串： productVersion(scmRevision/scmBranch)buildTime
   */
  @Override
  public String toString() {
    return String.format("%s.%s.%s.%s", productVersion, scmRevision, scmBranch, buildAt);
  }
}
