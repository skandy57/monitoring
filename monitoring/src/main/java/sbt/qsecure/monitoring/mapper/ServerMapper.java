package sbt.qsecure.monitoring.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestParam;

import sbt.qsecure.monitoring.constant.Server;
import sbt.qsecure.monitoring.vo.ServerVO;

@Mapper
public interface ServerMapper {

	public List<ServerVO> getServerList(Server serverType);
	public List<ServerVO> insertServerInfo(ServerVO vo);
	public int updateServerInfo(ServerVO vo);
	public int deleteServerInfo(ServerVO vo);
}
