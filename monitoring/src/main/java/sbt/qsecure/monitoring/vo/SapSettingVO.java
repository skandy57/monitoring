package sbt.qsecure.monitoring.vo;

public record SapSettingVO(String aiHost, String instance, String lang, String client, String peakLimit, String passwd, String user,
		String sysnr, String poolCapacity, String asHost) {

}
