package sbt.qsecure.monitoring.service;

import static org.hamcrest.CoreMatchers.nullValue;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.ibatis.annotations.Param;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sbt.qsecure.monitoring.checker.AIValidator;
import sbt.qsecure.monitoring.connector.LinuxConfigInitializer;
import sbt.qsecure.monitoring.connector.LinuxConnector;
import sbt.qsecure.monitoring.connector.OSConnector;
import sbt.qsecure.monitoring.connector.OSInitializer;
import sbt.qsecure.monitoring.connector.WindowsConfigInitializer;
import sbt.qsecure.monitoring.connector.WindowsConnector;
import sbt.qsecure.monitoring.constant.Auth.AuthGrade;
import sbt.qsecure.monitoring.constant.Server;
import sbt.qsecure.monitoring.constant.Server.Command;
import sbt.qsecure.monitoring.constant.Server.Command.Linux;
import sbt.qsecure.monitoring.constant.Server.Log;
import sbt.qsecure.monitoring.constant.Server.Module;
import sbt.qsecure.monitoring.constant.Server.OS;
import sbt.qsecure.monitoring.constant.Server.Type;
import sbt.qsecure.monitoring.constant.Server.Version;
import sbt.qsecure.monitoring.mapper.CommandMapper;
import sbt.qsecure.monitoring.mapper.ServerMapper;
import sbt.qsecure.monitoring.vo.DbSettingVO;
import sbt.qsecure.monitoring.vo.InstanceVO;
import sbt.qsecure.monitoring.vo.JcoSettingVO;
import sbt.qsecure.monitoring.vo.SapSettingVO;
import sbt.qsecure.monitoring.vo.CommandVO;
import sbt.qsecure.monitoring.vo.MemberVO;
import sbt.qsecure.monitoring.vo.ProcessVO;
import sbt.qsecure.monitoring.vo.ServerVO;

/**
 * 서버에서 작업할 기능들을 모은 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ServerServiceImpl implements ServerService {

	private final ServerMapper serverMapper;
	private final CommandMapper commandMapper;

	private AIValidator vali = new AIValidator();

	/**
	 * 서버 유형에 따라 서버 목록을 반환한다.
	 *
	 * @param serverType 서버 유형(A/I Server, Security Server, Manager Server)
	 * @return 서버 목록
	 */
	@Override
	public List<ServerVO> getServerList(Type serverType) {

		return serverMapper.getServerList(serverType);
	}

	/**
	 * 지정된 서버의 정보를 반환한다
	 *
	 * @param sequence   지정된 서버의 시퀀스
	 * @param serverType 서버 유형(A/I Server, Security Server, Manager Server)
	 * @return 지정된 서버 정보
	 */
	@Override
	public ServerVO getServerOne(long serverSequence, Type serverType) {
		return serverMapper.getServerOne(serverSequence, serverType);
	}

	/**
	 * 서버의 상세 정보를 JSON 형식으로 반환한다.
	 *
	 * @param server 서버 정보
	 * @return JSON 형식의 서버 상세 정보
	 */
	@Override
	public JSONObject getServerDetailInfo(ServerVO server) {
		JSONParser parser = new JSONParser();
		JSONObject json = null;
		OSConnector osConnector = getOSConnector(server);

		if (osConnector != null) {
			String command = commandMapper.getCommandServerDetailInformation().trim();
			try {
				json = (JSONObject) parser.parse(osConnector.sendCommand(command));
			} catch (Exception e) {
				log.error(Server.Log.GETSERVERINFO.error(server.host()), e);
			}
		}
		return json;
	}

	/**
	 * cubeone 인스턴스 기동을 시도한 후 성공 여부를 반환
	 * 
	 * @param server   기동을 시도할 서버
	 * @param member   기동을 시도할 멤버
	 * @param instance 기동을 시도할 인스턴스
	 * @return 명령 시도 후 성공 여부
	 */
	@Override
	public Module startCubeOneInstance(ServerVO server, MemberVO member, String instanceDirectory) {
		if (!vali.isAdmin(member.authGrade())) {
			return Module.ERR_NOAUTH;
		}

		OSConnector osConnector = getOSConnector(server);
		if (osConnector == null) {
			return Module.ERR_UNKNOWNOS;
		}

		try {
			String command = Linux.STARTINSTANCE.build(instanceDirectory);
			String result = osConnector.sendCommand(command);

			if (vali.isWrongPath(result)) {
				return Module.ERR_WRONGPATH_INST;
			}

			if (vali.isAlreadyRunning(result)) {
				return Module.ALREADY_RUN;
			}

			if (!vali.isStartInstance(result)) {
				return Module.ERR_INSTANCE_CONTROLL;
			}

		} catch (Exception e) {
			return Module.ERR_INSTANCE_CONTROLL;
		}

		return Module.SUCCESS;
	}

	/**
	 * cubeone 인스턴스 중지를 시도한 후 결과 상수를 반환
	 * 
	 * @param server   중지를 시도할 서버VO
	 * @param member   중지를 시도할 멤버VO
	 * @param instance 중지를 시도할 인스턴스
	 * @return 명령 시도 후 성공여부 결과 상수
	 */
	@Override
	public Module stopCubeOneInstance(ServerVO server, MemberVO member, String instanceDirectory) {
		if (!vali.isAdmin(member.authGrade())) {
			return Module.ERR_NOAUTH;
		}

		OSConnector osConnector = getOSConnector(server);
		if (osConnector == null) {
			return Module.ERR_UNKNOWNOS;
		}
		
		try {
			String command = Linux.STOPINSTANCE.build(instanceDirectory);
			String result = osConnector.sendCommand(command);

			if (vali.isWrongPath(result)) {
				return Module.ERR_WRONGPATH_INST;
			}
			if (vali.isNotRunning(result)) {
				return Module.NOT_RUNNING;
			}

			if (!vali.isStopInstance(result)) {
				return Module.ERR_INSTANCE_CONTROLL;
			}

		} catch (Exception e) {
			return Module.ERR_INSTANCE_CONTROLL;
		}

		return Module.SUCCESS;
	}

	/**
	 * cubeone 모듈 기동을 시도한 후 결과 상수를 반환
	 * 
	 * @param server 기동을 시도할 서버
	 * @param member 기동을 시도할 멤버
	 * @return 명령 시도 후 성공여부 결과 상수
	 */
	@Override
	public Module startCubeOneModule(ServerVO server, MemberVO member) {
		if (member == null || server == null) {
			return Module.NULL;
		}
		if (!vali.isAdmin(member.authGrade())) {
			return Module.ERR_NOAUTH;
		}
		OSConnector osConnector = getOSConnector(server);

		if (osConnector == null) {
			return Module.ERR_UNKNOWNOS;
		}
		try {
			String command = Linux.STARTMODULE.build();
			String result = osConnector.sendCommand(command);

			if (result == null) {
				return Module.ERR_MODULE_CONTROLL;
			}

			if (vali.isWrongPath(result)) {
				return Module.ERR_WRONGPATH_MODULE;
			}

			if (vali.isAlreadyRunning(result)) {
				return Module.ALREADY_RUN;
			}

			if (!vali.isStartModule(result)) {
//					log.info(Server.Log.STARTMODULE.error(member.managerName(), member.authGrade(), server.host()));
				return Module.ERR_MODULE_CONTROLL;
			}

		} catch (Exception e) {
			log.error(Log.STARTMODULE.error(member.managerName().trim(), member.authGrade().trim(),
					server.host().trim(), String.valueOf(server.port()).trim()), e);
			return Module.ERR_MODULE_CONTROLL;
		}
		return Module.SUCCESS;
	}

	/**
	 * cubeone 모듈 중지를 시도한 후 성공여부 반환
	 * 
	 * @param server 중지를 시도할 서버
	 * @param member 중지를 시도할 멤버
	 * @return 명령 시도 후 성공여부
	 */
	@Override
	public Module stopCubeOneModule(ServerVO server, MemberVO member) {
		if (!vali.isAdmin(member.authGrade())) {
			return Module.ERR_NOAUTH;
		}
		OSConnector osConnector = getOSConnector(server);

		if (osConnector == null) {
			return Module.ERR_UNKNOWNOS;
		}
		try {
			String command = Linux.STOPMODULE.build();
			String result = osConnector.sendCommand(command);

			if (result == null) {
				return Module.ERR_MODULE_CONTROLL;
			}
			if (vali.isWrongPath(result)) {
				return Module.ERR_WRONGPATH_MODULE;
			}
			if (vali.isNotRunning(result)) {
				return Module.NOT_RUNNING;
			}

			if (!vali.isStopModule(result)) {
				return Module.ERR_MODULE_CONTROLL;
			}
		} catch (Exception e) {
			log.error(Server.Log.STOPMODULE.error(member.managerName(), member.authGrade(), server.host()), e);
			return Module.ERR_MODULE_CONTROLL;
		}

		return Module.SUCCESS;
	}

	@Override
	public String getTop() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 서버의 cpu 사용량을 반환한다
	 * 
	 * @param server cpu 사용량을 구할 서버
	 * @return 서버의 cpu 사용량
	 */
	@Override
	public double getCpuUsage(ServerVO server) {
		String[] cpuUsage = null;
		OSConnector osConnector = getOSConnector(server);
		if (osConnector != null) {
			try {
				String command = Linux.CPU_USAGE.build();
				String result = osConnector.sendCommand(command);

				if (result == null) {
					return 0.0;
				}
				cpuUsage = result.trim().split("\\s+");
//				log.info(Server.Log.GETCPUUSAGE.success(server.host()));
			} catch (Exception e) {
				log.error(Log.GETCPUUSAGE.error(server.host()), e);
				return 0.0;
			}
		}

		return Double.parseDouble(cpuUsage[0]);
	}

	@Override
	public double getMemoryUsage(ServerVO server) {
		OSConnector osConnector = null;
		try {
			osConnector = getOSConnector(server);
			if (osConnector == null) {
				return 0.0;
			}

			String command = Linux.MEMORY_USAGE.build();
			String result = osConnector.sendCommand(command);
			if (result == null) {
				return 0.0;
			}

			String[] memoryUsage = result.trim().split("\\s+");
			if (memoryUsage.length == 0) {
				return 0.0;
			}

			try {
				return Double.parseDouble(memoryUsage[0]);
			} catch (NumberFormatException e) {
				log.error(Log.GETMEMORYUSAGE.error(server.host()), e);
				return 0.0;
			}
		} catch (Exception e) {
			log.error(Log.GETMEMORYUSAGE.error(server.host()), e);
			return 0.0;
		}

	}

	@Override
	public double getDiskUsage(ServerVO server) {
		String[] diskUsage = null;

		OSConnector osConnector = getOSConnector(server);

		try {
			String command = Linux.CPU_USAGE.build();
			String result = osConnector.sendCommand(command);
			if (result == null) {
				return 0.0;
			}
			diskUsage = result.trim().split("\\s+");
		} catch (Exception e) {
			log.error(Log.GETDISKUSAGE.error(server.host()), e);
			return 0.0;
		}
		return Double.parseDouble(diskUsage[0]);
	}

	@Override
	public List<ProcessVO> getProcess(ServerVO server, String sortType) {

		List<ProcessVO> processList = new ArrayList<>();
		OSConnector osConnector = getOSConnector(server);

		if (osConnector != null) {
			try {
				String getProcess = Linux.GETPROCESS.build(sortType);
				String result = osConnector.sendCommand(getProcess);

				if (!vali.isWrongPath(result)) {
					String[] lines = result.split("\n");

					for (String line : lines) {

						String[] parts = line.trim().split("\\s+");
						if (parts.length >= 11) {
							String user = parts[0];
							String pid = parts[1];
							String cpu = parts[2];
							String memory = parts[3];
							String vsz = parts[4];
							String rss = parts[5];
							String tty = parts[6];
							String status = parts[7];
							String start = parts[8];
							String time = parts[9];
							String command = parts[10];

							ProcessVO process = new ProcessVO(user, pid, cpu, memory, vsz, rss, tty, status, start,
									time, command);

							processList.add(process);

						} else {
							log.warn("[getProcess] Invalid line format: " + line);
						}
					}
				} else {
					log.warn(Log.GETPROCESS.error(server.host()));
				}
			} catch (Exception e) {
				log.error(Log.GETPROCESS.error(server.host()), e);
			}
		}
		return processList;
	}

	@Transactional
	@Override
	public int insertServerInfo(ServerVO server) {

		int result = serverMapper.addServerInfo(server);

		if (result == 0) {
			throw new RuntimeException("[addServerInfo] Fail insert Server Info, Roll Back");
		}
		return result;
	}

	@Transactional
	@Override
	public int deleteServerInfo(ServerVO server) {

		int result = serverMapper.deleteServerInfo(server);

		if (result == 0) {
			throw new RuntimeException("[deleteServerInfo] Fail delete Server Info, Roll Back");
		}
		return result;
	}

	@Override
	public int updateServerInfo(ServerVO server) {

		int result = serverMapper.updateServerInfo(server);

		if (result == 0) {
			throw new RuntimeException("[updateServerInfo] Fail update Server Info, Roll Back");
		}

		return result;
	}

	@Override
	public Linux[] getAllCommand(ServerVO server) {
//		if (server.serverOs().contains(Server.OS.WINDOWS.getOs())) {
//			return Server.Command.Windows.values();
//		} else if (server.serverOs().contains(Server.OS.LINUX.getOs())) {
//			return Server.Command.Linux.values();
//		}
		return null;
	}

	@Override
	public JSONObject readEncLog(ServerVO vo, String directory, String date, String sid, String conv) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * event_log를 서버에서 읽어 List형태의 JSON으로 파싱하여 반환한다.
	 *
	 * @param server    서버 정보
	 * @param directory event_log의 디렉토리
	 * @return List형태의 event_log JSON
	 */
	@Override
	public JSONObject readEventLog(ServerVO vo, String directory) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * enc_event_log의 암호화 ERROR건 수를 반환한다.
	 *
	 * @param server    서버 정보
	 * @param directory enc_event_log의 디렉토리
	 * @param date      enc_event_log의 날짜
	 * @return enc_event_log의 암호화 ERROR 건 수를 포맷된 문자열로 반환한다. (콤마로 천 단위 구분)
	 */
	@Override
	public String getCountEncError(ServerVO server, String directory, String date) {

		int encErrorCount = 0;
		OSConnector osConnector = getOSConnector(server);

		if (osConnector != null) {

			try {
				// 암호화 ERROR 건 수를 조회하는 명령어 조합하여 서버로부터 결과값을 받아온다.
				String command = Linux.COUNT_ENC_ERR.build(directory, date);
				String result = osConnector.sendCommand(command);

				// 만약에 잘못된 디렉토리 또는 없는 파일이라면 공백이나 NULL을 반환받았기 때문에
				// 0을 리턴한다.
				if (vali.isWrongPath(result)) {
					return String.valueOf(encErrorCount);
				}

				// 공백을 기준으로 분할하여 1보다 작거나 토큰이 숫자가 아닌지 확인한다.
				String[] resultTokens = result.trim().split("\\s+");
				if (resultTokens.length < 1) {
					log.error("[getCountEncError] Unexpected result format: {}", result);
					return null;
				}
				if (!vali.isNumeric(resultTokens[0])) {
					log.error("[getCountEncError] Non-numeric value received: {}", resultTokens[0]);
					return null;
				}
				// 결과를 정수로 변환한다.
				encErrorCount = Integer.parseInt(resultTokens[0]);

			} catch (NumberFormatException e) {
				log.error("[getCountEncError] Failed to parse result to integer: {}", e.getMessage());
				return null;
			} catch (Exception e) {
				log.error("[getCountEncError] Failed to execute command for server: {}", server.host(), e);
				return null;
			}

		}
//		DecimalFormat df = new DecimalFormat("###,###");
		return String.valueOf(encErrorCount);
	}

	/**
	 * enc_event_log의 복호화 ERROR건 수를 반환한다.
	 *
	 * @param server    서버 정보
	 * @param directory enc_event_log의 디렉토리
	 * @param date      enc_event_log의 날짜
	 * @return enc_event_log의 복호화 ERROR 건 수를 포맷된 문자열로 반환한다. (콤마로 천 단위 구분)
	 */
	@Override
	public String getCountDecError(ServerVO server, String directory, String date) {

		int decErrorCount = 0;
		OSConnector osConnector = getOSConnector(server);

		if (osConnector != null) {
			try {
				String command = Linux.COUNT_DEC_ERR.build(directory, date);
				String result = osConnector.sendCommand(command);

				if (vali.isWrongPath(result)) {
					return String.valueOf(decErrorCount);
				}
				String[] resultTokens = result.trim().split("\\s+");
				if (resultTokens.length < 1) {
					log.error("[getCountDecError] Unexpected result format: {}", result);
					return null;
				}
				if (!vali.isNumeric(resultTokens[0])) {
					log.error("[getCountDecError] Non-numeric value received: {}", resultTokens[0]);
					return null;
				}

				decErrorCount = Integer.parseInt(resultTokens[0]);

			} catch (NumberFormatException e) {
				log.error("[getCountDecError] Failed to parse result to integer: {}", e.getMessage());
				return null;
			} catch (Exception e) {
				log.error("[getCountDecError] Failed to execute command for server: {}", server.host(), e);
				return null;
			}
		}

		DecimalFormat df = new DecimalFormat("###,###");
		return df.format(decErrorCount);
	}

	/**
	 * 암호화서버에서 모듈테스트, SAP연결테스트, DB테스트를 진행한다.
	 * 
	 * @param server 테스트를 진행할 인스턴스가 있는 서버
	 * @param instance 테스트를 진행할 인스턴스
	 * @return 테스트의 결과 상수
	 */
	@Override
	public Module cotest(ServerVO server, InstanceVO instance) {
		
		OSConnector osConnector = getOSConnector(server);
		//테스트를 진행할 명령어 목록을 배열에 담는다.
		String[] checks = { "enc", "sap", "db" };
		
		//사용자가 입력한 디렉토리의 구분자를 확인한다
		String directory = vali.addMissingFlash(instance.directory());
		String[] serverInfo = { server.host(), String.valueOf(server.port()), instance.instance().toUpperCase() };

		for (String type : checks) {
			try {
				String command = Linux.COTEST.build(directory, type);
				String result = osConnector.sendCommand(command);

				if (result != null) {

					if (vali.isSuccess(result)) {
						log.info(Log.COTEST.success(type, instance.instance()));
						continue;

					} else if (vali.isWrongPath(result)) {

						log.info(Module.getDescription(Module.ERR_WRONGPATH_INST, new String[] { server.host(),
								String.valueOf(server.port()), instance.instance(), instance.directory() }));

						return Module.ERR_WRONGPATH_INST;
					}

					switch (type) {
					case "enc":
						log.warn(Module.getDescription(Module.ERR_MODULE_TEST, serverInfo));
						return Module.ERR_MODULE_TEST;

					case "sap":
						log.warn(Module.getDescription(Module.ERR_SAP_TEST, serverInfo));
						return Module.ERR_SAP_TEST;

					case "db":
						log.info("retry db");
						Module dbResult = retryConnectDb(osConnector, directory);

						if (dbResult.equals(Module.SUCCESS)) {
							return dbResult;
						}

						log.warn("[cotest] " + Module.getDescription(Module.ERR_AIDB_TEST, serverInfo));

						return dbResult;
					}
				}
			} catch (Exception e) {
				log.error(Module.getDescription(Module.ERR_COTEST, serverInfo), e);
				return Module.ERR_COTEST;
			}
		}
		return Module.SUCCESS;
	}

	private Module retryConnectDb(OSConnector osConnector, String path) {
		path = this.vali.addMissingFlash(path);
		try {
			String command = Linux.COTEST.retry(path);
			String result = osConnector.sendCommand(command);

			if (vali.isSuccess(result)) {
				return Module.SUCCESS;
			}
			return Module.ERR_AIDB_TEST;
		} catch (Exception e) {
			return Module.ERR_COTEST;
		}
	}

	@Override
	public Set<InstanceVO> getInstanceListToServer(ServerVO server) {

		OSConnector osConnector = getOSConnector(server);

		Set<InstanceVO> instances = new HashSet<>();
		try {

			String[] homeToken = osConnector.sendCommand(Linux.GETCOHOME.build()).trim().split("\\s");
			String coHome = homeToken[0];
			String directory = null;

			Version version = getModuleVersionToDB(server.host(), Type.AI);

			String command = Linux.GETINSTANCELIST.build(version);
			String result = osConnector.sendCommand(command);
			String[] lines = result.split("\\r?\\n");

			for (String line : lines) {
				if (vali.isDirectory(line)) {
					String[] parts = line.split("\\s+");
					if (parts.length > 8) {
						String instance = parts[8];
						switch (version) {
						case NEW -> directory = coHome + "/aisvr/" + instance;
						case OLD -> directory = coHome + "/JCOCubeOneServer/" + instance;
						}
						instances.add(new InstanceVO(server.host(), instance, directory));
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return instances;
	}

	@Override
	public List<InstanceVO> getInstanceListToDB(String host) {
		return serverMapper.getInstanceList(host);
	}

	/**
	 * 암호화 서버로부터 특정 인스턴스의 JCO 세팅을 가져온다
	 * 
	 * @param server   가져올 암호화 서버
	 * @param instance 가져올 인스턴스
	 */
	@Override
	public JcoSettingVO getInstanceJcoSettingFromServer(ServerVO server, String instance) throws Exception {

		Map<String, String> jcoSettings = new HashMap<>();
		OSConnector osConnector = getOSConnector(server);
		Version version = getModuleVersionToDB(server.host(), Type.AI);
		String command = Linux.GETJCOSETTING.build(version, instance);
		String result = osConnector.sendCommand(command);
		String[] lines = result.split("\\r?\\n");

		Pattern pattern = Pattern.compile("(?:jco\\.(\\w+)\\.)?(\\w+)");

		for (String line : lines) {
			String[] parts = line.split("=", 2);
			if (parts.length != 2) {
				continue;
			}

			String key = parts[0].trim();
			String value = parts[1].trim();

			Matcher matcher = pattern.matcher(key);
			if (matcher.find()) {
				String extractedKey = matcher.group(2);
				jcoSettings.put(extractedKey, value);
			}
		}
		String[] notNullParams = { "connection_count", "gwhost", "progid", "gwserv", "repository_destination",
				"worker_thread_count" };
		for (String param : notNullParams) {
			if (!jcoSettings.containsKey(param)) {
				throw new NullPointerException(
						"[getInstanceJcoSettingFromServer] JCO Missing required parameter: " + param);
			}
		}

		return new JcoSettingVO(server.host(), instance, jcoSettings.get("connection_count"), jcoSettings.get("gwhost"),
				jcoSettings.get("progid"), jcoSettings.get("gwserv"), jcoSettings.get("repository_destination"),
				jcoSettings.get("worker_thread_count"));
	}

	@Override
	public JcoSettingVO getInstanceJcoSettingFromDB(String host, String instance) {

		return serverMapper.getInstanceJcoSettingFromDB(host, instance);
	}

	/**
	 * 특정 인스턴스의 DB세팅값을 암호화 서버로부터 가져온다
	 * 
	 * @param server   DB세팅값을 가져올 암호화 서버
	 * @param instance DB세팅값을 가져올 암호화 인스턴스
	 * @return 특정 인스턴스의 DB세팅값
	 */
	@Override
	public DbSettingVO getInstanceDbSettingFromServer(@Param("host") ServerVO server, String instance)
			throws Exception {
		Map<String, String> dbSettings = new HashMap<>();

		OSConnector osConnector = getOSConnector(server);

		Version version = getModuleVersionToDB(server.host(), Type.AI);
		String command = Linux.GETDBSETTING.build(version, instance);

		String result = osConnector.sendCommand(command);
		String[] lines = result.split("\\r?\\n");

		for (String line : lines) {
			String[] parts = line.split("=", 2);
			if (parts.length != 2) {
				continue;
			}

			String key = parts[0].trim();
			String value = parts[1].trim();

			dbSettings.put(key.toLowerCase().replaceAll("[^a-zA-Z0-9]", ""), value);
		}

		String[] notNullParams = { "ip", "indicator", "port", "enclogfile", "userid", "logfile", "db", "enclog",
				"maxpoolsize", "item", "log", "dbname", "password" };
		for (String param : notNullParams) {
			if (!dbSettings.containsKey(param)) {
				throw new NullPointerException(
						"[getInstanceDbSettingFromServer] DB Setting Missing required parameter: " + param);
			}
		}

		return new DbSettingVO(server.host(), instance, dbSettings.get("ip"), dbSettings.get("indicator"),
				dbSettings.get("port"), dbSettings.get("enclogfile"), dbSettings.get("userid"),
				dbSettings.get("logfile"), dbSettings.get("db"), dbSettings.get("enclog"),
				dbSettings.get("maxpoolsize"), dbSettings.get("item"), dbSettings.get("log"), dbSettings.get("dbname"),
				dbSettings.get("password"), dbSettings.get("oldversion"), dbSettings.get("diftab"),
				dbSettings.get("oldapi"), dbSettings.get("fixuser"));

	}

	/**
	 * 데이터베이스에서 지정된 호스트와 인스턴스에 대한 설정을 가져온다.
	 *
	 * @param host     가져올 설정이 있는 호스트의 이름
	 * @param instance 가져올 설정이 있는 인스턴스의 이름
	 * @return 호스트와 인스턴스에 대한 설정 정보
	 */
	@Override
	public DbSettingVO getInstanceDbSettingFromDB(String host, String instance) {

		return serverMapper.getInstanceDbSettingFromDB(host, instance);
	}

	/**
	 * 서버로부터 지정된 인스턴스의 SAP 설정을 가져온다.
	 *
	 * @param server   SAP 설정을 가져올 서버 객체
	 * @param instance 가져올 SAP 설정이 속한 인스턴스의 이름
	 * @return 지정된 인스턴스의 SAP 설정
	 * @throws Exception 서버에서 SAP 설정을 가져오는 중에 오류가 발생한 경우
	 */
	@Override
	public SapSettingVO getInstanceSapSettingFromServer(ServerVO server, String instance) throws Exception {

		OSConnector osConnector = getOSConnector(server);

		// 각 SAP 설정 항목을 저장할 변수들을 초기화
		String lang = null;
		String client = null;
		String peakLimit = null;
		String passwd = null;
		String user = null;
		String sysnr = null;
		String poolCapacity = null;
		String asHost = null;

		// 서버로부터 SAP 설정을 가져오기 위한 명령어를 생성
		Version version = getModuleVersionToDB(server.host(), Type.AI);
		String command = Linux.GETSAPSETTING.build(version, instance);

		// 명령어를 실행하고 결과를 받아온다.
		String result = osConnector.sendCommand(command);
		String[] lines = result.split("\\r?\\n");

		// 각 설정 항목을 추출하여 변수에 저장.
		Pattern pattern = Pattern.compile("(?:jco\\.(\\w+)\\.)?(\\w+)");

		for (String line : lines) {
			String[] parts = line.split("=", 2);
			if (parts.length != 2) {
				continue;
			}

			String key = parts[0].trim();
			String value = parts[1].trim();

			Matcher matcher = pattern.matcher(key);
			if (matcher.find()) {
//				String jcoPart = matcher.group(1);
				String extractedKey = matcher.group(2);

				switch (extractedKey) {
				case "lang" -> lang = value;
				case "client" -> client = value;
				case "peak_limit" -> peakLimit = value;
				case "passwd" -> passwd = value;
				case "user" -> user = value;
				case "sysnr" -> sysnr = value;
				case "pool_capacity" -> poolCapacity = value;
				case "ashost" -> asHost = value;
				}
				;
			}
		}
	
		// SAP 설정 VO를 반환한다
		return new SapSettingVO(server.host(), instance, lang, client, peakLimit, passwd, user, sysnr, poolCapacity,
				asHost);
	}

	/**
	 * 데이터베이스로부터 지정된 호스트와 인스턴스에 대한 SAP 설정을 가져온다.
	 *
	 * @param host     가져올 설정이 있는 호스트의 이름
	 * @param instance 가져올 설정이 있는 인스턴스의 이름
	 * @return 호스트와 인스턴스에 대한 SAP 설정 정보
	 */
	@Override
	public SapSettingVO getInstanceSapSettingFromDB(String host, @Param("instance") String instance) {

		return serverMapper.getInstanceSapSettingFromDB(host, instance);
	}

	/**
	 * 암호화 서버의 JCO 설정을 데이터베이스에 삽입한다.
	 *
	 * @param 삽입할 JCO 설정 정보
	 * @return 데이터베이스에 삽입된 행의 수
	 */
	@Transactional
	@Override
	public int insertJcoSettingToDB(JcoSettingVO jco) {

		int result = serverMapper.insertJcoSettingToDB(jco);

		if (result == 0) {
			throw new RuntimeException("[insertJcosSettingToDB] insert Fail, Rollback");
		}
		return result;
	}

	/**
	 * 데이터베이스의 암호화 서버의 JCO 설정을 업데이트한다.
	 *
	 * @param jco 업데이트할 JCO 설정 정보
	 * @return 데이터베이스에서 영향 받은 행의 수
	 */
	@Transactional
	@Override
	public int updateJcoSettingToDB(JcoSettingVO jco) {

		int result = serverMapper.updateJcoSettingToDB(jco);

		if (result == 0) {
			throw new RuntimeException("[updateJcosSettingToDB] update Fail, Rollback");
		}
		return result;
	}

	/**
	 * 데이터베이스에 암호화 서버의 DB 설정을 삽입한다.
	 *
	 * @param db 삽입할 DB 설정 정보
	 * @return 데이터베이스에 삽입된 행의 수
	 */
	@Transactional
	@Override
	public int insertDbSettingToDB(DbSettingVO db) {

		int result = serverMapper.insertDbSettingToDB(db);

		if (result == 0) {
			throw new RuntimeException("[insertDbSettingToDB] Insert Fail, Rollback");
		}
		return result;
	}

	/**
	 * 데이터베이스의 암호화 서버의 DB 설정을 업데이트한다.
	 *
	 * @param db 업데이트할 DB 설정 정보
	 * @return 데이터베이스에서 영향 받은 행의 수
	 */
	@Transactional
	@Override
	public int updateDbSettingToDB(DbSettingVO db) {

		int result = serverMapper.updateDbSettingToDB(db);

		if (result == 0) {
			throw new RuntimeException("[updateDbSettingToDB] update Fail, Rollback");
		}
		return result;
	}

	/**
	 * 데이터베이스에 암호화 서버 SAP 설정을 삽입합니다.
	 *
	 * @param sap 삽입할 SAP 설정 정보
	 * @return 데이터베이스에 삽입된 행의 수
	 */
	@Transactional
	@Override
	public int insertSapSettingToDB(SapSettingVO sap) {

		int result = serverMapper.insertSapSettingToDB(sap);

		if (result == 0) {
			throw new RuntimeException("[insertSapSettingToDB] Insert Fail, Rollback");
		}
		return result;
	}

	/**
	 * 데이터베이스의 암호화 서버 SAP 설정을 업데이트합니다.
	 *
	 * @param sap 업데이트할 SAP 설정 정보
	 * @return 데이터베이스에서 영향 받은 행의 수
	 */
	@Transactional
	@Override
	public int updateSapSettingToDB(SapSettingVO sap) {

		int result = serverMapper.updateSapSettingToDB(sap);

		if (result == 0) {
			throw new RuntimeException("[updateSapSettingToDB] Update Fail, Rollback");
		}
		return result;

	}

	/**
	 * 서버로 부터 암호화 인스턴스 목록을 받아 DB에 업데이트한다.
	 *
	 * @param server    업데이트할 서버 객체. 서버는 Host(IP)로 구분한다.
	 * @param instances DB에 업데이트할 인스턴스 목록. 각 인스턴스는 유니크한 이름으로 식별한다.
	 *
	 * @return 데이터베이스에 성공적으로 업데이트된 레코드 수
	 * @exception RuntimeException DB 트랜잭션 간 에러가 발생할 경우
	 */
	@Transactional
	@Override
	public int updateInstanceList(ServerVO server, List<String> instances) {
		try {
			Set<InstanceVO> serverInstances = getInstanceListToServer(server);

			for (InstanceVO serverInstance : serverInstances) {
				String instanceName = serverInstance.instance();
				if (!instances.contains(instanceName)) {
					instances.add(instanceName);
				}
			}

			int result = serverMapper.updateInstanceList(server, instances);

			if (result == 0) {
				throw new RuntimeException("[updateInstanceList] Update Instance List Fail, Rollback");
			}
			return result;
		} catch (Exception e) {
			log.error("[updateInstanceList] Failed to update instance list: " + e.getMessage(), e);
			throw new RuntimeException("[updateInstanceList] Failed to update instance list", e);
		}
	}

	/**
	 * 서버로부터 모듈 버전을 가져온다.
	 *
	 * @param server 서버 객체. 모듈 버전을 확인할 서버를 지정한다.
	 * @return 서버에서 가져온 모듈 버전. {@link Version} 열거형을 반환한다.
	 * @throws RuntimeException 서버로부터 모듈 버전을 가져오는 데 실패한 경우 발생하거나,
	 *                          <p>
	 *                          정의되지 않은 버전을 가져올때 발생한다.
	 */
	public Version getModuleVersionToServer(ServerVO server) {

		// 암호화 서버와 연결하기 위하여 커넥터를 설정한다.
		OSConnector osConnector = getOSConnector(server);

		Version version = null;

		try {
			// 암호화 서버로부터 환경변수의 COHOME의 파일목록을 가져온다.
			String result = osConnector.sendCommand("source .bash_profile;ls -l $COHOME");

			/*
			 * 만약에 파일목록에 JCOCubeOneServer가 있다면 구형모듈, aisvr가 있다면 신형모듈. 둘다 없다면 COHOME이 환경변수에
			 * 등록되어있지 않거나, 버전을 정의할 수 없음
			 */
			if (result.contains("JCOCubeOneServer")) {
				version = Version.OLD;
			} else if (result.contains("aisvr")) {
				version = Version.NEW;
			} else {
				throw new RuntimeException("[getModuleVersionToServer] Unknown module version: " + result);
			}
		} catch (Exception e) {
			log.error("[getModuleVersionToServer] Failed to get module version from server: " + e.getMessage(), e);
			throw new RuntimeException("[getModuleVersionToServer] Failed to get module version from server", e);
		}

		return version;
	}

	/**
	 * 암호화 모듈의 버전을 DB에서 가져온다
	 * 
	 * @param host       암호화 서버의 host
	 * @param serverType 버전을 가져올 서버의 타입
	 * @return 암호화 모듈의 버전, {@link Version} 열거형을 반환한다.
	 */
	@Override
	public Version getModuleVersionToDB(@Param("host") String host, @Param("serverType") Type serverType) {
		try {
			Version version = serverMapper.getModuleVersionToDB(host, serverType);
			if (version == null) {
				throw new RuntimeException("[getModuleVersionToDB] Module version not found for host: " + host
						+ " and server type: " + serverType);
			}
			return version;
		} catch (Exception e) {
			log.error("[getModuleVersionToDB] Failed to get module version from database: " + e.getMessage(), e);
			throw new RuntimeException("[getModuleVersionToDB] Failed to get module version from database", e);
		}
	}

	/**
	 * DB에 저장된 암호화 모듈의 버전을 최신화한다
	 * 
	 * @param server  암호화 서버의 정보
	 * @param version 최신화할 암호화 모듈의 버전
	 * @return 최신화 성공 여부
	 */
	@Transactional
	@Override
	public int updateModuleVersionToDB(ServerVO server, Version version) {
		try {
			int result = serverMapper.updateModuleVersionToDB(server, version);
			if (result == 0) {
				throw new RuntimeException("[updateModuleVersionToDB] Update Module Version Fail, RollBack");
			}
			return result;
		} catch (Exception e) {
			log.error("[updateModuleVersionToDB] Update Module Version Failed: " + e.getMessage(), e);
			throw new RuntimeException("[updateModuleVersionToDB] Update Module Version Failed", e);
		}
	}

	/**
	 * 모니터링 프로그램에 필요한 모든 테이블을 순차적으로 생성한다.
	 *
	 * @return 테이블 생성 작업이 성공적으로 완료되면 1을 반환합니다.
	 * @throws RuntimeException 테이블 생성에 실패한 경우 발생합니다.
	 */
	@Transactional
	@Override
	public int createAllTable() {
		// 생성에 실패한 테이블 목록을 저장하는 리스트를 초기화.
		List<String> failedTables = new ArrayList<>();

		try {
			// 각 테이블을 순회하며 생성 메소드를 호출.
			createTable("Server", serverMapper::createServerTable, failedTables);
			createTable("Instance", serverMapper::createInstanceTable, failedTables);
			createTable("Db", serverMapper::createDbTable, failedTables);
			createTable("Jco", serverMapper::createJcoTable, failedTables);
			createTable("Sap", serverMapper::createSapTable, failedTables);
		} catch (RuntimeException e) {
			// 테이블 생성 중 예외가 발생한 경우 해당 테이블을 실패 목록에 추가.
			failedTables.add(e.getMessage());
		}

		// 생성에 실패한 테이블이 있다면 롤백을 수행하고 예외처리.
		if (!failedTables.isEmpty()) {
			String errorMessage = "[createAllTable] Create table failed for: " + String.join(", ", failedTables)
					+ ", Rollback";
			throw new RuntimeException(errorMessage);
		}
		// 모든 테이블이 성공적으로 생성되었으므로 1을 반환
		return 1;
	}

	/**
	 * 지정된 테이블을 생성한다.
	 *
	 * @param tableName        생성할 테이블의 이름
	 * @param creationFunction 테이블을 생성하는 람다 함수
	 * @param failedTables     생성에 실패한 테이블 목록
	 */
	private void createTable(String tableName, Runnable creationFunction, List<String> failedTables) {
		try {
			// 테이블 생성 함수를 실행.
			creationFunction.run();
		} catch (Exception e) {
			// 테이블 생성 실패 시 RuntimeException 발생
			throw new RuntimeException("[createTable] Error creating table " + tableName, e);
		}
	}
//	@Transactional
//	@Override
//	public int createAllTable() {
//	    ExecutorService executorService = Executors.newFixedThreadPool(5);
//	    List<Callable<String>> tasks = new ArrayList<>();
//	    tasks.add(() -> createTable("Server", serverMapper::createServerTable));
//	    tasks.add(() -> createTable("Instance", serverMapper::createInstanceTable));
//	    tasks.add(() -> createTable("Db", serverMapper::createDbTable));
//	    tasks.add(() -> createTable("Jco", serverMapper::createJcoTable));
//	    tasks.add(() -> createTable("Sap", serverMapper::createSapTable));
//
//	    try {
//	        List<Future<String>> futures = executorService.invokeAll(tasks);
//	        List<String> failedTables = new ArrayList<>();
//
//	        for (Future<String> future : futures) {
//	            try {
//	                String failedTable = future.get();
//	                if (failedTable != null) {
//	                    failedTables.add(failedTable);
//	                }
//	            } catch (ExecutionException e) {
//	                Throwable cause = e.getCause();
//	                if (cause instanceof RuntimeException) {
//	                    throw (RuntimeException) cause;
//	                } else {
//	                    throw new RuntimeException("Error while executing table creation task", cause);
//	                }
//	            }
//	        }
//
//	        if (!failedTables.isEmpty()) {
//	            String errorMessage = "[createAllTable] Create table failed for: " + String.join(", ", failedTables)
//	                    + ", Rollback";
//	            throw new RuntimeException(errorMessage);
//	        }
//
//	    } catch (InterruptedException e) {
//	        Thread.currentThread().interrupt();
//	        throw new RuntimeException("[createAllTable] Thread interrupted while waiting for table creation tasks", e);
//	    } finally {
//	        executorService.shutdown();
//	    }
//
//	    return 1;
//	}
//
//	private String createTable(String tableName, Function<ServerMapper, Integer> creationFunction) {
//	    try {
//	        creationFunction.apply(serverMapper);
//	        return null;
//	    } catch (Exception e) {
//	        return tableName;
//	    }
//	}

//	@Transactional
//	@Override
//	public String createServerTable() {
//		return createTable("Server", serverMapper::createServerTable);
//	}
//
//	@Transactional
//	@Override
//	public String createJcoTable() {
//		return createTable("Jco", serverMapper::createJcoTable);
//	}
//
//	@Transactional
//	@Override
//	public String createDbTable() {
//		return createTable("Db", serverMapper::createDbTable);
//	}
//
//	@Transactional
//	@Override
//	public String createSapTable() {
//		return createTable("Sap", serverMapper::createSapTable);
//	}
//
//	@Transactional
//	@Override
//	public String createInstanceTable() {
//		return createTable("Instance", serverMapper::createInstanceTable);
//	}

	/**
	 * 암호화된 텍스트를 복호화한다.
	 *
	 * @param server      암호화된 비밀번호를 복호화할 서버 객체
	 * @param instance    복호화 할 비밀번호가 있는 암호화 인스턴스
	 * @param encryptText 복호화 할 암호문
	 *
	 * @return 복호화된 문자열, 복호화에 실패한 경우 null을 반환한다.
	 */
	@Override
	public String decrypt(ServerVO server, String instance, String encryptText) {
		// 서버와의 연결을 설정한다.
		OSConnector osConnector = getOSConnector(server);

		// 암호화된 비밀번호를 복호화하는 데 필요한 아이템을 데이터베이스에서 가져온다.
		String item = serverMapper.getInstanceDbSettingFromDB(server.host(), instance).item();

		try {
			// 리눅스 명령어를 이용하여 암호를 복호화 시도한다.
			String result = osConnector.sendCommand(Linux.DECRYPT.build(instance, item, encryptText));
			// 결과에서 복호화된 값과 오류 코드를 추출한다.
			Matcher matcher = Pattern.compile("dVal\\s*=\\s*\\[(.*?)\\]\\s*errbyte\\s*=\\s*\\[(.*?)\\]")
					.matcher(result);
			String dVal = null;
			String errbyte = null;
			while (matcher.find()) {
				dVal = matcher.group(1);
				errbyte = matcher.group(2).trim();

				// dVal 값이 "null"이 아닌 경우 복호화가 성공했으므로 반환한다.
				if (!dVal.equals("null")) {
					return dVal;
				} else {
					// 실패했을 경우 errbyte에 따른 결과를 찍는다
					switch (errbyte) {
					case "20023":
						log.info("암호화모듈 꺼져있음");
						break;
					case "20014":
						log.info("암호화값 아님");
						break;
//					default:
//						continue;
					}

					return null;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 서버의 Os를 받아 알맞는 OS의 Connector를 반환한다.
	 *
	 * @param server 서버 정보
	 * @return OSConnector 인스턴스
	 */
	private OSConnector getOSConnector(ServerVO server) {
		if (server.serverOs().contains(OS.WINDOWS.getOs())) {
			return new WindowsConnector(server);
		} else if (server.serverOs().contains(OS.LINUX.getOs())) {
			return new LinuxConnector(server);
		}
		log.error(Module.getDescription(Module.ERR_UNKNOWNOS, server.serverOs()));
		return null;

	}

	private OSInitializer getOSInitializer(ServerVO server) {
		if (server.serverOs().contains(OS.WINDOWS.getOs())) {
			return new WindowsConfigInitializer(server);
		} else if (server.serverOs().contains(OS.LINUX.getOs())) {
			return new LinuxConfigInitializer(server);
		}
		log.error(Module.getDescription(Module.ERR_UNKNOWNOS, server.serverOs()));
		return null;
	}
}
