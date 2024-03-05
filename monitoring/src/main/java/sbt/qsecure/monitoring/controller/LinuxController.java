package sbt.qsecure.monitoring.controller;

import java.io.IOException;
import java.sql.Date;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sbt.qsecure.monitoring.checker.AIValidator;
import sbt.qsecure.monitoring.connector.LinuxConnector;
import sbt.qsecure.monitoring.constant.Auth;
import sbt.qsecure.monitoring.constant.Server;
import sbt.qsecure.monitoring.constant.Server.Module;
import sbt.qsecure.monitoring.constant.Server.Type;
import sbt.qsecure.monitoring.constant.Auth.AuthGrade;
import sbt.qsecure.monitoring.service.ServerService;
import sbt.qsecure.monitoring.vo.DbSettingVO;
import sbt.qsecure.monitoring.vo.InstanceVO;
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
	public String getCpuAllServerAvg(Model model, HttpSession session, RedirectAttributes redirectAttributes) throws Exception {
		double totalCpu = 0;
		int serverCount = 0;
		try {
			List<ServerVO> aiServers = serverService.getServerList(Type.AI);

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
	public String getCpuUsage(@RequestParam("serverSequence") long serverSequence, Model model, RedirectAttributes redirectAttributes) {
		try {
			ServerVO aiServer = serverService.getServerOne(serverSequence, Type.AI);

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
//	@GetMapping("/getCountEncErrorAllServer")
//	public String getCountEncErrorAllServer(HttpSession session, RedirectAttributes redirectAttributes) {
//
//		// 서버 시퀀스 목록을 세션에서 가져옵니다.
//		@SuppressWarnings("unchecked")
//		List<Long> serverSequenceList = (List<Long>) session.getAttribute("serverSequenceList");
//
//		// 암호화 오류 발생 횟수를 저장할 변수.
//		AtomicInteger errCount = new AtomicInteger(0);
//
//		// 각 서버 시퀀스에 대해 병렬 처리.
//		serverSequenceList.parallelStream().forEach(serverSequence -> {
//			// 서버 시퀀스로부터 해당 AI 서버 정보를 가져온다.
//			ServerVO aiServer = serverService.getServerOne(serverSequence, Type.AI);
//
//			// AI 서버의 인스턴스 목록을 가져온다.
//			List<InstanceVO> instanceList = serverService.getInstanceListToDB(aiServer.host());
//
//			// 현재 날짜를 가져온다.
//			LocalDate currentDate = LocalDate.now();
//			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
//			String formatDate = currentDate.format(formatter);
//
//			// 각 인스턴스에 대해 반복한다.
//			for (InstanceVO instance : instanceList) {
//				// 인스턴스의 데이터베이스 설정에서 로그 파일 경로를 가져온다.
//				DbSettingVO dbSetting = serverService.getInstanceDbSettingFromDB(aiServer.host(), instance.instance());
//
//				// 로그 파일에서 오늘 날짜를 기준으로 암호화 오류 발생 횟수를 가져와 errCount에 더한다.
//				errCount.addAndGet(Integer.parseInt(serverService.getCountEncError(aiServer, dbSetting.encLogFile(), formatDate)));
//			}
//		});
//
//		// 발생한 암호화 오류 횟수를 포맷팅하여 반환합니다.
//		DecimalFormat df = new DecimalFormat("###,###");
//		return df.format(errCount);
//	}
//	
	@ResponseBody
	@GetMapping("/getCountEncErrorAllServer")
	public String getCountEncErrorAllServer(HttpSession session, HttpServletResponse response) throws IOException {
        // 서버 시퀀스 목록을 세션에서 가져온다.
        @SuppressWarnings("unchecked")
        List<Long> serverSequenceList = (List<Long>) session.getAttribute("serverSequenceList");

        if (serverSequenceList == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Server sequence list is null");
            return null;
        }

        // 암호화 오류 발생 횟수를 저장할 변수.
        AtomicInteger errCount = new AtomicInteger(0);

        // 현재 날짜를 가져온다.
        LocalDate currentDate = LocalDate.now();
        String formatDate = currentDate.format(DateTimeFormatter.BASIC_ISO_DATE);

        // CompletableFuture를 이용하여 비동기적으로 작업을 처리한다.
        List<CompletableFuture<Object>> futures = serverSequenceList.stream()
                .map(serverSequence -> CompletableFuture.supplyAsync(() -> {
                    // 서버 시퀀스로부터 해당 AI 서버 정보를 가져온다.
                    ServerVO aiServer = serverService.getServerOne(serverSequence, Type.AI);

                    if (aiServer == null) {
                        return null;
                    }

                    // AI 서버의 인스턴스 목록을 가져옵니다.
                    List<InstanceVO> instanceList = serverService.getInstanceListToDB(aiServer.host());

                    // 각 인스턴스에 대해 반복한다.
                    for (InstanceVO instance : instanceList) {
                        // 인스턴스의 데이터베이스 설정에서 로그 파일 경로를 가져온다.
                        DbSettingVO dbSetting = serverService.getInstanceDbSettingFromDB(aiServer.host(), instance.instance());

                        if (dbSetting != null) {
                            // 로그 파일에서 오늘 날짜를 기준으로 암호화 오류 발생 횟수를 가져와 errCount에 더한다.
                            int count = Integer.parseInt(serverService.getCountEncError(aiServer, dbSetting.encLogFile(), formatDate));
                            errCount.addAndGet(count);
                        }
                    }
                    return null;
                }))
                .collect(Collectors.toList());

        // CompletableFuture들이 모두 완료될 때까지 대기한다.
        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        try {
            allOf.get(); // 모든 작업이 완료될 때까지 대기한다.
        } catch (InterruptedException | ExecutionException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error occurred during execution: " + e.getMessage());
            return null;
        }

        // 발생한 암호화 오류 횟수를 포맷팅하여 반환한다.
        DecimalFormat df = new DecimalFormat("###,###");
        return df.format(errCount.get());
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
			List<ServerVO> aiServers = serverService.getServerList(Type.AI);

			for (ServerVO aiServer : aiServers) {
				try {
					totalMemory += serverService.getMemoryUsage(aiServer);
				} catch (Exception e) {
					totalMemory += 0;
				}
				serverCount++;
			}
			String averageMemory = (serverCount != 0) ? String.format("%.1f", totalMemory / serverCount) : "0";

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
	public String getMemoryUsage(@RequestParam("serverSequence") long serverSequence, Model model, RedirectAttributes redirectAttributes) {
		try {
			ServerVO aiServer = serverService.getServerOne(serverSequence, Type.AI);
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

		ServerVO aiServer = serverService.getServerOne(serverSequence, Type.AI);
		String diskUsage = String.valueOf(serverService.getCpuUsage(aiServer));

		return diskUsage;
	}

	@ResponseBody
	@GetMapping("/getDiskAllServerAvg")
	public String getDiskAllServerAvg() throws Exception {
		double totalDisk = 0;
		int serverCount = 0;

		List<ServerVO> aiServers = serverService.getServerList(Type.AI);

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

		List<ServerVO> aiServers = serverService.getServerList(Type.AI);

		for (ServerVO aiServer : aiServers) {
			List<InstanceVO> instances = serverService.getInstanceListToDB(aiServer.host());
			for (InstanceVO instance : instances) {
				String[] serverInfo = { aiServer.host(), String.valueOf(aiServer.port()) };
				Module result = serverService.cotest(aiServer, instance);
				switch (result != null ? result : Module.NULL) {
				case SUCCESS:
					successServers.add(aiServer);
					successMessages.add(Module.getDescription(result, serverInfo));
					continue;
				default:
					failedServers.add(aiServer);
					errorMessages.add(Module.getDescription(result, serverInfo));
					break;
				}
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
	public String cotest(@Param("serverSequence") long serverSequence, String instanceName, Model model) {

		ServerVO aiServer = serverService.getServerOne(serverSequence, Type.AI);

		String[] serverInfo = { aiServer.host(), String.valueOf(aiServer.port()) };

		InstanceVO instanceList = serverService.getInstanceListToDB(aiServer.host()).get(0);

//		if (instanceList.stream().noneMatch(instance -> instance.instance().equals(instanceName))) {
//			return null;
//		}

		Module result = serverService.cotest(aiServer, instanceList);

		model.addAttribute("result", Module.getDescription(result, serverInfo));

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
		String[] serverInfo = { aiServer.host(), String.valueOf(aiServer.port()) };
		MemberVO member = new MemberVO(memberSequence, company, managerName, userId, null, phoneNumber, email, regDate, authGrade);

		String message = null;
		Module result = serverService.startCubeOneModule(aiServer, member);
		switch (result != null ? result : Module.NULL) {
		case SUCCESS -> message = Module.getDescription(result, serverInfo);
		default -> message = Module.getDescription(result, serverInfo);
		}
		model.addAttribute("message", message);
		return message;

	}
}
