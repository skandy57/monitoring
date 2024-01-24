package sbt.qsecure.monitoring.service;

import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import sbt.qsecure.monitoring.constant.Result;
import sbt.qsecure.monitoring.constant.Server;
import sbt.qsecure.monitoring.vo.AiServerSettingVO;
import sbt.qsecure.monitoring.vo.CommandVO;
import sbt.qsecure.monitoring.vo.ConvExitVO;
import sbt.qsecure.monitoring.vo.MemberVO;
import sbt.qsecure.monitoring.vo.ServerVO;

@Service
public interface ServerService {
	
	public List<ServerVO>getServerList(Server serverType);
	public int updateServerInfo(ServerVO vo, MemberVO member);
	public int addServerInfo(ServerVO server, MemberVO member);
	public int deleteServerInfo(ServerVO server, MemberVO member);
	public JSONObject getServerDetailInfo(ServerVO vo);
	public JSONObject getCubeOneInstanceInfo(ServerVO vo, AiServerSettingVO ai);
	public JSONObject updateCubeOneInstanceInfo(ServerVO server, MemberVO member, AiServerSettingVO ai);
	public Result startCubeOneInstance(ServerVO server, MemberVO member, AiServerSettingVO ai);
	public Result stopCubeOneInstance(ServerVO server, MemberVO member, AiServerSettingVO ai);
	public Result startCubeOneModule(ServerVO server, MemberVO member, AiServerSettingVO ai);
	public Result stopCubeOneModule(ServerVO server, MemberVO member, AiServerSettingVO ai);
	public Result cotest(ServerVO server, String instance); 
	public JSONObject getTop();
	public List<CommandVO> getAllCommand(ServerVO vos);
	public JSONObject getCpuUsage(ServerVO vo);
	public JSONObject getMemoryUsage(ServerVO vo);
	public JSONObject getDiskUsage(ServerVO vo);
	public JSONObject getProcess(ServerVO vo);
	public JSONObject readEncLog(ServerVO vo, String directory, String date, String sid, String conv);
	public JSONObject readEventLog(ServerVO vo, String directory);

	public String getCountDecError(ServerVO server, String directory, String date, String sid, String conv);
	public String getCountEncError(ServerVO server, String directory, String date, String sid, String conv);
}
