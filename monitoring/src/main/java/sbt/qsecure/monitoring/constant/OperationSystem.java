package sbt.qsecure.monitoring.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OperationSystem {
	
	WINDOWS("Windows"),
	LINUX("Linux");
	
	private final String os;
	


}
