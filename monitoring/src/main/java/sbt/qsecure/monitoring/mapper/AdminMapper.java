package sbt.qsecure.monitoring.mapper;

import org.apache.ibatis.annotations.Mapper;

import sbt.qsecure.monitoring.vo.MemberVO;
import sbt.qsecure.monitoring.vo.ServerVO;

@Mapper
public interface AdminMapper {
	
	public MemberVO addMemberByAdmin(MemberVO vo);
	public MemberVO updateAuth(String targetMember, String auth);
	public MemberVO updateMemberByAdmin(Long memberSequence, MemberVO vo);
	public MemberVO deleteMemberByAdmin(MemberVO vo);
	public ServerVO addAIServer(ServerVO vo);
	public ServerVO deleteAIServer(ServerVO vo);
	public ServerVO updateAIServer(ServerVO vo);

}
