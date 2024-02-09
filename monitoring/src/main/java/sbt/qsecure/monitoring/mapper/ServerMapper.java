package sbt.qsecure.monitoring.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestParam;

import sbt.qsecure.monitoring.constant.Server;
import sbt.qsecure.monitoring.vo.ServerVO;

@Mapper
public interface ServerMapper {

	public List<ServerVO> getServerList(Server.Type serverType);
	public ServerVO getServerOne(@Param("serverSequence")long serverSequence,@Param("serverType")Server.Type serverType);
	public int addServerInfo(ServerVO vo);
	public int updateServerInfo(ServerVO vo);
	public int deleteServerInfo(ServerVO vo);
}
