package sbt.qsecure.monitoring.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.json.simple.JSONObject;

import sbt.qsecure.monitoring.vo.ServerVO;

@Mapper
public interface LinuxMapper {
	
	public List<Map<String, Object>> getSshInformation(ServerVO vo);
	public String startCubeOneInstance(ServerVO vo);
	public String stopCubeOneInstance(ServerVO vo);
	public String startCubeOneModule(ServerVO vo);
	public String stopCubeOneModule(ServerVO vo);
	public JSONObject getCpuUsage(ServerVO vo, String command);
	public JSONObject getMemoryUsage(ServerVO vo, String command);
	public JSONObject getDiskUsage(ServerVO vo, String command);
	public JSONObject getProcess(ServerVO vo, String command);
	public JSONObject getEncLog(ServerVO vo, String command);
	public JSONObject getEventLog(ServerVO vo, String command);
	public JSONObject getSecurityServerSetting(ServerVO vo, String command);
	public JSONObject getSapDestination(ServerVO vo, String command);
	public JSONObject getJcoDestination(ServerVO vo, String command);
	public boolean isConnectedAiToSecurity(ServerVO vo, String command);
	public String getEventLogDirectory(ServerVO vo);
	public String setEventLogDirectory(ServerVO vo);
	public String getEventLog(ServerVO vo);
	public String getEncLog(ServerVO vo);
}
