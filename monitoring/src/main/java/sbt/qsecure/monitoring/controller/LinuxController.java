package sbt.qsecure.monitoring.controller;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
import sbt.qsecure.monitoring.vo.MemberVO;
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
				try {
					totalCpu += serverService.getCpuUsage(aiServer);
				} catch (Exception e) {
					totalCpu += 0;
				}
				serverCount++;
			}
			String averageCpu = (serverCount != 0) ? String.valueOf(totalCpu / serverCount) : "0";

			return averageCpu;
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("Error", "모든 A/I서버의 Cpu사용량을 가져오는 중에 오류가 발생했습니다.");
			if (model.getAttribute("cpuUsage") != null) {
				return String.valueOf(model.getAttribute("cpuUsage"));
			}
		}
		return null;
	}

	@ResponseBody
	@GetMapping("/getCpuUsage")
	public String getCpuUsage(@RequestParam("serverSequence") Long serverSequence, Model model,
			RedirectAttributes redirectAttributes) {
		try {
			ServerVO aiServer = serverService.getServerOne(serverSequence, Server.Type.AI);

			String cpuUsage = String.valueOf(serverService.getCpuUsage(aiServer));

			return cpuUsage;
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("Error", "Cpu사용량을 가져오는 중에 오류가 발생했습니다.");
			if (model.getAttribute("memoryUsage") != null) {
				return String.valueOf(model.getAttribute("cpuUsage"));
			} else {
				return "0";
			}
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
	public String getMemoryAllServerAvg(Model model, RedirectAttributes redirectAttributes) throws Exception {

		double totalMemory = 0;
		int serverCount = 0;

		try {
			List<ServerVO> aiServers = serverService.getServerList(Server.Type.AI);

			for (ServerVO aiServer : aiServers) {
				try {
					totalMemory += serverService.getMemoryUsage(aiServer);
				} catch (Exception e) {
					totalMemory += 0;
				}
				serverCount++;
			}
			String averageMemory = (serverCount != 0) ? String.valueOf(totalMemory / serverCount) : "0";

			return averageMemory;
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("Error", "모든 A/I서버의 메모리사용량을 가져오는 중에 오류가 발생했습니다.");
			if (model.getAttribute("memoryUsage") != null) {
				return String.valueOf(model.getAttribute("memoryUsage"));
			}
			return "0";
		}
	}

	@ResponseBody
	@GetMapping("getMemoryUsage")
	public String getMemoryUsage(@RequestParam("serverSequence") long serverSequence, Model model,
			RedirectAttributes redirectAttributes) {
		try {
			ServerVO aiServer = serverService.getServerOne(serverSequence, Server.Type.AI);
			String memoryUsage = String.valueOf(serverService.getCpuUsage(aiServer));

			return memoryUsage;
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("Error", "메모리사용량을 가져오는 중에 오류가 발생했습니다.");
			if (model.getAttribute("memoryUsage") != null) {
				return String.valueOf(model.getAttribute("memoryUsage"));
			}
			return "0";
		}
	}

//	@ResponseBody
//	@GetMapping("/getCpuUsage")
//	public String getProcess() {
//		return null;	
//	}

	@ResponseBody
	@GetMapping("getDiskUsage")
	public String getDiskUsage(@RequestParam("sequence") long serverSequence) {

		ServerVO aiServer = serverService.getServerOne(serverSequence, Server.Type.AI);
		String diskUsage = String.valueOf(serverService.getCpuUsage(aiServer));

		return diskUsage;
	}

	@ResponseBody
	@GetMapping("/getDiskAllServerAvg")
	public String getDiskAllServerAvg() throws Exception {
		double totalDisk = 0;
		int serverCount = 0;

		List<ServerVO> aiServers = serverService.getServerList(Server.Type.AI);

		for (ServerVO aiServer : aiServers) {
			totalDisk += serverService.getDiskUsage(aiServer);
			serverCount++;
		}

		String averageDisk = (serverCount != 0) ? String.valueOf((totalDisk / serverCount)) : "0";

		return averageDisk;
	}

//	@ResponseBody
//	@GetMapping("/cotestAll")
//	public ResponseEntity<Map<String, Object>> cotestAll() {
//		List<ServerVO> aiServers = serverService.getServerList(Server.Type.AI);
//		Map<String, Object> response = new HashMap<>();
//		List<Map<String, Object>> successServers = new ArrayList<>();
//		List<Map<String, Object>> failedServers = new ArrayList<>();
//
//		aiServers.parallelStream().forEach(aiServer -> {
//			Map<String, Object> serverInfo = new HashMap<>();
//			serverInfo.put("host", aiServer.host());
//			serverInfo.put("port", aiServer.port());
//
//			try {
//				Server.Module result = serverService.cotest(aiServer, "jco_54");
//				serverInfo.put("message", Server.Module.getDescription(result,
//						new String[] { aiServer.host(), String.valueOf(aiServer.port()) }));
//
//				if (result == Server.Module.SUCCESS) {
//					synchronized (successServers) {
//						successServers.add(serverInfo);
//					}
//				} else {
//					synchronized (failedServers) {
//						failedServers.add(serverInfo);
//					}
//				}
//			} catch (Exception e) {
//				synchronized (failedServers) {
//					serverInfo.put("message", "Error occurred during testing: " + e.getMessage());
//					failedServers.add(serverInfo);
//				}
//			}
//		});
//
//		if (failedServers.isEmpty()) {
//			response.put("result", "success");
//			response.put("servers", successServers);
//		} else {
//			response.put("result", "failure");
//			response.put("servers", failedServers);
//		}
//
//		return ResponseEntity.ok(response);
//	}
	@ResponseBody
	@GetMapping("/cotestAll")
	public List<ServerVO> cotestAll(Model model) {

		List<ServerVO> successServers = new ArrayList<>();
		List<ServerVO> failedServers = new ArrayList<>();

		List<String> successMessages = new ArrayList<>();
		List<String> errorMessages = new ArrayList<>();

		List<ServerVO> aiServers = serverService.getServerList(Server.Type.AI);
		for (ServerVO aiServer : aiServers) {
			String[] serverInfo = { aiServer.host(), String.valueOf(aiServer.port()) };
			Server.Module result = serverService.cotest(aiServer, "jco_54");
			switch (result != null ? result : Server.Module.NULL) {
			case SUCCESS:
				successServers.add(aiServer);
				successMessages.add(Server.Module.getDescription(result, serverInfo));
				continue;
			default:
				failedServers.add(aiServer);
				errorMessages.add(Server.Module.getDescription(result, serverInfo));
				break;
			}
		}
		if (failedServers.isEmpty()) {
			model.addAttribute("result", successServers);
			model.addAttribute("message", successMessages);
			return successServers;
		}
		model.addAttribute("result", failedServers);
		model.addAttribute("message", errorMessages);

		return failedServers;
	}
	@ResponseBody
	@GetMapping("/cotest")
	public String cotest(@Param("serverSequence")long serverSequence ,Model model) {
		
		ServerVO aiServer = serverService.getServerOne(serverSequence, Server.Type.AI);
		
		String[] serverInfo = { aiServer.host(), String.valueOf(aiServer.port()) };
		
		Server.Module result = serverService.cotest(aiServer, "jco_54");
		
		model.addAttribute("result", Server.Module.getDescription(result, serverInfo));
		
		
		
		return null;
		
	}

	@ResponseBody
	@GetMapping("startCubeOneModule")
	public String startCubeOneModule(@Param("serverSequence") long serverSequence, HttpSession session, Model model) {
		long memberSequence = (long) session.getAttribute("memberSequence");
		String company = (String) session.getAttribute("company");
		String managerName = (String) session.getAttribute("managerName");
		String userId = (String) session.getAttribute("userId");
		String phoneNumber = (String) session.getAttribute("phoneNumber");
		String email = (String) session.getAttribute("email");
		Date regDate = (Date) session.getAttribute("regDate");
		String authGrade = (String) session.getAttribute("authGrade");

		ServerVO aiServer = serverService.getServerOne(serverSequence, Server.Type.AI);
		MemberVO member = new MemberVO(memberSequence, company, managerName, userId, null, phoneNumber, email, regDate,
				authGrade);

		String message = null;
		Server.Module result = serverService.startCubeOneModule(aiServer, member);
		switch (result != null ? result : Server.Module.NULL) {
		case SUCCESS -> message = "기동 성공";
		case ERR_NOAUTH -> message = "해당 기능 사용 권한이 없는 유저입니다";
		case ERR_MODULE_CONTROLL -> message = "모듈 제어 간 오류가 발생하였습니다";
		case ERR_WRONGPATH_MODULE -> message = "잘못된 암호화 모듈 경로 입니다. 암호화 모듈 경로를 수정 해주세요.";
		case ERR_UNKNOWNOS -> message = "잘못된 암호화 OS 세팅입니다. WASDB내 암호화 OS 세팅을 수정해주세요";
		case NULL -> message = "NullPointerException이 발생하였습니다. WAS <-> WASDB간 통신을 확인해주세요.";
		default -> message = "A";
		}
		model.addAttribute("message", message);
		return null;

	}
}
