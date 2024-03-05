package sbt.qsecure.monitoring.connector;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.swing.JFileChooser;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Logger;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import lombok.extern.slf4j.Slf4j;
import sbt.qsecure.monitoring.constant.Server;
import sbt.qsecure.monitoring.vo.ServerVO;
import sbt.qsecure.monitoring.vo.TerminalConnectionVO;
import sbt.qsecure.monitoring.vo.TerminalVO;

/**
 * 
 */
/**
 * 
 */
@Slf4j
public class LinuxConnector implements OSConnector {

//	@Autowired
//	Environment env;
	private static final int SSH_CONNECT_TIMEOUT = 5000;
	private static final int SHELL_CONNECT_TIMEOUT = 3000;
	private static final int SHELL_READ_BUFFER_SIZE = 1024;

	private static Map<WebSocketSession, TerminalConnectionVO> sshMap = new ConcurrentHashMap<>();

	JSch jsch;
	Session session;
	String userId;
	String passwd;
	String host;
	int port;
	Channel channel;

	public LinuxConnector(ServerVO server) {
		this.host = server.host();
		this.userId = server.userId();
		this.passwd = server.passwd();
		this.port = server.port();

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

	/**
	 * WAS와 A/I서버간 연결 가능 여부를 반환한다.
	 * 
	 * @return WAS <-> A/I 서버 연결 여부
	 * @exception JSchException A/I Server자체가 내려가있거나, 네트워크 관련 문제로 exception발생할 수 있음
	 */
	@Override
	public boolean isConnected() {

		try {
			log.info("[isConnected] Try Connected WAS <-> A/I Server Target Host=[{}:{}] A/I Server userId=[{}]", host,
					port, userId);
			session = jsch.getSession(userId, host, port);
			session.setPassword(passwd);
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect(SSH_CONNECT_TIMEOUT);
			return session.isConnected();
		} catch (JSchException e) {
			log.error(Server.Log.ISCONNECT.error(host), e);
			return false;
		} finally {
			disconnect();
		}
	}

	/**
	 * WAS와 A/I서버와 세션을 연결한다.
	 * 
	 * 추후 setConfig에서 HostKeyChecking을 해야함
	 */
	public void connect() {
		try {
			if (session == null || !session.isConnected()) {
				session = jsch.getSession(userId.trim(), host.trim(), port);
				session.setPassword(passwd.trim());
				session.setConfig("StrictHostKeyChecking", "no");
				session.connect();
//				log.info(Server.Log.CONNECT.success(host, String.valueOf(port)));
			}
		} catch (JSchException e) {
			log.error(Server.Log.CONNECT.error(host, String.valueOf(port)));
		}
	}

	public void reConnect() {
		disconnect();
		connect();
	}

	/**
	 * A/I서버로 명령어를 던진다.
	 * 
	 * @param command A/I서버로 던질 명령어
	 * @return 명령어의 결과값
	 * @exception JSchException        세션 및 채널통신 간 예외
	 * @exception IOException          명령어를 실행 중 or 결과값을 리턴받는 과정에서의 입출력 예외
	 * @exception TimeoutException     5초간 결과값이 들어오기까지를 기다리는데, 5초 초과할 경우 예외
	 * @exception InterruptedException 멀티쓰레드 제어간 예외
	 * @exception ExecutionException   비동기 작업간 예외
	 * 
	 * 
	 */
	@Override
	public String sendCommand(String command) throws Exception {

		connect();
		if (channel == null || !channel.isConnected()) {
			channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);
			channel.connect();
		}

		ExecutorService executor = Executors.newSingleThreadExecutor();
		StringBuilder result = new StringBuilder();

		try {
			Future<String> future = executor.submit(() -> {
				try (InputStream inputStream = channel.getInputStream();
						InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
						BufferedReader reader = new BufferedReader(inputStreamReader)) {

					long startTime = System.currentTimeMillis();
					String line;

					while ((line = reader.readLine()) != null) {

						result.append(line.trim()).append(System.lineSeparator());

						if (Thread.currentThread().isInterrupted()) {
							log.warn("[sendCommand] Input stream reading thread interrupted.");
							break;
						}

						if (System.currentTimeMillis() - startTime > 5000) {
							log.warn("[sendCommand] Timeout reached while reading input stream.");
							break;
						}
					}
					return result.toString().trim();
				} catch (IOException e) {
					log.error("[sendCommand] Exception while reading input stream: " + command);
					return null;
				}
			});

			try {
				result.append(future.get(5, TimeUnit.SECONDS));
//				future.get(5, TimeUnit.SECONDS);
			} catch (TimeoutException e) {
				future.cancel(true);
				log.warn("[sendCommand] Task execution timed out.");
			} catch (InterruptedException e) {
				future.cancel(true);
				log.warn("[sendCommand] Task execution interrupted.");
				Thread.currentThread().interrupt();
			} catch (ExecutionException e) {
				future.cancel(true);
				log.error("[sendCommand] Error while executing task: " + e.getMessage(), e.getCause());
			}
		} finally {
			disconnect();
			if (executor != null) {
				executor.shutdown();
			}
			try {
				if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
					executor.shutdownNow();
					if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
						log.error("[sendCommand] ExecutorService did not terminate within 5 seconds.");
					}
				}
			} catch (InterruptedException e) {
				log.error("[sendCommand] ExecutorService termination interrupted: " + e.getMessage(), e);
				executor.shutdownNow();
				Thread.currentThread().interrupt();
			}

		}
		return result.toString();

	}

	public List<String> sendCommandList(String[] commands) throws Exception {
		connect();
		ExecutorService executor = Executors.newSingleThreadExecutor();
		List<String> results = new ArrayList<>();

		try {
			for (String command : commands) {
				final Channel commandChannel = session.openChannel("exec");
				try {
					((ChannelExec) commandChannel).setCommand(command);
					commandChannel.connect();

					StringBuilder result = new StringBuilder();
					Future<String> future = executor.submit(() -> {
						try (InputStream inputStream = commandChannel.getInputStream();
								InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
								BufferedReader reader = new BufferedReader(inputStreamReader)) {

							long startTime = System.currentTimeMillis();
							String line;

							while ((line = reader.readLine()) != null) {
								result.append(line).append(System.lineSeparator());

								if (Thread.currentThread().isInterrupted()) {
									log.warn("[sendCommandList] Input stream reading thread interrupted.");
									break;
								}

								if (System.currentTimeMillis() - startTime > 5000) {
									log.warn("[sendCommandList] Timeout reached while reading input stream.");
									break;
								}
							}
						} catch (IOException e) {
							log.error("[sendCommandList] Exception while reading input stream: " + command, e);
						}
						return result.toString();
					});

					try {
						results.add(future.get(5, TimeUnit.SECONDS));
					} catch (TimeoutException e) {
						future.cancel(true);
						log.warn("[sendCommandList] Task execution timed out for command: " + command);
					} catch (InterruptedException e) {
						future.cancel(true);
						log.warn("[sendCommandList] Task execution interrupted for command: " + command);
						Thread.currentThread().interrupt();
					} catch (ExecutionException e) {
						future.cancel(true);
						log.error("[sendCommandList] Error while executing task for command: " + command, e.getCause());
					}
				} finally {
					commandChannel.disconnect();
				}
			}
		} finally {
			disconnect();
			if (executor != null) {
				executor.shutdown();
			}
			try {
				if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
					executor.shutdownNow();
					if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
						log.error("[sendCommandList] ExecutorService did not terminate within 5 seconds.");
					}
				}
			} catch (InterruptedException e) {
				log.error("[sendCommandList] ExecutorService termination interrupted: " + e.getMessage(), e);
				executor.shutdownNow();
				Thread.currentThread().interrupt();
			}
		}

		return results;
	}

	/**
	 * WAS <-> A/I 간 세션 및 채널의 연결을 해제한다.
	 */
	public void disconnect() {
		try {
			if (session != null || session.isConnected()) {
				session.disconnect();
			}
			if (channel != null || channel.isConnected()) {
				channel.disconnect();
			}
		} catch (NullPointerException e) {

		}
	}

	public void getFile(String remoteFilePath) throws JSchException, IOException, SftpException {
		connect();
		ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
		channelSftp.connect();
		channelSftp.get(remoteFilePath, "C:\\Users\\Administrator\\event_log");

		channelSftp.disconnect();
		session.disconnect();
	}

	public void getEventLogFile(String instance, String eventLogPath, String localPath) {
		ChannelSftp channelSftp = null;
		try {
			connect();
			channelSftp = (ChannelSftp) session.openChannel("sftp");
			channelSftp.connect();

			if (eventLogPath.contains("./event_log")) {
				eventLogPath = "/home/" + this.userId + "/aisvr/" + instance + "/event_log";
			}

			log.info("[getEventLogFile] Trying to get " + instance + " Event Log File... ");
			channelSftp.get(eventLogPath, localPath);
			log.info("[getEventLogFile] Successfully got " + instance + " Event Log File");
		} catch (JSchException | SftpException e) {
			log.error("[getEventLogFile] An error occurred get " + instance + " event log : " + e.getMessage());

			if (e instanceof SftpException && ((SftpException) e).id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
				log.error("[getEventLogFile] Error: File does not exist " + eventLogPath + " " + instance);
			}

			File localFile = new File(localPath);
			if (localFile.exists()) {
				if (localFile.delete()) {
					log.error("[handleFileError] Error: EventLog File Deleted " + localPath);
				} else {
					log.error("[handleFileError] Error: Failed to delete local file " + localPath);
				}
			} else {
				log.error("[handleFileError] Error: Local file does not exist " + localPath);
			}
		} finally {
			if (channelSftp != null && channelSftp.isConnected()) {
				channelSftp.disconnect();
			}
			disconnect();
		}
	}

	/**
	 * WAS <-> A/I 간 WebSocket을 연결한다
	 * 
	 * @param webSession WebSocket 세션
	 * @param vo         WebSocket간 필요한 정보
	 */
	public void connectTerminal(WebSocketSession webSession, TerminalVO vo) {
		TerminalConnectionVO connection = new TerminalConnectionVO();
		connection.setJsch(jsch);
		connection.setSession(webSession);
		connection.setVo(vo);
		sshMap.put(webSession, connection);

//		executorService.execute(new Runnable() {
//
//			@Override
//			public void run() {
//				try {
//					connectShell(connection, vo, webSession);
//				} catch (IOException | JSchException e) {
//					log.error("터미널 연결 실패", e);
//				}
//
//			}
//		});
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

//	/**
//	 * CubeOne 인스턴스 기동 시 return이 바로 오지 않으니, 5초간의 리턴 기록을 받아온다.
//	 * 
//	 * @return 해당 인스턴스 실행 명령어의 5초간의 결과값
//	 * @throws IOException
//	 * @throws JSchException
//	 */
//	private String sendCubeOneCommand() throws IOException, JSchException {
//
//		try (InputStream in = channel.getInputStream();
//				ByteArrayOutputStream resultStream = new ByteArrayOutputStream()) {
//			if (!channel.isConnected()) {
//				channel.connect();
//			}
//
//			StringBuilder result = new StringBuilder();
//			Object lock = new Object();
//
//			executor.submit(() -> {
//				try {
//					byte[] buffer = new byte[8192];
//					int bytesRead;
//					long startTime = System.currentTimeMillis();
//
//					while ((bytesRead = in.read(buffer, 0, 8192)) > 0) {
//						synchronized (lock) {
//							result.append(new String(buffer, 0, bytesRead));
//						}
//
//						if (Thread.currentThread().isInterrupted()) {
//							log.warn("[sendCommand] Input stream reading thread interrupted.");
//							break;
//						}
//
//						if (System.currentTimeMillis() - startTime > 5000) {
//							log.warn("[sendCommand] Timeout reached while reading input stream.");
//							break;
//						}
//					}
//				} catch (InterruptedIOException e) {
//					log.warn("[sendCommand] Input stream reading thread interrupted: " + e.getMessage());
//					Thread.currentThread().interrupt();
//				} catch (IOException e) {
//					log.error("[sendCommand] Exception while reading input stream: " + e.getMessage(), e);
//				}
//			});
//
//			executor.shutdown();
//			try {
//				if (!executor.awaitTermination(7000, TimeUnit.MILLISECONDS)) {
//					executor.shutdownNow();
//				}
//			} catch (InterruptedException e) {
//				log.error("[sendCommand] Executor service termination interrupted: " + e.getMessage(), e);
//				Thread.currentThread().interrupt();
//			}
//
//			log.info(result.toString());
//			return result.toString();
//		}
//	}
	/**
	 * CubeOne 인스턴스 기동 시 return이 바로 오지 않으니, 5초간의 리턴 기록을 받아온다.
	 * 
	 * @return 해당 인스턴스 실행 명령어의 5초간의 결과값
	 * @throws JSchException
	 */
//	private String sendInstaceStart() throws JSchException {
//
//		StringBuilder result = new StringBuilder();
//
//		try {
//			Future<String> future = executor.submit(() -> {
//				try (InputStream in = channel.getInputStream();
//						ByteArrayOutputStream resultStream = new ByteArrayOutputStream()) {
//					byte[] buffer = new byte[8192];
//					int bytesRead;
//					long startTime = System.currentTimeMillis();
//
//					while ((bytesRead = in.read(buffer)) != -1) {
//						resultStream.write(buffer, 0, bytesRead);
//
//						if (Thread.currentThread().isInterrupted()) {
//							log.warn("[sendInstaceStart] Input stream reading thread interrupted.");
//							break;
//						}
//
//						if (System.currentTimeMillis() - startTime > 5000) {
//							log.warn("[sendInstaceStart] Timeout reached while reading input stream.");
//							break;
//						}
//					}
//					log.info(resultStream.toString());
//					return resultStream.toString();
//				} catch (IOException e) {
//					log.error("[sendInstaceStart] Exception while reading input stream: " + e.getMessage(), e);
//					return null;
//				}
//			});
//
//			try {
//				result.append(future.get(5, TimeUnit.SECONDS));
//			} catch (TimeoutException e) {
//				future.cancel(true);
//				log.warn("[sendInstaceStart] Task execution timed out.");
//			} catch (InterruptedException e) {
//				future.cancel(true);
//				log.warn("[sendInstaceStart] Task execution interrupted.");
//				Thread.currentThread().interrupt();
//			} catch (ExecutionException e) {
//				future.cancel(true);
//				log.error("[sendInstaceStart] Error while executing task: " + e.getMessage(), e.getCause());
//			}
//		} finally {
//			if (executor != null) {
//				executor.shutdown();
//			}
//			try {
//				if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
//					log.error("[sendInstaceStart] ExecutorService did not terminate within 5 seconds.");
//				}
//			} catch (InterruptedException e) {
//				log.error("[sendInstaceStart] ExecutorService termination interrupted: " + e.getMessage(), e);
//				Thread.currentThread().interrupt();
//			}
//			disconnect();
//		}
//
//		return result.toString();
//	}

//	private String sendInstaceStart() throws JSchException {
//		
//		try (InputStream in = channel.getInputStream();
//				ByteArrayOutputStream resultStream = new ByteArrayOutputStream()) {
//			if (!channel.isConnected()) {
//				channel.connect();
//			}
//
//			StringBuilder result = new StringBuilder();
//			Object lock = new Object();
//
//			executor.submit(() -> {
//				try {
//					byte[] buffer = new byte[8192];
//					int bytesRead;
//					long startTime = System.currentTimeMillis();
//
//					while ((bytesRead = in.read(buffer)) != -1) {
//						synchronized (lock) {
//							result.append(new String(buffer, 0, bytesRead));
//						}
//						if (Thread.currentThread().isInterrupted()) {
//							log.warn("[sendCommand] Input stream reading thread interrupted.");
//							break;
//						}
//
//						if (System.currentTimeMillis() - startTime > 5000) {
//							log.warn("[sendCommand] Timeout reached while reading input stream.");
//							break;
//						}
//					}
//				} catch (InterruptedIOException e) {
////					log.warn("[sendCommand] Input stream reading thread interrupted: " + e.getMessage());
//					if (Thread.currentThread().isAlive() || !Thread.currentThread().isInterrupted()) {
//						Thread.currentThread().interrupt();
//					}
//				} catch (IOException e) {
//					log.error("[sendCommand] Exception while reading input stream: " + e.getMessage(), e);
//				} finally {
//					disconnect();
//					try {
//						in.close();
//
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
//			});
//			executor.shutdown();
//			try {
//				if (!executor.awaitTermination(7000, TimeUnit.MILLISECONDS)) {
//					executor.shutdownNow();
//				}
//			} catch (InterruptedException e) {
//				log.error("[sendCommand] Executor service termination interrupted: " + e.getMessage(), e);
//				Thread.currentThread().interrupt();
//			}
//			return result.toString();
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		return null;
//	}

	public Server.OS getOSType() {
		return Server.OS.LINUX;
	}

}
