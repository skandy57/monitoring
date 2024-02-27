package sbt.qsecure.monitoring.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestParam;

import sbt.qsecure.monitoring.constant.Server;
import sbt.qsecure.monitoring.constant.Server.Type;
import sbt.qsecure.monitoring.constant.Server.Version;
import sbt.qsecure.monitoring.vo.DbSettingVO;
import sbt.qsecure.monitoring.vo.InstanceVO;
import sbt.qsecure.monitoring.vo.JcoSettingVO;
import sbt.qsecure.monitoring.vo.SapSettingVO;
import sbt.qsecure.monitoring.vo.MemberVO;
import sbt.qsecure.monitoring.vo.ServerVO;

@Mapper
public interface ServerMapper {

	public List<ServerVO> getServerList(Type serverType);

	public int updateInstanceList(ServerVO server, List<String> instances);

	public ServerVO getServerOne(@Param("serverSequence") long serverSequence, @Param("serverType") Type serverType);

	public int addServerInfo(ServerVO vo);

	public int updateServerInfo(ServerVO vo);

	public int deleteServerInfo(ServerVO vo);

	public List<InstanceVO> getInstanceList(String host);

	public JcoSettingVO getInstanceJcoSettingFromDB(@Param("host") String host, @Param("instance") String instance);

	public SapSettingVO getInstanceSapSettingFromDB(@Param("host") String host, @Param("instance") String instance);

	public DbSettingVO getInstanceDbSettingFromDB(@Param("host") String host, @Param("instance") String instance);

	public int insertJcoSettingToDB(JcoSettingVO jco);

	public int updateJcoSettingToDB(JcoSettingVO jco);

	public int insertDbSettingToDB(DbSettingVO db);

	public int updateDbSettingToDB(DbSettingVO db);

	public int insertSapSettingToDB(SapSettingVO sap);

	public int updateSapSettingToDB(SapSettingVO sap);

	public int insertInstanceSettingToDB(InstanceVO instance);

	public String getSapSettingFileExtend(ServerVO server);

	public Version getModuleVersionToDB(@Param("host") String host, @Param("serverType") Type serverType);

	public int updateModuleVersionToDB(ServerVO server, Version version);

	public int callCreateAllTable();

	public int createServerTable();

	public int createJcoTable();

	public int createDbTable();

	public int createSapTable();

	public int createInstanceTable();

}
