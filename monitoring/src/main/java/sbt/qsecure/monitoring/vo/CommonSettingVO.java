package sbt.qsecure.monitoring.vo;

public record CommonSettingVO(
		Long sequence, 
		String eventLogDirectory, 
		String encLogDirectory, 
		String sid) {

}
