package sbt.qsecure.monitoring;

import static org.mockito.ArgumentMatchers.contains;

import java.lang.ProcessHandle.Info;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sbt.qsecure.monitoring.constant.Result;
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

//	  
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
		List<ServerVO> list = serverService.getServerList(Server.AI);
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
		List<ServerVO> aiServerList = serverService.getServerList(Server.AI);

		ServerVO testServer = aiServerList.get(0);
		List<CommonSettingVO> settings = settingService.getCommonSettingList();
		LocalDate currentDate = LocalDate.of(2023, 9, 10);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

		settings.forEach(setting -> {
			AtomicInteger iterationCount = new AtomicInteger(0);

			IntStream.rangeClosed(0, 11).mapToObj(i -> currentDate.minusDays(i).format(formatter))
					.forEach(formattedDate -> {
						String errorCount = serverService.getCountEncError(testServer, setting.encLogDirectory() + "/",
								formattedDate, setting.sid());

						log.info("{} 반복횟수 {} 디렉토리 : {} 에러 건수: {} 날짜 : {}", testServer.host(),
								iterationCount.incrementAndGet(), setting.encLogDirectory(), errorCount, formattedDate);

					});
		});
	}

	@Test
	public void 코테스트한다() {

		ServerVO server = serverService.getServerList(Server.AI).get(0);
		LinuxConnector osConnector = new LinuxConnector(server);

		switch (server.version()) {
		case "NEW":
			log.info(getCotestResult(osConnector, "aisvr/jco_54").toString());
			break;
		case "OLD":
			log.info(getCotestResult(osConnector, "JCOCubeOneServer/JCO54").toString());
			break;
		}

	}

	private Result getCotestResult(OSConnector osConnector, String path) {

		String[] checks = { "enc", "sap", "db" };

		for (String type : checks) {
			try {
				String command = String.format("source .bash_profile;$COHOME/%s/cotest.sh %s", path, type);
				String result = osConnector.sendCommand(command);
				if (result != null) {
					log.info(result);

					if (isSuccess(result)) {
						log.info(type + " is ok");
						continue;
					} else if (result.isBlank() || result == "") {
						return Result.ERR_WRONGPATH;
					}
					switch (type) {
					case "enc":
						return Result.ERR_MODULE;
					case "sap":
						return Result.ERR_SAP;
					case "db":
						log.info("retry db");
						return reTryConnectDb(osConnector, path);
					}
				}

			} catch (Exception e) {
				log.error("Error cotest.", e);
				return Result.ERR_COTEST;
			}
		}
		return Result.SUCCESS;

	}

	private Result reTryConnectDb(OSConnector osConnector, String path) {
		try {
			String result = osConnector.sendCommand(String.format("source .bash_profile;$COHOME/%s/cotest.sh ora", path));
			if (isSuccess(result)) {
				log.info("success");
				return Result.SUCCESS;
			}
			return Result.ERR_AIDB;
		} catch (Exception e) {
			return Result.ERR_COTEST;
		}

	}

	private boolean isSuccess(String result) {
		return (result !=null && (result.contains("Hello SAP") || result.contains("complete") || result.contains("ok"))
				&& !result.contains("failed"));
	}
}
