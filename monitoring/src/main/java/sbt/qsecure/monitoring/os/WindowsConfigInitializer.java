package sbt.qsecure.monitoring.os;

import java.util.List;

import sbt.qsecure.monitoring.constant.Server.Version;
import sbt.qsecure.monitoring.vo.DbSettingVO;
import sbt.qsecure.monitoring.vo.JcoSettingVO;
import sbt.qsecure.monitoring.vo.SapSettingVO;
import sbt.qsecure.monitoring.vo.ServerVO;

public class WindowsConfigInitializer extends WindowsConnector implements OSInitializer {

	public WindowsConfigInitializer(ServerVO vo) {
		super(vo);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<String> getInstanceDirectoryList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SapSettingVO getSapSetting(String instance) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DbSettingVO getDbSetting(String instance) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JcoSettingVO getJcoSetting(String instance) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Version getModuleVersion() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
