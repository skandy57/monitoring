package sbt.qsecure.monitoring.vo;

import lombok.Data;

@Data
public class TerminalVO {
	private String type;
	private String host;
	private int port;
	private String userId;
	private String passwd;
}
