package sbt.qsecure.monitoring.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Server {
	AI("A/I Server"),
	SECURITY("Security Server"),
	MANAGER("Manager Server");
	
	private final String type;
	

}
