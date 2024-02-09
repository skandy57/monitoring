package sbt.qsecure.monitoring.vo;

import java.util.Date;

import lombok.Setter;


public record MemberVO(
		Long memberSequence,
		String company,
		String managerName, 
		String userId, 
		String passwd,
		String phoneNumber, 
		String email, 
		Date regDate, 
		String authGrade) 
{}
