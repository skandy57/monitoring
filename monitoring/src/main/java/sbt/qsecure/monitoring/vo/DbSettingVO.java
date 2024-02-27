package sbt.qsecure.monitoring.vo;

public record DbSettingVO(String aiHost, String instance, String ip, String indicator, String port, String encLogFile, String userId,
		String logFile, String db, String encLog, String maxPoolSize, String item, String log, String dbName,
		String password, String oldVersion, String difTab, String oldApi, String fixUser) {
}
