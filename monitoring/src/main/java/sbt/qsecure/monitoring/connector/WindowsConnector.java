package sbt.qsecure.monitoring.connector;

import java.util.List;

import sbt.qsecure.monitoring.constant.Server;
import sbt.qsecure.monitoring.vo.ServerVO;

public class WindowsConnector implements OSConnector {

	private String userId;
	private String passwd;
	private String host;
	private int port;

	public WindowsConnector(ServerVO vo) {

		this.userId = vo.userId();
		this.passwd = vo.passwd();
		this.host = vo.host();
		this.port = vo.port();
	}

	@Override
	public String sendCommand(String command) throws Exception {
		return null;
	}
	@Override
	public boolean isConnected() {
		return false;
	}

	@Override
	public Server.OS getOSType() {
		// TODO Auto-generated method stub
		return Server.OS.WINDOWS;
	}

	@Override
	public List<String> sendCommandList(String... command) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}



}
