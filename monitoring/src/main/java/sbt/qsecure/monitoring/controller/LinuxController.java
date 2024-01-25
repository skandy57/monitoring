package sbt.qsecure.monitoring.controller;

import java.util.List;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sbt.qsecure.monitoring.constant.Auth;
import sbt.qsecure.monitoring.constant.Server;
import sbt.qsecure.monitoring.constant.Auth.AuthGrade;
import sbt.qsecure.monitoring.os.LinuxConnector;
import sbt.qsecure.monitoring.service.ServerService;
import sbt.qsecure.monitoring.vo.AiServerSettingVO;
import sbt.qsecure.monitoring.vo.ServerVO;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/server/*")
public class LinuxController {

	private final ServerService serverService;

//	
//	
//	@ResponseBody
//	@GetMapping("/getServerDetailInfo")
//	public String getServerDetailInfo(ServerVO vo) {
//		return null;
//		
//	}
//	@ResponseBody
//	@GetMapping("/getCubeOneInstanceInfo")
//	public String getCubeOneInstanceInfo(ServerVO vo) {
//		return null;
//		
//	}
//	@ResponseBody
//	@GetMapping("/updateCubeOneInstanceInfo")
//	public String updateCubeOneInstanceInfo(ServerVO vo) {
//		return null;
//		
//	}
//	@ResponseBody
//	@GetMapping("/getCubeOneInstanceInfo")
//	public String startCubeOneInstance(ServerVO serverVO, HttpSession session, AiServerSettingVO aiVO) {
//		return null;
//		
//	}
//	
//	
	@ResponseBody
	@GetMapping("/getCpuUsage")
	public double getCpuUsage(Model model) throws Exception {
		List<ServerVO> aiServerList = serverService.getServerList(Server.AI);

		ServerVO testServer = aiServerList.get(0);
		LinuxConnector linux = new LinuxConnector(testServer);

		String cpuUsage = linux.sendCommand("mpstat | awk '/all/ {print 100 - $NF}'");
		log.info(cpuUsage);

		return Double.parseDouble(cpuUsage);

	}

	@ResponseBody
	@GetMapping("/getMemoryUsage")
	public double getMemoryUsage() throws Exception {
		List<ServerVO> aiServerList = serverService.getServerList(Server.AI);

		ServerVO testServer = aiServerList.get(0);
		LinuxConnector linux = new LinuxConnector(testServer);

		String memoryUsage = linux.sendCommand("free | awk '/Mem:/ {used = $2 - $4 - $6; print used / $2 * 100}'");

		return (Math.round(Double.parseDouble(memoryUsage) * 100) / 100.0);

	}
//	
//	@ResponseBody
//	@GetMapping("/getCpuUsage")
//	public String getProcess() {
//		return null;
//		
//	}
//	

	@ResponseBody
	@GetMapping("/getDiskUsage")
	public String getDiskUsage() throws Exception {
		int totalDiskUsage = 0;
		int serverCount = 0;

		List<ServerVO> aiServers = serverService.getServerList(Server.AI);

		for (ServerVO aiServer : aiServers) {
			totalDiskUsage += Integer.parseInt(serverService.getDiskUsage(aiServer));
			serverCount++;
		}

		double averageDiskUsage = (serverCount != 0) ? ((double) totalDiskUsage / serverCount) : 0.0;

		return Double.toString(averageDiskUsage);
	}

	@ResponseBody
	@GetMapping("/cotest")
	public String cotest() {

		ServerVO aiServer = serverService.getServerList(Server.AI).get(0);

		return null;

	}

}
