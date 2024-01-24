package sbt.qsecure.monitoring.constant;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public enum Result {

	SUCCESS,
	ERR_SAP,
	ERR_AIDB,
	ERR_MODULE,
	ERR_UNKNOWN_HOSTKEY,
	ERR_INVALID_PRIVATEKEY,
	ERR_AUTH_CANCEL,
	ERR_BUFFER,
	ERR_SESSION_REUSE,
	ERR_CHANNEL,
	ERR_RUNTIME,
	ERR_IDFILE,
	ERR_PARTIAL_AUTH,
	ERR_PROXYHTTP,
	ERR_PROXYSOCK,
	ERR_IO,
	ERR_NULLPOINTER,
	ERR_CUBEONE_INSTANCE,
	ERR_CUBEONE_MODULE,
	NO_AUTH
	
}
