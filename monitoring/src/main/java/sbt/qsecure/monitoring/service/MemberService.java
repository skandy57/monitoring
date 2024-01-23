package sbt.qsecure.monitoring.service;

import org.springframework.stereotype.Service;

import sbt.qsecure.monitoring.vo.MemberVO;


@Service
public interface MemberService {

	public MemberVO login(String userId, String passwd);
	public MemberVO loginTest(String userId, String passwd);
	public MemberVO regMember(MemberVO dto);
	public MemberVO getMemberById(String userId);
	public MemberVO updateMember(MemberVO vo);
	public int deleteMember(MemberVO vo) throws Exception;
//	public int updateAuth(MemberVO vo, String targetMember, String auth);
	public boolean isAdmin(MemberVO vo);
	
}
