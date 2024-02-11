package sbt.qsecure.monitoring.vo;



import java.sql.Date;




public record MemberVO(
		long memberSequence,
		String company,
		String managerName, 
		String userId, 
		String passwd,
		String phoneNumber, 
		String email, 
		Date regDate, 
		String authGrade) 
{}
