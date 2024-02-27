package sbt.qsecure.monitoring.service;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.annotations.Param;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import sbt.qsecure.monitoring.constant.Server;
import sbt.qsecure.monitoring.constant.Server.Command.Linux;
import sbt.qsecure.monitoring.constant.Server.Module;
import sbt.qsecure.monitoring.constant.Server.Type;
import sbt.qsecure.monitoring.constant.Server.Version;
import sbt.qsecure.monitoring.vo.DbSettingVO;
import sbt.qsecure.monitoring.vo.InstanceVO;
import sbt.qsecure.monitoring.vo.JcoSettingVO;
import sbt.qsecure.monitoring.vo.SapSettingVO;
import sbt.qsecure.monitoring.vo.CommandVO;
import sbt.qsecure.monitoring.vo.ConvExitVO;
import sbt.qsecure.monitoring.vo.MemberVO;
import sbt.qsecure.monitoring.vo.ProcessVO;
import sbt.qsecure.monitoring.vo.ServerVO;

@Service
public interface ServerService {

	public List<ServerVO> getServerList(Type serverType);

	public ServerVO getServerOne(long serverSequence, Type serverType);

	public int updateServerInfo(ServerVO server);

	public int insertServerInfo(ServerVO server);

	public int deleteServerInfo(ServerVO server);

	public int createAllTable();

//	public String createServerTable();
//
//	public String createJcoTable();
//
//	public String createDbTable();
//
//	public String createSapTable();
//
//	public String createInstanceTable();

	public JSONObject getServerDetailInfo(ServerVO server);

	public Set<InstanceVO> getInstanceListToServer(ServerVO server);
	
	public List<InstanceVO> getInstanceListToDB(String host);

	public JcoSettingVO getInstanceJcoSettingFromServer(ServerVO server, String instance) throws Exception;

	public JcoSettingVO getInstanceJcoSettingFromDB(String host, String instance);

	public DbSettingVO getInstanceDbSettingFromServer(ServerVO server, String instance) throws Exception;

	public DbSettingVO getInstanceDbSettingFromDB(String host, String instance);

	public SapSettingVO getInstanceSapSettingFromServer(ServerVO server, String instance) throws Exception;

	public SapSettingVO getInstanceSapSettingFromDB(String host, String instance);

	public int insertJcoSettingToDB(JcoSettingVO jco);

	public int updateJcoSettingToDB(JcoSettingVO jco);

	public int insertDbSettingToDB(DbSettingVO db);

	public int updateDbSettingToDB(DbSettingVO db);

	public int insertSapSettingToDB(SapSettingVO sap);

	public int updateSapSettingToDB(SapSettingVO sap);

	public int updateInstanceList(ServerVO server, List<String> instances);

	public Module startCubeOneInstance(ServerVO server, MemberVO member, String instance);

	public Module stopCubeOneInstance(ServerVO server, MemberVO member, String instance);

	public Module startCubeOneModule(ServerVO server, MemberVO member);

	public Module stopCubeOneModule(ServerVO server, MemberVO member);

	public Module cotest(ServerVO server, String instance);

	public String getTop();

	public Linux[] getAllCommand(ServerVO server);

	public double getCpuUsage(ServerVO server);

	public double getMemoryUsage(ServerVO server);

	public double getDiskUsage(ServerVO server);

	public List<ProcessVO> getProcess(ServerVO server, String sortType);

	public JSONObject readEncLog(ServerVO server, String directory, String date, String sid, String conv);

	public JSONObject readEventLog(ServerVO server, String directory);

	public String getCountDecError(ServerVO server, String directory, String date);

	public String getCountEncError(ServerVO server, String directory, String date);

	public Version getModuleVersionToServer(ServerVO server);

	public int updateModuleVersionToDB(ServerVO server, Version version);

	public Version getModuleVersionToDB(@Param("host") String host, @Param("serverType") Type serverType);

	public String decrypt(ServerVO server, String instance, String encryptText);

}
