package sbt.qsecure.monitoring.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.json.simple.JSONObject;

import sbt.qsecure.monitoring.vo.CommandVO;
import sbt.qsecure.monitoring.vo.ServerVO;

@Mapper
public interface CommandMapper {
	public String getCommandServerDetailInformation();
	public String getCommandstartCubeOneInstance(String instance);
	public String getCommandstopCubeOneInstance(String instance);
	public String getCommandstartCubeOneModule(String instance);
	public String getCommandstopCubeOneModule(String instance);
//	public String getTop(ServerVO vo, String alias);
	public List<CommandVO> getAllCommand();
	public String getCommandCpuUsage();
	public String getCommandMemoryUsage();
	public String getCommandDiskUsage();
	public String getCommandProcess();
	public String getCommandReadEncLog();
	public String getCommandReadEventLog();
}
