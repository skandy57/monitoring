package sbt.qsecure.monitoring.vo;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sbt.qsecure.monitoring.checker.AIValidator;

public record DbSettingVO(String aiHost, String instance, String ip, String indicator, String port, String encLogFile, String userId, String logFile,
		String db, String encLog, String maxPoolSize, String item, String log, String dbName, String password, String oldVersion, String difTab,
		String oldApi, String fixUser) {

	public DbSettingVO(String aiHost, String instance, String ip, String indicator, String port, String encLogFile, String userId, String logFile,
			String db, String encLog, String maxPoolSize, String item, String log, String dbName, String password, String oldVersion, String difTab,
			String oldApi, String fixUser) {

		AIValidator.validateParameters(aiHost, instance, ip, indicator, port, encLogFile, userId, logFile, db, encLog, maxPoolSize, item, log, dbName, password);

		this.aiHost = aiHost;
		this.instance = instance;
		this.ip = ip;
		this.indicator = indicator;
		this.port = port;
		this.encLogFile = encLogFile;
		this.userId = userId;
		this.logFile = logFile;
		this.db = db;
		this.encLog = encLog;
		this.maxPoolSize = maxPoolSize;
		this.item = item;
		this.log = log;
		this.dbName = dbName;
		this.password = password;
		this.oldVersion = oldVersion;
		this.difTab = difTab;
		this.oldApi = oldApi;
		this.fixUser = fixUser;

	}

	

}
