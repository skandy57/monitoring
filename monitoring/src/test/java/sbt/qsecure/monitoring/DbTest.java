package sbt.qsecure.monitoring;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.xml.bind.DatatypeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sbt.qsecure.monitoring.checker.AIValidator;
import sbt.qsecure.monitoring.connector.LinuxConfigInitializer;
import sbt.qsecure.monitoring.connector.LinuxConnector;
import sbt.qsecure.monitoring.constant.Server;
import sbt.qsecure.monitoring.constant.Server.Module;
import sbt.qsecure.monitoring.constant.Server.Type;
import sbt.qsecure.monitoring.mapper.ServerMapper;
import sbt.qsecure.monitoring.service.MemberService;
import sbt.qsecure.monitoring.service.ServerService;
import sbt.qsecure.monitoring.service.SettingService;
import sbt.qsecure.monitoring.vo.DbSettingVO;
import sbt.qsecure.monitoring.vo.InstanceVO;
import sbt.qsecure.monitoring.vo.JcoSettingVO;
import sbt.qsecure.monitoring.vo.SapSettingVO;
import sbt.qsecure.monitoring.vo.ConvExitVO;
import sbt.qsecure.monitoring.vo.MemberVO;
import sbt.qsecure.monitoring.vo.ServerVO;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

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
	@Autowired
	private ServerMapper serverMapper;

	@Test
	public void 로그인테스트() {
		MemberVO login = memberService.login("root", "1234");
		System.out.println(login);
	}

//	@Test
//	public void VO테스트() {
//
//		List<CommonSettingVO> settings = settingService.getCommonSettingList();
//		List<ConvExitVO> conv = settingService.getConvExitList();
//
//		System.out.println(settings);
//		log.info(conv.toString());
//	}

//	@Test
//	public void 로그카운트한다() {
//		List<ServerVO> list = serverService.getServerList(Server.Type.AI);
//		List<CommonSettingVO> list1 = settingService.getCommonSettingList();
//		List<ConvExitVO> list2 = settingService.getConvExitList();
//		ServerVO server = list.get(0);
//		CommonSettingVO setting = list1.get(0);
//		ConvExitVO conv = list2.get(0);
//		Date now = new Date();
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//		String dir = setting.encLogDirectory();
//		String sid = setting.sid();
//		log.info(sid);
//
//		String result = serverService.getCountEncError(server, dir, "20230908", sid);
//
//		System.out.println("result: " + result);
//
//	}

	@Test
	public void 로그반복카운트한다() {
		List<ServerVO> aiServerList = serverService.getServerList(Server.Type.AI);
		log.info(LocalDate.now().toString());
		ServerVO testServer = aiServerList.get(0);
		List<InstanceVO> instances = serverService.getInstanceListToDB(testServer.host());
		for (InstanceVO instance : instances) {
			DbSettingVO dbsetting = serverService.getInstanceDbSettingFromDB(testServer.host(), instance.instance());

			LocalDate currentDate = LocalDate.of(2023, 9, 10);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

			int iterationCount = 0;

			String[] convs = { "ZCARD", "ZBNKN" };

			for (int i = 0; i <= 11; i++) {
				String formattedDate = currentDate.minusDays(i).format(formatter);

				Arrays.stream(convs).forEach(setting -> {

					String errorCount = serverService.getCountEncError(testServer, dbsetting.encLogFile(), formattedDate).trim();
					log.info(errorCount);
				});
				iterationCount++;
			}
		}

	}

	@Test
	public void 코테스트한다() {

		ServerVO server = serverService.getServerList(Server.Type.AI).get(0);
//		CommonSettingVO setting = settingService.getCommonSettingList().get(0);
		InstanceVO instance = serverService.getInstanceListToDB(server.host()).get(0);

		log.info(serverService.cotest(server, instance).toString());

	}

	@Test
	public void 코테스트를동적으로한다() {
		ServerVO aiServer = serverService.getServerOne(1, Type.AI);

		Set<InstanceVO> instanceList = serverService.getInstanceListToServer(aiServer);

		for (InstanceVO instance : instanceList) {
			log.info(serverService.cotest(aiServer, instance).toString());

		}
	}

	@Test
	public void 모듈기동() throws Exception {
		MemberVO member = memberService.getMemberById("root");
//		log.info(member.toString());
//		log.info(member.authGrade());
//		String authGrade = member.authGrade();
//		if (authGrade == "ADMIN") {
//			log.info("권한없음");
//		}
		ServerVO server = serverService.getServerOne(1, Server.Type.AI);
		log.info(serverService.startCubeOneModule(server, member).toString());
	}

	@Test
	public void 모듈중지() throws Exception {
		MemberVO member = memberService.getMemberById("root");
//		AIChecker checker = new AIChecker();
//		log.info(member.toString());
//		log.info(member.authGrade());
//		String authGrade = member.authGrade();
//		if (checker.isAdmin(authGrade)) {
//			log.info("권한없음");
//		}
		ServerVO server = serverService.getServerOne(1, Server.Type.AI);
		log.info(serverService.stopCubeOneModule(server, member).toString());
	}

	@Test
	public void 인스턴스기동() throws Exception {
		MemberVO member = memberService.getMemberById("root");
		AIValidator checker = new AIValidator();
		ServerVO server = serverService.getServerOne(1, Server.Type.AI);
		log.info(server.toString());
		LinuxConnector osConnector = new LinuxConnector(server);

//		String command = Server.Command.Linux.STARTINSTANCE.build("aisvr/jco_54");
//
//		String result = osConnector.sendCommand(command);

		log.info(serverService.startCubeOneInstance(server, member, "jco_54").toString());
//		if (!checker.isStartInstance(result)) {
//			log.info(Server.Log.STARTINSTANCE.error("jco_54", member.managerName(), member.authGrade(), server.host()));
//		}
//		log.info(Server.Log.STARTINSTANCE.success("jco_54", member.managerName(), member.authGrade(), server.host()));
	}

	@Test
	public void 인스턴스기동을동적으로한다() {
		MemberVO member = memberService.getMemberById("root");
		ServerVO server = serverService.getServerOne(1, Server.Type.AI);

		for (InstanceVO instance : serverService.getInstanceListToDB(server.host())) {
			log.info(instance.instance() + serverService.startCubeOneInstance(server, member, instance.instance()).toString());
		}

	}

	@Test
	public void 인스턴스중지() throws Exception {
		MemberVO member = memberService.getMemberById("root");
		ServerVO server = serverService.getServerOne(1, Server.Type.AI);

		for (InstanceVO instance : serverService.getInstanceListToDB(server.host())) {
			log.info(instance.instance().toUpperCase() + " " + serverService.stopCubeOneInstance(server, member, instance.directory()).toString());
		}
	}

	@Test
	public void 프로세스목록가져온다() throws Exception {
		ServerVO server = serverService.getServerOne(1, Server.Type.AI);
//		LinuxConnector osConnector = new LinuxConnector(server);

//		for (int i = 0; i <= 10; i++) {
//			List<ProcessVO> result = serverService.getProcess(server, null);
//			if (result != null) {
//				for (ProcessVO process : result) {
//                    
//                }
//		    } else {
//		        log.error("프로세스 목록을 가져오는 데 실패했습니다.");
//		    }
//		}
		log.info(serverService.getProcess(server, "cpu").toString());

	}

	@Test
	public void 호출한메소드명을가져온다() {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		if (stackTrace.length >= 3) {
			StackTraceElement caller = stackTrace[1];
			String className = caller.getClassName();
			String methodName = caller.getMethodName();

			// 클래스 이름에서 패키지 부분을 추출
			Pattern pattern = Pattern.compile("([\\w$]+\\.)*([\\w$]+)");
			Matcher matcher = pattern.matcher(className);
			if (matcher.find()) {
				String packageName = matcher.group(1);
				// 패키지 이름과 클래스 이름을 결합하여 호출한 메소드명 생성
				String fullMethodName = packageName + methodName;
				log.info(fullMethodName);
			} else {
				log.error("패키지 이름을 추출할 수 없습니다.");
			}
		} else {
			log.error("메소드명을 가져올 수 없습니다.");
		}

	}

	/**
	 * 
	 */
	@Test
	public void 디렉토리목록가져온다() {
		ServerVO server = serverService.getServerOne(1, Server.Type.AI);
		LinuxConfigInitializer osConnector = new LinuxConfigInitializer(server);
		log.info(osConnector.getInstanceDirectoryList().toString());
	}

	@Test
	public void DB세팅가져온다() throws Exception {
		ServerVO server = serverService.getServerOne(1, Server.Type.AI);
		log.info(new DbSettingVO("1", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null)
				.toString());

		log.info(serverService.getInstanceDbSettingFromServer(server, "jco_54").toString());
	}

	@Test
	public void SAP세팅가져온다() throws Exception {
		ServerVO server = serverService.getServerOne(1, Server.Type.AI);
		log.info(serverService.getInstanceSapSettingFromServer(server, "jco_54").toString());
	}

	@Test
	public void JCO세팅가져온다() throws Exception {
		ServerVO server = serverService.getServerOne(1, Server.Type.AI);
		log.info(serverService.getInstanceJcoSettingFromServer(server, "jco_54").toString());
	}

	@Test
	public void JCO세팅가져와서DB에저장한다() throws Exception {
		ServerVO server = serverService.getServerOne(1, Server.Type.AI);
		JcoSettingVO jco = serverService.getInstanceJcoSettingFromServer(server, "jco_54");
		log.info(jco.toString());
		int result = serverMapper.insertJcoSettingToDB(jco);
		log.info(String.valueOf(result));
	}

	@Test
	public void 전체인스턴스의세팅들을서버에서가져와서DB에저장한다() throws Exception {
		ServerVO server = serverService.getServerOne(1, Server.Type.AI);

		Set<InstanceVO> instances = serverService.getInstanceListToServer(server);
		log.info(instances.toString());
		for (InstanceVO instance : instances) {
			SapSettingVO sap = serverService.getInstanceSapSettingFromServer(server, instance.instance());
			DbSettingVO db = serverService.getInstanceDbSettingFromServer(server, instance.instance());
			JcoSettingVO jco = serverService.getInstanceJcoSettingFromServer(server, instance.instance());
			log.info(db.toString());
			log.info(jco.toString());
			log.info(sap.toString());
			serverMapper.insertInstanceSettingToDB(instance);
			serverMapper.insertSapSettingToDB(sap);
			serverMapper.insertDbSettingToDB(db);
			serverMapper.insertJcoSettingToDB(jco);

		}

	}

	@Test
	public void 전체서버리스트를가져온다() {
		log.info(serverService.getServerList(Type.AI).toString());
	}

	@Test
	public void 전체테이블을생성한다() {
		int result = serverService.createAllTable();
		log.info(String.valueOf(result));
	}

	@Test
	public void DB에서모든세팅값을가져온다() {
		ServerVO server = serverService.getServerOne(1, Type.AI);
		log.info(serverService.getInstanceSapSettingFromDB(server.host(), "jco_54").toString());
		log.info(serverService.getInstanceJcoSettingFromDB(server.host(), "jco_54").toString());
		log.info(serverService.getInstanceDbSettingFromDB(server.host(), "jco_54").toString());

	}

	@Test
	public void pwd() throws Exception {
		ServerVO server = serverService.getServerOne(1, Type.AI);
		LinuxConnector c = new LinuxConnector(server);
		log.info(c.sendCommand("pwd"));
	}

	@Test
	public void 복호화한다() {
		ServerVO server = serverService.getServerOne(1, Type.AI);
		List<InstanceVO> instanceList = serverService.getInstanceListToDB(server.host());
		for (InstanceVO instance : instanceList) {
			String password = serverMapper.getInstanceSapSettingFromDB(server.host(), instance.instance()).passwd();
			String decryptText = serverService.decrypt(server, instance.instance(), password);
			log.info("현재인스턴스 : " + instance.instance() + " 암호화 값 : " + password + " 복호화 값 : " + decryptText);
		}
	}

	@Test
	public void 여러개의명령어를던져결과값들을받아온다() throws Exception {
		ServerVO server = serverService.getServerOne(1, Type.AI);
		LinuxConnector c = new LinuxConnector(server);

		List<String> list = c.sendCommandList(new String[] { "ls", "pwd" });

		int count = 0;
		for (String result : list) {
			count++;
			log.info(count + ". " + result);
		}

	}

	@Test
	public void 파일을가져온다() throws Exception {
		ServerVO server = serverService.getServerOne(1, Server.Type.AI);
		LinuxConnector c = new LinuxConnector(server);
		List<InstanceVO> instanceList = serverService.getInstanceListToDB(server.host());
		for (InstanceVO instance : instanceList) {
			String eventLogPath = serverService.getInstanceDbSettingFromDB(server.host(), instance.instance()).logFile();
			c.getEventLogFile(instance.instance(), eventLogPath, "C:\\Users\\Administrator\\event_log_" + instance.instance());

		}
	}

	@Test
	public void testDecryptionWithPaddingRemoval() throws Exception {

		// Encryption key
		String encryptionKeyString = "AB86464BE335402CBB0E429437C85445BCB8DA3C174FD47B1D89AF5CBC5B3B2B";
		byte[] encryptionKeyBytes = Hex.decodeHex(encryptionKeyString);
		SecretKeySpec secretKeySpec = new SecretKeySpec(encryptionKeyBytes, "AES");

		// Initialization vector
		String ivString = "00000000000000000000000000000000";
		byte[] ivBytes = Hex.decodeHex(ivString);
		IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);

		// Encrypted message
		byte[] encryptedMessageBytes = Hex.decodeHex("AC5A37E5CADB4F81DC21E4C81D4996E2FB3D44E06AC6986BF0789A084246DBCD");

		// Decrypt the message
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
		byte[] decryptedMessageBytes = cipher.doFinal(encryptedMessageBytes);

		// Remove padding
//		byte[] unpaddedData = removePadding(decryptedMessageBytes);

		// Convert decrypted bytes to string
		String decryptedMessage = new String(decryptedMessageBytes, "UTF-8");
		System.out.println("Decrypted message: " + decryptedMessage);

		// Add your assertion here if necessary
//		assertEquals("AAAABBBB", decryptedMessage);
	}

//	public static byte[] removePadding(byte[] data) {
//		if (data.length == 0) {
//			throw new IllegalArgumentException("Input data cannot be empty");
//		}
//
//		// 마지막 바이트에서 패딩의 길이를 확인합니다.
//		int padLength = data[data.length - 1];
//
//		// 패딩의 길이가 유효한지 확인합니다.
//		if (padLength < 1 || padLength > data.length) {
//			throw new IllegalArgumentException("Invalid padding length");
//		}
//
//		// 패딩을 제외한 데이터의 길이를 계산합니다.
//		int unpaddedLength = data.length - padLength;
//
//		// 패딩을 제외한 데이터를 반환합니다.
//		return Arrays.copyOfRange(data, 0, unpaddedLength);
//	}
}
