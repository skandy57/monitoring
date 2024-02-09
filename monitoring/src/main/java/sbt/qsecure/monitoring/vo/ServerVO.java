package sbt.qsecure.monitoring.vo;


import sbt.qsecure.monitoring.constant.Server;

public record ServerVO(
		long serverSequence,
		String company,
		String serverName,
		String host,
		String userId,
		String passwd,
		int port,
		String serverOs,
		Server.Type serverType,
		String version)
{}
