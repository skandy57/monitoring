package sbt.qsecure.monitoring;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
import sbt.qsecure.monitoring.constant.Server;
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
		String convertion = conv.conversionExit();
		
		int result = serverService.getCountEncError(server, dir, "20230906", sid , "ZBAN2");
		
		System.out.println("result: "+ result);
		
	}

}
