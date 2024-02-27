package sbt.qsecure.monitoring.vo;

public record JcoSettingVO(String aiHost, String instance, String connectionCount, String gwHost, String progId, String gwPort,
		String repositoryDestination, String workerThreadCount) {

}
