package sbt.qsecure.monitoring.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sbt.qsecure.monitoring.checker.AIChecker;
import sbt.qsecure.monitoring.constant.Auth.AuthGrade;
import sbt.qsecure.monitoring.constant.Server;
import sbt.qsecure.monitoring.constant.Server.Command.Linux;
import sbt.qsecure.monitoring.mapper.CommandMapper;
import sbt.qsecure.monitoring.mapper.ServerMapper;
import sbt.qsecure.monitoring.os.LinuxConnector;
import sbt.qsecure.monitoring.os.OSConnector;
import sbt.qsecure.monitoring.os.WindowsConnector;
import sbt.qsecure.monitoring.vo.AiServerSettingVO;
import sbt.qsecure.monitoring.vo.CommandVO;
import sbt.qsecure.monitoring.vo.MemberVO;
import sbt.qsecure.monitoring.vo.ServerVO;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServerServiceImpl implements ServerService {

	private final ServerMapper serverMapper;
	private final CommandMapper commandMapper;

	private AIChecker checker = new AIChecker();

	/**
	 * 서버 유형에 따라 서버 목록을 반환한다.
	 *
	 * @param serverType 서버 유형(A/I Server, Security Server, Manager Server)
	 * @return 서버 목록
	 */
	@Override
	public List<ServerVO> getServerList(Server.Type serverType) {

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
	public ServerVO getServerOne(long serverSequence, Server.Type serverType) {
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
	 * @param server 기동을 시도할 서버
	 * @param member 기동을 시도할 멤버
	 * @param ai     기동을 시도할 인스턴스
	 * @return 명령 시도 후 성공 여부
	 */
	@Override
	public Server.Module startCubeOneInstance(ServerVO server, MemberVO member, AiServerSettingVO ai) {
		if (!member.authGrade().equals(AuthGrade.ADMIN.toString())) {
			return Server.Module.ERR_NOAUTH;
		}
		String instance = "jco_54";
		OSConnector osConnector = getOSConnector(server);
		if (osConnector != null) {
			try {
				String command = Server.Command.Linux.STARTINSTANCE.build("aisvr/jco_54");
				String result = osConnector.sendCommand(command);

				if (checker.isWrongPath(result)) {
					return Server.Module.ERR_WRONGPATH_INST;
				}

				if (!checker.isStartInstance(result)) {
//					log.warn(Server.Log.STARTINSTANCE.error(instance, member.managerName(), member.authGrade(),
//							server.host(), Server.Module.ERR_INSTANCE_CONTROLL.toString()));
					return Server.Module.ERR_INSTANCE_CONTROLL;
				}
			} catch (Exception e) {
//				log.error(Server.Log.STARTINSTANCE.error(instance, member.managerName(), member.authGrade(),
//						server.host(), Server.Module.ERR_INSTANCE_CONTROLL.toString()), e);
				return Server.Module.ERR_INSTANCE_CONTROLL;
			}
		}

		log.info(Server.Log.STARTINSTANCE.success(instance, member.managerName(), member.authGrade(), server.host()));

		return Server.Module.SUCCESS;
	}

	/**
	 * cubeone 인스턴스 중지를 시도한 후 결과 상수를 반환
	 * 
	 * @param server 중지를 시도할 서버VO
	 * @param member 중지를 시도할 멤버VO
	 * @param ai     중지를 시도할 인스턴스VO
	 * @return 명령 시도 후 성공여부 결과 상수
	 */
	@Override
	public Server.Module stopCubeOneInstance(ServerVO server, MemberVO member, AiServerSettingVO ai) {
		if (!member.authGrade().equals(AuthGrade.ADMIN.toString())) {
			return Server.Module.ERR_NOAUTH;
		}
		String instance = "jco_54";
		OSConnector osConnector = getOSConnector(server);

		if (osConnector != null) {
			try {
				String command = Server.Command.Linux.STARTINSTANCE.build("aisvr/jco_54");
				String result = osConnector.sendCommand(command);

				if (checker.isWrongPath(result)) {
//					log.warn(Server.Log.STOPINSTANCE.error(instance, member.managerName(), member.authGrade(),
//							server.host(), Server.Module.ERR_WRONGPATH.toString()));
					return Server.Module.ERR_WRONGPATH_INST;
				}

				if (result != null) {
					if (!checker.isStopInstance(result)) {
//						log.warn(Server.Log.STOPINSTANCE.error(instance, member.managerName(), member.authGrade(),
//								server.host(), Server.Module.ERR_INSTANCE_CONTROLL.toString()));
						return Server.Module.ERR_INSTANCE_CONTROLL;
					}
				}
			} catch (Exception e) {
//				log.error(Server.Log.STOPINSTANCE.error(instance, member.managerName(), member.authGrade(),
//						server.host(), Server.Module.ERR_INSTANCE_CONTROLL.toString()), e);
				return Server.Module.ERR_INSTANCE_CONTROLL;
			}

		}
//		log.info(Server.Log.STOPINSTANCE.success(instance, member.managerName(), member.authGrade(), server.host()));

		return Server.Module.SUCCESS;
	}

	/**
	 * cubeone 모듈 기동을 시도한 후 결과 상수를 반환
	 * 
	 * @param server 기동을 시도할 서버VO
	 * @param member 기동을 시도할 멤버VO
	 * @param ai     기동을 시도할 인스턴스VO
	 * @return 명령 시도 후 성공여부 결과 상수
	 */
	@Override
	public Server.Module startCubeOneModule(ServerVO server, MemberVO member) {
		if (member == null || server == null) {
			return Server.Module.NULL;
		}
		if (!checker.isAdmin(member.authGrade())) {
			return Server.Module.ERR_NOAUTH;
		}
		OSConnector osConnector = getOSConnector(server);

		if (osConnector != null) {
			try {
				String command = Server.Command.Linux.STARTMODULE.build();
				String result = osConnector.sendCommand(command);
				if (result == null) {
					return Server.Module.ERR_MODULE_CONTROLL;
				}
				if (checker.isWrongPath(result)) {
					return Server.Module.ERR_WRONGPATH_MODULE;
				}
				if (!checker.isStartModule(result)) {
//					log.info(Server.Log.STARTMODULE.error(member.managerName(), member.authGrade(), server.host()));
					return Server.Module.ERR_MODULE_CONTROLL;
				}
				return Server.Module.SUCCESS;
			} catch (Exception e) {
				log.error(Server.Log.STARTMODULE.error(member.managerName(), member.authGrade(), server.host()), e);
				return Server.Module.ERR_MODULE_CONTROLL;
			}
		}
//		log.info(Server.Log.STARTMODULE.success(member.managerName(), member.authGrade(), server.host()));
		return Server.Module.ERR_UNKNOWNOS;
	}

	/**
	 * cubeone 모듈 중지를 시도한 후 성공여부 반환
	 * 
	 * @param server 중지를 시도할 서버
	 * @param member 중지를 시도할 멤버
	 * @param ai     중지를 시도할 인스턴스
	 * @return 명령 시도 후 성공여부 (NOT_ADMIN, SUCCESS, FAIL_RUN)
	 */
	@Override
	public Server.Module stopCubeOneModule(ServerVO server, MemberVO member) {
		if (!checker.isAdmin(member.authGrade())) {
			return Server.Module.ERR_NOAUTH;
		}
		OSConnector osConnector = getOSConnector(server);

		if (osConnector != null) {
			try {
				String command = Server.Command.Linux.STOPMODULE.build();
				String result = osConnector.sendCommand(command);
				if (result == null) {
					return Server.Module.ERR_MODULE_CONTROLL;
				}
				if (checker.isWrongPath(result)) {
					return Server.Module.ERR_WRONGPATH_MODULE;
				}

				if (!checker.isStopModule(result)) {
					return Server.Module.ERR_MODULE_CONTROLL;
				}
			} catch (Exception e) {
				log.error(Server.Log.STOPMODULE.error(member.managerName(), member.authGrade(), server.host()), e);
				return Server.Module.ERR_MODULE_CONTROLL;
			}
		}

		return Server.Module.SUCCESS;
	}

	@Override
	public String getTop() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getCpuUsage(ServerVO server) {
		String cpuUsage = null;
		OSConnector osConnector = getOSConnector(server);
		if (osConnector != null) {
			try {
				String command = Server.Command.Linux.CPU_USAGE.build();
//				log.info(command);
				cpuUsage = osConnector.sendCommand(command);

				if (cpuUsage == null) {
					return 0.0;
				}
//				log.info(Server.Log.GETCPUUSAGE.success(server.host()));
			} catch (Exception e) {
				log.error(Server.Log.GETCPUUSAGE.error(server.host()), e);
				return 0.0;
			}
		}

		return Double.parseDouble(cpuUsage);
	}

	@Override
	public double getMemoryUsage(ServerVO server) {
		String memoryUsage = null;

		OSConnector osConnector = getOSConnector(server);
		if (osConnector != null) {
			try {
				String command = Server.Command.Linux.MEMORY_USAGE.build();
				memoryUsage = osConnector.sendCommand(command);

				if (memoryUsage == null) {
					return 0.0;
				}
			} catch (Exception e) {
//				log.error(Server.Log.GETMEMORYUSAGE.error(server.host()), e);
				return 0.0;
			}
			return Double.parseDouble(memoryUsage);
		}

		return 0.0;
	}

	@Override
	public double getDiskUsage(ServerVO server) {
		String diskUsage = null;

		OSConnector osConnector = getOSConnector(server);

		try {
			String command = Server.Command.Linux.CPU_USAGE.build();
			diskUsage = osConnector.sendCommand(command);
			if (diskUsage == null) {
				return 0.0;
			}
		} catch (Exception e) {
			log.error(Server.Log.GETDISKUSAGE.error(server.host()), e);
			return 0.0;
		}
		return Double.parseDouble(diskUsage);
	}

	@Override
	public List<Map<String, String>> getProcess(ServerVO server, String sortType) {

		List<Map<String, String>> processList = new ArrayList<>();
		OSConnector osConnector = getOSConnector(server);
		if (osConnector != null) {
			try {
				String command = Server.Command.Linux.GETPROCESS.build("cpu");
				String result = osConnector.sendCommand(command);
				if (!checker.isWrongPath(result)) {
					String[] lines = result.split("\n");
					for (int i = 1; i < lines.length; i++) {
						String[] parts = lines[i].trim().split("\\s+");

						Map<String, String> process = new LinkedHashMap<>();
						process.put("user", parts[0]);
						process.put("pid", parts[1]);
						process.put("cpu", parts[2]);
						process.put("memory", parts[3]);
						process.put("vsz", parts[4]);
						process.put("rss", parts[5]);
						process.put("tty", parts[6]);
						process.put("status", parts[7]);
						process.put("start", parts[8]);
						process.put("time", parts[9]);
						process.put("command", parts[10]);

						processList.add(process);
					}
					return processList;
				} else {
					log.warn(Server.Log.GETPROCESS.error(server.host()));
				}
			} catch (Exception e) {
				log.error(Server.Log.GETPROCESS.error(server.host()), e);
				return null;
			}
		}
		return null;
	}

	@Override
	public int addServerInfo(ServerVO server) {
		return serverMapper.addServerInfo(server);
	}

	@Override
	public int deleteServerInfo(ServerVO server) {
		return serverMapper.deleteServerInfo(server);
	}

	@Override
	public int updateServerInfo(ServerVO server) {
		return serverMapper.updateServerInfo(server);
	}

	@Override
	public JSONObject getCubeOneInstanceInfo(ServerVO server, AiServerSettingVO ai) {
		return null;
	}

	@Override
	public JSONObject updateCubeOneInstanceInfo(ServerVO server, MemberVO member, AiServerSettingVO ai) {
		// TODO Auto-generated method stub
		return null;
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
	 * @param sid       암호화를 실행한 SAP SID
	 * @param conv      암호화 Conv.exit
	 * @return enc_event_log의 암호화 중 ERROR 건 수
	 */
	@Override
	public String getCountEncError(ServerVO server, String directory, String date, String sid) {
		String result = null;
		DecimalFormat df = new DecimalFormat("###,###");
		int encErrorCount = 0;
		OSConnector osConnector = getOSConnector(server);
		if (osConnector != null) {
			try {
				String command = Server.Command.Linux.COUNT_ENC_ERR.build(directory, date, sid);
				log.info(command);
				result = osConnector.sendCommand(command);

				if (checker.isWrongPath(result)) {
					return String.valueOf(encErrorCount);
				}
				encErrorCount = Integer.parseInt(result.trim());

			} catch (NumberFormatException e) {
				log.error("[getCountEncError] Failed to parse result to integer: {}", e.getMessage());
				return null;
			} catch (Exception e) {
				log.error("[getCountEncError] Failed to execute command for server: {}", server.host(), e);
				return null;
			}
		}
		return df.format(encErrorCount);
	}

	/**
	 * enc_event_log의 복호화 ERROR건 수를 반환한다.
	 *
	 * @param server    서버 정보
	 * @param directory enc_event_log의 디렉토리
	 * @param date      enc_event_log의 날짜
	 * @param sid       복호화를 실행한 SAP SID
	 * @param conv      복호화 Conv.exit
	 * @return enc_event_log의 복호화 중 ERROR 건 수
	 */
	@Override
	public String getCountDecError(ServerVO server, String directory, String date, String sid) {
		String result = null;
		DecimalFormat df = new DecimalFormat("###,###");
		OSConnector osConnector = getOSConnector(server);
		if (osConnector != null) {
			try {
				String command = Server.Command.Linux.COUNT_DEC_ERR.build(directory, date, sid);
				result = osConnector.sendCommand(command);
				if (checker.isWrongPath(result)) {
					result = "0";
				}
			} catch (Exception e) {
				log.error("Error Counting Error Enc.", e);
				return null;
			}
		}
		return df.format(Integer.parseInt(result));
	}

	@Override
	public Server.Module cotest(ServerVO server, String instance) {
		OSConnector osConnector = getOSConnector(server);

		String[] checks = { "enc", "sap", "db" };
		String path = "aisvr/" + instance;
		path = checker.addMissingFlash(path);
		for (String type : checks) {
			try {
				String command = Server.Command.Linux.COTEST.build(path, type);
				log.info(command);
				String result = osConnector.sendCommand(command);

				if (result != null) {
					if (checker.isSuccess(result)) {
						continue;
					} else if (checker.isWrongPath(result)) {
						return Server.Module.ERR_WRONGPATH_INST;
					}
					switch (type) {
					case "enc":
						return Server.Module.ERR_MODULE_TEST;
					case "sap":
						return Server.Module.ERR_SAP_TEST;
					case "db":
						log.info("retry db");
						return retryConnectDb(osConnector, path);
					}
				}
			} catch (Exception e) {
				log.error("Error cotest.", e);
				return Server.Module.ERR_COTEST;
			}
		}
		return Server.Module.SUCCESS;
	}

	private Server.Module retryConnectDb(OSConnector osConnector, String path) {
		path = this.checker.addMissingFlash(path);
		try {
			String command = Server.Command.Linux.COTEST.retry(path);
			String result = osConnector.sendCommand(command);
			if (this.checker.isSuccess(result)) {
				log.info("Retry DB Success");
				return Server.Module.SUCCESS;
			}
			return Server.Module.ERR_AIDB_TEST;
		} catch (Exception e) {
			return Server.Module.ERR_COTEST;
		}
	}

	/**
	 * 서버의 Os를 받아 알맞는 OS의 Connector를 반환한다.
	 *
	 * @param server 서버 정보
	 * @return OSConnector 인스턴스
	 */
	private OSConnector getOSConnector(ServerVO server) {
		if (server.serverOs().contains(Server.OS.WINDOWS.getOs())) {
			return new WindowsConnector(server);
		} else if (server.serverOs().contains(Server.OS.LINUX.getOs())) {
			return new LinuxConnector(server);
		}
		return null;
	}

}
