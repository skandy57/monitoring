package sbt.qsecure.monitoring.vo;

import lombok.Data;

public record SapVO(
		Long serverSequence, 
		String company, 
		String serverDescription, 
		String RFCdestination, 
		String userId,
		String passwd, 
		String client, 
		String ashost, 
		String systemNumber
		) 
{}
