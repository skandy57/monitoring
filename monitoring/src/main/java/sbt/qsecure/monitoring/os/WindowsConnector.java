package sbt.qsecure.monitoring.os;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.management.ObjectName;

import sbt.qsecure.monitoring.constant.OperationSystem;
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
	public OperationSystem getOSType() {
		// TODO Auto-generated method stub
		return OperationSystem.WINDOWS;
	}

}
