package sbt.qsecure.monitoring.os;

import sbt.qsecure.monitoring.constant.Server;

public interface OSConnector {
	
	 String sendCommand(String command)throws Exception;
	 boolean isConnected();
	 Server.OS getOSType();
}
