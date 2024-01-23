package sbt.qsecure.monitoring.vo;

import java.util.List;

import lombok.Data;

@Data
public class AiServerSettingVO {
	private String si;
	private String instance;
	private int connectionCount;
	private String sapHost;
	private String programId;
	private int sapPort;
	private String repositoryDestination;
	private int workerThreadCount;
	private String sapLanguage;
	private String rfcUser;
	private String client;
	private String systemNumber;
	private String passwd;
	private boolean isActivedEncLog;
	private String encLogDirectory;
	private int dbPort;
	private int maxPoolSize;
	private boolean isActivedEventLog;
	private String dbHost;
	private String indicater;
	private List<String> item;
	private String eventLogDirectory;

}
