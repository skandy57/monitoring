package sbt.qsecure.monitoring.os;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.IllegalSelectorException;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Logger;
import com.jcraft.jsch.Session;

import lombok.extern.slf4j.Slf4j;
import sbt.qsecure.monitoring.constant.OperationSystem;
import sbt.qsecure.monitoring.vo.ServerVO;
import sbt.qsecure.monitoring.vo.TerminalConnectionVO;
import sbt.qsecure.monitoring.vo.TerminalVO;

@Slf4j
public class LinuxConnector implements OSConnector{

//	@Autowired
//	Environment env;
	private static final int SSH_CONNECT_TIMEOUT = 5000;
	private static final int SHELL_CONNECT_TIMEOUT = 3000;
	private static final int SHELL_READ_BUFFER_SIZE = 1024;

	private static Map<WebSocketSession, TerminalConnectionVO> sshMap = new ConcurrentHashMap<>();
	private ExecutorService executorService = Executors.newCachedThreadPool();

	private JSch jsch;
	private Session session;
	private String userId;
	private String passwd;
	private String host;
	private int port;
	private Channel channel;

	public LinuxConnector(ServerVO vo) {
		this.host = vo.host();
		this.userId = vo.userId();
		this.passwd = vo.passwd();
		this.port = vo.port();
		
		JSch.setLogger(new Logger() {
			@Override
			public void log(int level, String message) {
				log.debug("========== JSch Logger==========\n==========Level: {}, Message: {} ===========", level,
						message);
			}

			@Override
			public boolean isEnabled(int level) {
				// TODO Auto-generated method stub
				return true;
			}
		});
		jsch = new JSch();
	}

	public boolean isConnected() {

		try {
			log.info("{} {} {} {}", userId, host, port, passwd);
			session = jsch.getSession(userId, host, port);
			session.setPassword(passwd);
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect(SSH_CONNECT_TIMEOUT);

		} catch (JSchException e) {
			log.error("SSH 연결 실패", e);
			return false;
		} finally {
			disconnect();
		}

		return session.isConnected();
	}

	public void connect() {
		try {
			session = jsch.getSession(userId.trim(), host.trim(), port);
			session.setPassword(passwd.trim());
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();
		} catch (JSchException e) {
			log.error("SSH 연결 실패", e);
		}
	}
	@Override
	public String sendCommand(String command) throws Exception {
		connect();
		if (channel == null || !channel.isConnected()) {
			channel = session.openChannel("exec");
		}
		((ChannelExec) channel).setCommand(command);

		try (InputStream in = channel.getInputStream()) {
			if (!channel.isConnected()) {
				channel.connect();
			}
			byte[] buffer = new byte[8192];
			StringBuilder result = new StringBuilder();

			int bytesRead;
			while ((bytesRead = in.read(buffer, 0, 8192)) > 0) {
				result.append(new String(buffer, 0, bytesRead));
			}
			log.info(result.toString());
			return result.toString();
		 } catch (JSchException e) {
		        log.error("JSchException: " + e.getMessage(), e);
		    } catch (IOException e) {
		        log.error("IOException: " + e.getMessage(), e);
		    } finally {
		        disconnect();
		    }
		return null;

	}

	public void disconnect() {
		if (session != null || session.isConnected()) {
			session.disconnect();
		}
		if (channel != null || channel.isConnected()) {
			channel.disconnect();
		}
	}

	public void connectTerminal(WebSocketSession webSession, TerminalVO vo) {
		TerminalConnectionVO connection = new TerminalConnectionVO();
		connection.setJsch(jsch);
		connection.setSession(webSession);
		connection.setVo(vo);
		sshMap.put(webSession, connection);

		executorService.execute(new Runnable() {

			@Override
			public void run() {
				try {
					connectShell(connection, vo, webSession);
				} catch (IOException | JSchException e) {
					log.error("터미널 연결 실패", e);
				}

			}
		});
	}

	public void recvHandle(WebSocketSession session, String command) {
		TerminalConnectionVO connection = sshMap.get(session);

		if (connection != null) {
			try {
				transTerminel(connection.getChannel(), command);
			} catch (IOException e) {
				log.error("에러 정보: {}", e);
				close(session);
			}
		}
	}

	public void sendMessage(WebSocketSession session, byte[] buffer) throws IOException {
		session.sendMessage(new TextMessage(buffer));
	}

	public void close(WebSocketSession session) {
		TerminalConnectionVO connection = sshMap.get(session);
		if (connection != null) {
			if (connection.getChannel() != null) {
				connection.getChannel().disconnect();
			}
			sshMap.remove(session);
		}
	}

	private void connectShell(TerminalConnectionVO connection, TerminalVO vo, WebSocketSession webSocketSession)
			throws IOException, JSchException {
		Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no");

		session = jsch.getSession(userId.trim(), host.trim(), port);
		session.setConfig(config);

		session.setPassword(passwd);
		session.connect(SSH_CONNECT_TIMEOUT);

		Channel channel = session.openChannel("shell");
		channel.connect(SHELL_CONNECT_TIMEOUT);
		connection.setChannel(channel);

		try (InputStream is = channel.getInputStream()) {
			byte[] buffer = new byte[SHELL_READ_BUFFER_SIZE];
			int bytesRead;
			while ((bytesRead = is.read(buffer)) != -1) {
				sendMessage(webSocketSession, Arrays.copyOfRange(buffer, 0, bytesRead));
			}
		} finally {
			session.disconnect();
			channel.disconnect();
		}
	}

	private void transTerminel(Channel channel, String command) throws IOException {
		if (channel != null) {
			try (OutputStream os = channel.getOutputStream()) {
				if ("SIGINT".equals(command)) {
					os.write(3);
				} else if ("SIGTSTP".equals(command)) {
					os.write(26);
				} else {
					os.write(command.getBytes());
				}
			}
		}
	}
	public OperationSystem getOSType() {
		return OperationSystem.LINUX;
	}
}
