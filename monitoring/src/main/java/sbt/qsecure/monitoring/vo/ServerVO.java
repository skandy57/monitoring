package sbt.qsecure.monitoring.vo;

import sbt.qsecure.monitoring.constant.Server.Type;

public record ServerVO(
		String host,
		long serverSequence,
		String company,
		String serverName,
		String userId,
		String passwd,
		int port,
		String serverOs,
		Type serverType,
		String version,
		String coHome
)
{}
