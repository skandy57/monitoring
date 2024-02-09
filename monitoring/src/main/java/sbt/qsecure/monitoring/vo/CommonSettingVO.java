package sbt.qsecure.monitoring.vo;

public record CommonSettingVO(
		long sequence,
		String eventLogDirectory, 
		String encLogDirectory, 
		String sid,
		String system
		) {}
