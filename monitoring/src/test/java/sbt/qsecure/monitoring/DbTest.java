package sbt.qsecure.monitoring;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sbt.qsecure.monitoring.constant.Auth.AuthGrade;
import sbt.qsecure.monitoring.checker.AIChecker;
import sbt.qsecure.monitoring.constant.Server;
import sbt.qsecure.monitoring.os.LinuxConnector;
import sbt.qsecure.monitoring.os.OSConnector;
import sbt.qsecure.monitoring.service.MemberService;
import sbt.qsecure.monitoring.service.ServerService;
import sbt.qsecure.monitoring.service.SettingService;
import sbt.qsecure.monitoring.vo.CommonSettingVO;
import sbt.qsecure.monitoring.vo.ConvExitVO;
import sbt.qsecure.monitoring.vo.MemberVO;
import sbt.qsecure.monitoring.vo.ServerVO;

@SpringBootTest
@Slf4j
@RequiredArgsConstructor
public class DbTest {

	@Autowired
	private MemberService memberService;
	@Autowired
	private ServerService serverService;
	@Autowired
	private SettingService settingService;

	@Test
	public void 로그인테스트() {
		MemberVO login = memberService.login("root", "1234");
		System.out.println(login);
	}

	@Test
	public void VO테스트() {

		List<CommonSettingVO> settings = settingService.getCommonSettingList();
		List<ConvExitVO> conv = settingService.getConvExitList();

		System.out.println(settings);
		log.info(conv.toString());
	}

	@Test
	public void 로그카운트한다() {
		List<ServerVO> list = serverService.getServerList(Server.Type.AI);
		List<CommonSettingVO> list1 = settingService.getCommonSettingList();
		List<ConvExitVO> list2 = settingService.getConvExitList();
		ServerVO server = list.get(0);
		CommonSettingVO setting = list1.get(0);
		ConvExitVO conv = list2.get(0);
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String dir = setting.encLogDirectory();
		String sid = setting.sid();
		log.info(sid);

		String result = serverService.getCountEncError(server, dir, "20230906", sid);

		System.out.println("result: " + result);

	}

	@Test
	public void 로그반복카운트한다() {
		List<ServerVO> aiServerList = serverService.getServerList(Server.Type.AI);

		ServerVO testServer = aiServerList.get(0);
		List<CommonSettingVO> settings = settingService.getCommonSettingList();
		log.info(settings.toString());
		LocalDate currentDate = LocalDate.of(2023, 9, 10);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

		int iterationCount = 0;

		for (int i = 0; i <= 11; i++) {
			String formattedDate = currentDate.minusDays(i).format(formatter);

			settings.forEach(setting -> {
				
				String errorCount = serverService
						.getCountEncError(testServer, setting.encLogDirectory(), formattedDate, setting.sid()).trim();

			});
			iterationCount++;
		}
	}

	@Test
	public void 코테스트한다() {

		ServerVO server = serverService.getServerList(Server.Type.AI).get(0);
		CommonSettingVO setting = settingService.getCommonSettingList().get(0);

		log.info(serverService.cotest(server, "jco_54").toString());

	}



	@Test
	public void 모듈기동() throws Exception {
		MemberVO member = memberService.getMemberById("root");
		log.info(member.toString());
		log.info(member.authGrade());
		String authGrade = member.authGrade();
		if (authGrade == "ADMIN") {
			log.info("권한없음");
		}
		ServerVO server = serverService.getServerOne(1, Server.Type.AI);
		log.info(server.toString());
		LinuxConnector osConnector = new LinuxConnector(server);
		String command = Server.Command.Linux.STARTMODULE.build();
		AIChecker checker = new AIChecker();
		String result = osConnector.sendCommand(command);
		if (!checker.isStartModule(result)) {
			log.info(Server.Log.STARTMODULE.error(member.managerName(), member.authGrade(), server.host()));
		}
		log.info(Server.Log.STARTMODULE.success(member.managerName(), member.authGrade(), server.host()));
	}

	@Test
	public void 모듈중지() throws Exception {
		MemberVO member = memberService.getMemberById("root");
		AIChecker checker = new AIChecker();
		log.info(member.toString());
		log.info(member.authGrade());
		String authGrade = member.authGrade();
		if (checker.isAdmin(authGrade)) {
			log.info("권한없음");
		}
		ServerVO server = serverService.getServerOne(1, Server.Type.AI);
		log.info(server.toString());
		LinuxConnector osConnector = new LinuxConnector(server);
		String command = Server.Command.Linux.STOPMODULE.build();

		String result = osConnector.sendCommand(command);
		if (!checker.isStopModule(result)) {
			log.info(Server.Log.STOPMODULE.error(member.managerName(), member.authGrade(), server.host()));
		}
		log.info(Server.Log.STOPMODULE.success(member.managerName(), member.authGrade(), server.host()));
	}

	@Test
	public void 인스턴스기동() throws Exception {
		MemberVO member = memberService.getMemberById("root");
		AIChecker checker = new AIChecker();
		ServerVO server = serverService.getServerOne(1, Server.Type.AI);
		log.info(server.toString());
		LinuxConnector osConnector = new LinuxConnector(server);

		String command = Server.Command.Linux.STARTINSTANCE.build("aisvr/jco_54");

		String result = osConnector.sendCommand(command);
		log.info(result);
		if (!checker.isStartInstance(result)) {
			log.info(Server.Log.STARTINSTANCE.error("jco_54", member.managerName(), member.authGrade(), server.host()));
		}
		log.info(Server.Log.STARTINSTANCE.success("jco_54", member.managerName(), member.authGrade(), server.host()));
	}

	@Test
	public void 인스턴스중지() throws Exception {
		MemberVO member = memberService.getMemberById("root");
		AIChecker checker = new AIChecker();
		ServerVO server = serverService.getServerOne(1, Server.Type.AI);
		LinuxConnector osConnector = new LinuxConnector(server);

		String command = Server.Command.Linux.STOPINSTANCE.build("aisvr/jco_54");

		String result = osConnector.sendCommand(command);
		if (!checker.isStopInstance(result)) {
			log.info(Server.Log.STOPINSTANCE.error("jco_54", member.managerName(), member.authGrade(), server.host()));
		}
		log.info(Server.Log.STOPINSTANCE.success("jco_54", member.managerName(), member.authGrade(), server.host()));
	}
	
	@Test
	public void 프로세스목록가져온다() throws Exception {
		ServerVO server = new ServerVO(2, null, null, "192.168.0.231", "test", "sprtms55", 22, "Linux", Server.Type.AI, "NEW");
		LinuxConnector osConnector = new LinuxConnector(server);
		

		for (int i = 0; i <= 10; i++) {
			List<Map<String, String>> result = serverService.getProcess(server, null);
			if (result != null) {
		        for (Map<String, String> map : result) {
		            StringBuilder sb = new StringBuilder();
		            sb.append("{");
		            for (Map.Entry<String, String> entry : map.entrySet()) {
		                sb.append(entry.getKey())
		                  .append(": ")
		                  .append(entry.getValue())
		                  .append(", ");
		            }
		            if (!map.isEmpty()) {
		                sb.setLength(sb.length() - 2);
		            }
		            sb.append("}");
		            log.info(sb.toString());
		        }
		    } else {
		        log.error("프로세스 목록을 가져오는 데 실패했습니다.");
		    }
		}
	
	}
}
