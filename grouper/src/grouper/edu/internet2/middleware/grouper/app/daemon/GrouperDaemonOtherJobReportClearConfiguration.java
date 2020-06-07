package edu.internet2.middleware.grouper.app.daemon;

import edu.internet2.middleware.grouper.app.loader.GrouperLoaderType;
import edu.internet2.middleware.grouper.cfg.dbConfig.ConfigFileName;

public class GrouperDaemonOtherJobReportClearConfiguration extends GrouperDaemonConfiguration {

  @Override
  public ConfigFileName getConfigFileName() {
    return ConfigFileName.GROUPER_LOADER_PROPERTIES;
  }

  //  # reports clear Job class
  //  # {valueType: "class", mustExtendClass: "edu.internet2.middleware.grouper.app.loader.OtherJobBase", mustImplementInterface: "org.quartz.Job"}
  //  otherJob.grouperReportClearDaemon.class = edu.internet2.middleware.grouper.app.reports.GrouperReportClearJob
  //
  //  # reports clear Job cron
  //  # {valueType: "string"}
  //  otherJob.grouperReportClearDaemon.quartzCron = 0 0 3 * * ?
  //

  @Override
  public String getConfigIdRegex() {
    return "^(otherJob\\.grouperReportClearDaemon)\\.(.*)$";
  }

  @Override
  public String getConfigItemPrefix() {
    return "otherJob.grouperReportClearDaemon.";
  }

  @Override
  public boolean isMultiple() {
    return false;
  }
  
  @Override
  public String getDaemonJobPrefix() {
    return GrouperLoaderType.GROUPER_OTHER_JOB_PREFIX;
  }

  @Override
  public boolean matchesQuartzJobName(String jobName) {
    return "OTHER_JOB_grouperReportClearDaemon".equals(jobName);
  }
  
  

}
