package sbt.qsecure.monitoring.vo;

import lombok.Data;

@Data
public class CommandVO {
	private Long commandSequence;
	private String os;
	private String alias;
	private String command;
	private String description;
}
