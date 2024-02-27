package sbt.qsecure.monitoring;

import java.io.InputStream;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sbt.qsecure.monitoring.constant.Server;
import sbt.qsecure.monitoring.constant.Server.Type;
import sbt.qsecure.monitoring.os.LinuxConnector;
import sbt.qsecure.monitoring.service.ServerService;
import sbt.qsecure.monitoring.vo.ServerVO;


@SpringBootTest
@Slf4j
@RequiredArgsConstructor
public class LinuxTest {
	private JSch jsch = new JSch();
	private Channel channel;
	@Autowired
	private ServerService serverService;
	
	@Test
	public void 연결테스트() throws Exception {
		Session session;
		session = jsch.getSession("test", "192.168.0.135", 22);
		session.setPassword("1234");
		session.setConfig("StrictHostKeyChecking", "no");
		session.connect(5000);
		if (session.isConnected()) {
			System.out.println("=============================");
			System.out.println("연결성공");
			System.out.println("=============================");
		}
		if (channel == null || !channel.isConnected()) {
			channel = session.openChannel("exec");
		}
		System.out.println(channel.isConnected());
		((ChannelExec) channel).setCommand("source ~/.bash_profile;$COHOME/bin/cubeone.sh stop API");

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
			System.out.println(result.toString());
		}
	}
	@Test
	public void getMemoryUsage() throws Exception {
		List<ServerVO> aiServerList = serverService.getServerList(Server.Type.AI);

		ServerVO testServer = aiServerList.get(0);
		LinuxConnector linux = new LinuxConnector(testServer);
		
//		String memoryUsage = linux.sendCommand("free | awk '/Mem:/ {used = $2 - $4 - $6; print used / $2 * 100}'");
		String result = linux.sendCommand("source .bash_profile;ls -l $COHOME/aisvr");
		log.info(result);
	
		
	}
	@Test
	public void 에러건수를가져온다() throws Exception {
		List<ServerVO> aiServerList = serverService.getServerList(Server.Type.AI);

		ServerVO testServer = aiServerList.get(0);
		LinuxConnector linux = new LinuxConnector(testServer);
//		Date nowDate = new Date();
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//		System.out.println(sdf.format(nowDate));
		log.info(linux.sendCommand("source .bash_profile;grep -c '[ERROR]' $COHOME/aisvr/log/test.*"));
		log.info(linux.sendCommand("source .bash_profile;grep -c '[ERROR]' $COHOME/aisvr/log/20230906_S54_ENC_ZBAN2_*"));
	}
}
