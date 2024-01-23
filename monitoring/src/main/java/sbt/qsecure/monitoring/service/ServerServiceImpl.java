package sbt.qsecure.monitoring.service;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sbt.qsecure.monitoring.constant.Auth.AuthGrade;
import sbt.qsecure.monitoring.constant.OperationSystem;
import sbt.qsecure.monitoring.constant.Result;
import sbt.qsecure.monitoring.constant.Server;
import sbt.qsecure.monitoring.mapper.CommandMapper;
import sbt.qsecure.monitoring.mapper.ServerMapper;
import sbt.qsecure.monitoring.os.LinuxConnector;
import sbt.qsecure.monitoring.os.OSConnector;
import sbt.qsecure.monitoring.os.WindowsConnector;
import sbt.qsecure.monitoring.vo.AiServerSettingVO;
import sbt.qsecure.monitoring.vo.CommandVO;
import sbt.qsecure.monitoring.vo.ConvExitVO;
import sbt.qsecure.monitoring.vo.MemberVO;
import sbt.qsecure.monitoring.vo.ServerVO;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServerServiceImpl implements ServerService {

	private final ServerMapper serverMapper;
	private final CommandMapper commandMapper;

	/**
	 * 서버 유형에 따라 서버 목록을 반환합니다.
	 *
	 * @param serverType 서버 유형(A/I Server, Security Server, Manager Server)
	 * @return 서버 목록
	 */
	@Override
	public List<ServerVO> getServerList(Server serverType) {

		return serverMapper.getServerList(serverType);
	}

	/**
	 * 서버의 상세 정보를 JSON 형식으로 반환합니다.
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
				log.error("Error retrieving server detail information.", e);
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
	public Result startCubeOneInstance(ServerVO server, MemberVO member, AiServerSettingVO ai) {
		if (!member.authGrade().equals(AuthGrade.ADMIN.toString())) {
			return Result.NO_AUTH;
		}

		OSConnector osConnector = getOSConnector(server);
		if (osConnector != null) {
			try {
				String command = commandMapper.getCommandstartCubeOneInstance(ai.getInstance()).trim();
				String result = osConnector.sendCommand(command);
				if (!result.contains("Running JCOCubeOneServer")) {
					return Result.ERR_CUBEONE_INSTANCE;
				}
			} catch (Exception e) {
				log.error("Error starting CubeOne instance.", e);
				return Result.ERR_CUBEONE_INSTANCE;
			}
		}

		log.info("{} 이 {} {} {} {} {} 인스턴스 실행", member.managerName(), server.host(), server.serverName(),
				ai.getInstance(), ai.getSystemNumber(), ai.getClient());

		return Result.SUCCESS;
	}

	/**
	 * cubeone 인스턴스 중지를 시도한 후 int를 반환
	 * 
	 * @param server 중지를 시도할 서버
	 * @param member 중지를 시도할 멤버
	 * @param ai     중지를 시도할 인스턴스
	 * @return 명령 시도 후 성공여부
	 */
	@Override
	public Result stopCubeOneInstance(ServerVO server, MemberVO member, AiServerSettingVO ai) {
		if (!member.authGrade().equals(AuthGrade.ADMIN.toString())) {
			return Result.NO_AUTH;
		}

		OSConnector osConnector = getOSConnector(server);

		if (osConnector != null) {
			try {
				String command = commandMapper.getCommandstopCubeOneInstance(ai.getInstance()).trim();
				String result = osConnector.sendCommand(command);
				if (result.contains("Killing JCOCubeOneServer")) {
					return Result.ERR_CUBEONE_INSTANCE;
				}
			} catch (Exception e) {
				log.error("Error stop CubeOne instance.", e);
				return Result.ERR_CUBEONE_INSTANCE;
			}
		}
		log.info("{} 이 {} {} {} {} {} 인스턴스 실행", member.managerName(), server.host(), server.serverName(),
				ai.getInstance(), ai.getSystemNumber(), ai.getClient());
		return Result.SUCCESS;
	}

	/**
	 * cubeone 모듈 기동을 시도한 후 성공여부 반환
	 * 
	 * @param server 기동을 시도할 서버
	 * @param member 기동을 시도할 멤버
	 * @param ai     기동을 시도할 인스턴스
	 * @return 명령 시도 후 성공여부 (NOT_ADMIN, SUCCESS, FAIL_RUN)
	 */
	@Override
	public Result startCubeOneModule(ServerVO server, MemberVO member, AiServerSettingVO ai) {
		if (!member.authGrade().equals(AuthGrade.ADMIN.toString())) {
			return Result.NO_AUTH;
		}
		OSConnector osConnector = getOSConnector(server);
		if (osConnector != null) {
			try {
				String command = commandMapper.getCommandstartCubeOneModule(server.host()).trim();
				String result = osConnector.sendCommand(command);
				if (result.contains("Killing JCOCubeOneServer")) {
					return Result.ERR_CUBEONE_INSTANCE;
				}
			} catch (Exception e) {
				log.error("Error starting CubeOne Module.", e);
				return Result.ERR_CUBEONE_MODULE;
			}
		}
		log.info("{} 이 {} {} 암호화 모듈실행", member.managerName(), server.host(), server.serverName());
		return Result.SUCCESS;
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
	public Result stopCubeOneModule(ServerVO server, MemberVO member, AiServerSettingVO ai) {
		if (!member.authGrade().equals(AuthGrade.ADMIN.toString())) {
			return Result.NO_AUTH;
		}
		OSConnector osConnector = getOSConnector(server);
		if (osConnector != null) {
			try {
				String result = osConnector
						.sendCommand(commandMapper.getCommandstopCubeOneModule(ai.getInstance()).trim());
				if (!result.contains("수정해야함")) {
					return Result.ERR_CUBEONE_MODULE;
				}
			} catch (Exception e) {
				log.error("Error stop CubeOne Module.", e);
				return Result.ERR_CUBEONE_MODULE;
			}
		}

		return Result.SUCCESS;
	}

	@Override
	public JSONObject getTop() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject getCpuUsage(ServerVO vo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject getMemoryUsage(ServerVO vo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject getDiskUsage(ServerVO vo) {
		JSONParser parser = new JSONParser();
		JSONObject json = null;

		if (vo.serverOs().contains("Windows")) {
			WindowsConnector windows = new WindowsConnector(vo);

		} else if (vo.serverOs().contains("Linux")) {
			LinuxConnector linux = new LinuxConnector(vo);
			try {
				json = (JSONObject) parser.parse(linux.sendCommand(commandMapper.getCommandDiskUsage().trim()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return json;
	}

	@Override
	public JSONObject getProcess(ServerVO vo) {
		JSONParser parser = new JSONParser();
		JSONObject json = null;

		if (vo.serverOs().contains("Windows")) {
			WindowsConnector windows = new WindowsConnector(vo);

		} else if (vo.serverOs().contains("Linux")) {
			LinuxConnector linux = new LinuxConnector(vo);
			try {
				json = (JSONObject) parser.parse(linux.sendCommand(commandMapper.getCommandProcess().trim()));
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return json;
	}

	@Override
	public int addServerInfo(ServerVO vo, MemberVO member) {
		//
		return 0;
	}

	@Override
	public int deleteServerInfo(ServerVO vo, MemberVO member) {

		return 0;
	}

	@Override
	public int updateServerInfo(ServerVO vo, MemberVO member) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public JSONObject getCubeOneInstanceInfo(ServerVO vo, AiServerSettingVO ai) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject updateCubeOneInstanceInfo(ServerVO server, MemberVO member, AiServerSettingVO ai) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CommandVO> getAllCommand(ServerVO vos) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject readEncLog(ServerVO vo, AiServerSettingVO ai) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject readEventLog(ServerVO vo, AiServerSettingVO ai) {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * enc_event_log의 ERROR건 수를 반환합니다.
	 *
	 * @param server 서버 정보
	 * @param directory enc_event_log의 디렉토리
	 * @param date enc_event_log의 날짜
	 * @param sid 암호화를 실행한 SAP SID
	 * @param conv 암호화 Conv.exit
	 * @return enc_event_log의 암호화 중 ERROR 건 수 
	 */
	@Override
	public int getCountEncError(ServerVO server, String directory, String date, String sid, String conv) {
		String result = null;
		OSConnector osConnector = getOSConnector(server);
		if (osConnector != null) {
			try {
				 result = osConnector.sendCommand("source .bash_profile;grep -c '[ERROR]' $COHOME"+directory+"/"+date+ "_" + sid + "_ENC_" + conv + "*");
				 if (result.isBlank()) {
					result = "0";
				}
				 log.info("source .bash_profile;grep -c '[ERROR]' $COHOME"+directory+"/"+date+"_"+sid+"_ENC_"+conv+"*");
			} catch (Exception e) {
				log.error("Error Counting Error Enc.", e);
				return 1;
			}
		}
		return Integer.parseInt(result);
	}
	/**
	 * enc_event_log의 ERROR건 수를 반환합니다.
	 *
	 * @param server 서버 정보
	 * @param directory enc_event_log의 디렉토리
	 * @param date enc_event_log의 날짜
	 * @param sid 복호화를 실행한 SAP SID
	 * @param conv 복호화 Conv.exit
	 * @return enc_event_log의 복호화 중 ERROR 건 수 
	 */
	@Override
	public int getCountDecError(ServerVO server, String directory, String date, String sid, String conv) {
		String result = null;
		OSConnector osConnector = getOSConnector(server);
		if (osConnector != null) {
			try {
				 result = osConnector.sendCommand("source .bash_profile;grep -c '[ERROR]' $COHOME"+directory+"/"+date+ "_" + sid + "_ENC_" + conv + "*");
				 if (result.isBlank()) {
					result = "0";
				}
				 log.info("source .bash_profile;grep -c '[ERROR]' $COHOME"+directory+"/"+date+"_"+sid+"_DEC_"+conv+"*");
			} catch (Exception e) {
				log.error("Error Counting Error Enc.", e);
				return 1;
			}
		}
		return Integer.parseInt(result);
	}

	/**
	 * OSConnector를 반환합니다.
	 *
	 * @param server 서버 정보
	 * @return OSConnector 인스턴스
	 */
	private OSConnector getOSConnector(ServerVO server) {
		if (server.serverOs().contains(OperationSystem.WINDOWS.getOs())) {
			return new WindowsConnector(server);
		} else if (server.serverOs().contains(OperationSystem.LINUX.getOs())) {
			return new LinuxConnector(server);
		}
		return null;
	}
}
