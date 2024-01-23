package sbt.qsecure.monitoring.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import sbt.qsecure.monitoring.vo.MemberVO;

@Mapper
public interface MemberMapper {
	
	public MemberVO login(@Param("userId")String userId, @Param("passwd")String passwd);
	public MemberVO updateMember(MemberVO vo);
	public void deleteMember(MemberVO vo);
//	public MemberVO updateAuth(String targetMember, String auth);
	public MemberVO getMemberById(@Param("userId")String userId);
	public MemberVO loginTest(@Param("userId")String userId, @Param("passwd")String passwd);

}
