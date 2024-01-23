package sbt.qsecure.monitoring.os;

import sbt.qsecure.monitoring.constant.OperationSystem;

public interface OSConnector {
	
	 String sendCommand(String command)throws Exception;
	 OperationSystem getOSType();
}
