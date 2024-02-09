package sbt.qsecure.monitoring.service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import sbt.qsecure.monitoring.vo.AiServerSettingVO;
import sbt.qsecure.monitoring.constant.Server;
import sbt.qsecure.monitoring.vo.CommandVO;
import sbt.qsecure.monitoring.vo.CommonSettingVO;
import sbt.qsecure.monitoring.vo.ConvExitVO;
import sbt.qsecure.monitoring.vo.MemberVO;
import sbt.qsecure.monitoring.vo.ServerVO;

@Service
public interface ServerService {

	public List<ServerVO> getServerList(Server.Type serverType);

	public ServerVO getServerOne(long serverSequence, Server.Type serverType);

	public int updateServerInfo(ServerVO vo);

	public int addServerInfo(ServerVO server);

	public int deleteServerInfo(ServerVO server);

	public JSONObject getServerDetailInfo(ServerVO vo);

	public JSONObject getCubeOneInstanceInfo(ServerVO vo, AiServerSettingVO ai);

	public JSONObject updateCubeOneInstanceInfo(ServerVO server, MemberVO member, AiServerSettingVO ai);

	public Server.Module startCubeOneInstance(ServerVO server, MemberVO member, AiServerSettingVO ai);

	public Server.Module stopCubeOneInstance(ServerVO server, MemberVO member, AiServerSettingVO ai);

	public Server.Module startCubeOneModule(ServerVO server, MemberVO member);

	public Server.Module stopCubeOneModule(ServerVO server, MemberVO member);

	public Server.Module cotest(ServerVO server, String instance);

	public String getTop();

	public Server.Command.Linux[] getAllCommand(ServerVO vos);

	public double getCpuUsage(ServerVO vo);

	public double getMemoryUsage(ServerVO vo);

	public double getDiskUsage(ServerVO vo);

	public List<Map<String, String>> getProcess(ServerVO vo, String sortType);

	public JSONObject readEncLog(ServerVO vo, String directory, String date, String sid, String conv);

	public JSONObject readEventLog(ServerVO vo, String directory);

	public String getCountDecError(ServerVO server, String directory, String date, String sid);

	public String getCountEncError(ServerVO server, String directory, String date, String sid);
}
