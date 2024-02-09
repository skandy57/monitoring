package sbt.qsecure.monitoring.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sbt.qsecure.monitoring.checker.AIChecker;
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

	@ResponseBody
	@GetMapping("/getCpuAllServerAvg")
	public String getCpuAllServerAvg(Model model, HttpSession session, RedirectAttributes redirectAttributes)
			throws Exception {
		double totalCpu = 0;
		int serverCount = 0;
		try {
			List<ServerVO> aiServers = serverService.getServerList(Server.Type.AI);

			for (ServerVO aiServer : aiServers) {
				totalCpu += serverService.getCpuUsage(aiServer);
				serverCount++;
			}
			String averageCpu = (serverCount != 0) ? String.valueOf(totalCpu / serverCount) : "0";

			return averageCpu;
		} catch (Exception e) {
			session.invalidate();
			redirectAttributes.addFlashAttribute("error", "서버 목록을 가져오는 중에 오류가 발생했습니다. 다시 로그인해주세요.");
			return "redirect:/login.html";
		}
	}

	@ResponseBody
	@GetMapping("/getCpuUsage")
	public String getCpuUsage(@RequestParam("sequence") Long sequence, HttpSession session,
			RedirectAttributes redirectAttributes) {
		try {
			ServerVO aiServer = serverService.getServerOne(sequence, Server.Type.AI);

			String cpuUsage = String.valueOf(serverService.getCpuUsage(aiServer));

			return cpuUsage;
		} catch (Exception e) {
			session.invalidate();
			redirectAttributes.addFlashAttribute("error", "서버 목록을 가져오는 중에 오류가 발생했습니다. 다시 로그인해주세요.");
			return "redirect:/login.html";
		}
	}
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
	@GetMapping("/getMemoryAllServerAvg")
	public String getMemoryAllServerAvg(HttpSession session, RedirectAttributes redirectAttributes) throws Exception {

		double totalMemory = 0;
		int serverCount = 0;

		try {
			List<ServerVO> aiServers = serverService.getServerList(Server.Type.AI);

			for (ServerVO aiServer : aiServers) {
				totalMemory += serverService.getMemoryUsage(aiServer);
				serverCount++;
			}
			String averageMemory = (serverCount != 0) ? String.valueOf(totalMemory / serverCount) : "0";

			return averageMemory;
		} catch (Exception e) {
			session.invalidate();
			redirectAttributes.addFlashAttribute("error", "서버 목록을 가져오는 중에 오류가 발생했습니다. 다시 로그인해주세요.");
			return "redirect:/login.html";
		}
	}

	@ResponseBody
	@GetMapping("getMemoryUsage")
	public double getMemoryUsage(@RequestParam("sequence") Long sequence) {

		ServerVO aiServer = serverService.getServerOne(sequence, Server.Type.AI);
		double memoryUsage = serverService.getCpuUsage(aiServer);

		return memoryUsage;
	}

//	@ResponseBody
//	@GetMapping("/getCpuUsage")
//	public String getProcess() {
//		return null;	
//	}

	@ResponseBody
	@GetMapping("getDiskUsage")
	public double getDiskUsage(@RequestParam("sequence") Long sequence) {

		ServerVO aiServer = serverService.getServerOne(sequence, Server.Type.AI);
		double diskUsage = serverService.getCpuUsage(aiServer);

		return diskUsage;
	}

	@ResponseBody
	@GetMapping("/getDiskAllServerAvg")
	public double getDiskAllServerAvg() throws Exception {
		double totalDisk = 0;
		int serverCount = 0;

		List<ServerVO> aiServers = serverService.getServerList(Server.Type.AI);

		for (ServerVO aiServer : aiServers) {
			totalDisk += serverService.getDiskUsage(aiServer);
			serverCount++;
		}

		double averageDisk = (serverCount != 0) ? (totalDisk / serverCount) : 0.0;

		return averageDisk;
	}

	@ResponseBody
	@GetMapping("/cotestAll")
	public Module cotestAll() {

		String host = null;

		List<ServerVO> aiServers = serverService.getServerList(Server.Type.AI);
		for (ServerVO aiServer : aiServers) {
			if (!Server.Module.SUCCESS.equals(serverService.cotest(aiServer, null))) {

			}
		}

		return null;
	}

	@ResponseBody
	@GetMapping("startCubeOneModule")
	public Module startCubeOneModule() {

		return null;

	}
}
