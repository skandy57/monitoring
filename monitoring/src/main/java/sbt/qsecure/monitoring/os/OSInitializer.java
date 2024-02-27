package sbt.qsecure.monitoring.os;

import java.util.List;

import sbt.qsecure.monitoring.constant.Server.Version;
import sbt.qsecure.monitoring.vo.DbSettingVO;
import sbt.qsecure.monitoring.vo.JcoSettingVO;
import sbt.qsecure.monitoring.vo.SapSettingVO;

public interface OSInitializer extends OSConnector {

	public List<String> getInstanceDirectoryList();

	public Version getModuleVersion() throws Exception;

	public SapSettingVO getSapSetting(String instance) throws Exception;

	public DbSettingVO getDbSetting(String instance) throws Exception;

	public JcoSettingVO getJcoSetting(String instance) throws Exception;

}
