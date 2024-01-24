package sbt.qsecure.monitoring.vo;


import sbt.qsecure.monitoring.constant.Server;


public record ServerVO(
		Long serverSequence,
		String company,
		String serverName,
		String host,
		String userId,
		String passwd,
		int port,
		String serverOs,
		Server serverType,
		String version)
{}
