package sbt.qsecure.monitoring.controller;

import java.lang.ProcessHandle.Info;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sbt.qsecure.monitoring.constant.Server;
import sbt.qsecure.monitoring.constant.Server.Type;
import sbt.qsecure.monitoring.os.LinuxConnector;
import sbt.qsecure.monitoring.os.OSConnector;
import sbt.qsecure.monitoring.service.ServerService;
import sbt.qsecure.monitoring.service.SettingService;
import sbt.qsecure.monitoring.vo.ConvExitVO;
import sbt.qsecure.monitoring.vo.ServerVO;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MainController {

	private final ServerService serverService;
	private final SettingService settingService;

	@GetMapping("/main")
	public String main(Model model, HttpSession session, RedirectAttributes redirectAttributes) throws Exception {
		try {
			Optional.ofNullable(session.getAttribute("userId"))
					.orElseThrow(() -> new IllegalStateException("사용자가 로그인되어 있지 않습니다."));

			ServerVO server = serverService.getServerOne(1, Type.AI);

//			OSConnector osConnector = new LinuxConnector(server);
//
//			if (!osConnector.isConnected()) {
//				session.invalidate();
//				redirectAttributes.addFlashAttribute("Error", "서버 목록을 가져오는 중에 오류가 발생했습니다. 다시 로그인해주세요.");
//				return "redirect:/login";
//			}

			List<ServerVO> aiServerList = serverService.getServerList(Type.AI);
			model.addAttribute("aiServerList", aiServerList);

			List<ServerVO> securityServerList = serverService.getServerList(Type.SECURITY);
			model.addAttribute("securityServerList", securityServerList);

//		ServerVO testServer = aiServerList.get(0);
//			List<CommonSettingVO> settings = settingService.getCommonSettingList();
//		CommonSettingVO setting = settings.get(0);
			List<ConvExitVO> convs = settingService.getConvExitList();
//		ConvExitVO conv = convs.get(0);

//		LocalDate currentDate = LocalDate.now();
//
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
//
//		for (int i = 11; i >= 0; i--) {
//			LocalDate date = currentDate.minusDays(i);
//			String formattedDate = date.format(formatter);
//
//		}
			LocalDate currentDate = LocalDate.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
//
//			for (ServerVO server : aiServerList) {
//				for (CommonSettingVO setting : settings) {
//					for (ConvExitVO conv : convs) {
//						for (int i = 11; i >= 0; i--) {
//							LocalDate date = currentDate.minusDays(i);
//							String formattedDate = date.format(formatter);
//
//							String errorCount = serverService.getCountEncError(server, setting.encLogDirectory(),
//									formattedDate, setting.sid());
//							if (errorCount == null) {
//								log.info("Error count for server {} with setting {} and conv {}: {}", server.host(),
//										setting.encLogDirectory(), conv.conversionExit(), errorCount);
//								session.invalidate();
//								redirectAttributes.addFlashAttribute("Error", "서버 목록을 가져오는 중에 오류가 발생했습니다. 다시 로그인해주세요.");
//								return "redirect:/login.html";
//							}
//
//						}
//					}
//				}
//			}
		} catch (IllegalStateException e) {
			model.addAttribute("Error", "세션이 만료되어 재로그인 해주세요");
			return "redirect:/login";
		}

		return "main";

//        ServerVO testServer = serverList.get(0);
//        System.out.println(testServer);

//        LinuxConnector linux = new LinuxConnector(testServer);
//        try {
//			System.out.println(linux.sendCommand("pwd"));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

//        for (ServerVO server : serverList) {
//        	System.out.println(server);
//            try {
//                LinuxConnector linux = new LinuxConnector(server);
//                if (server.getServerOs().contains("Unix")) {
////					System.out.println(linux.sendCommand("free | awk '/^Mem:/ {printf(\"사용 중인 메모리 : %s\\n\", $3)}'"));
//				}
////                System.out.println(linux.sendCommand("mpstat 1 1 | awk '$12 ~ /[0-9.]+/ {printf(\"CPU 사용률 : %.1f%%\\n\", 100 - $12)}'"));
////                System.out.println(linux.sendCommand("free | awk '/^Mem:/ {printf(\"메모리 사용률 : %.1f%%\\n\", ($3/$2)*100)}'"));
////                System.out.println(linux.sendCommand("source .bash_profile; df -h $COHOME | awk 'NR==2 {printf(\"Disk 사용률 : %s\", $5)}'"));
////                System.out.println(linux.sendCommand("ps aux --sort=-rss --no-headers | awk '{\r\n"
////                		+ "        printf \"{\\\"USER\\\":\\\"%s\\\",\\\"PID\\\":\\\"%s\\\",\\\"%CPU\\\":\\\"%s\\\",\\\"%MEM\\\":\\\"%s\\\",\\\"VSZ\\\":\\\"%s\\\",\\\"RSS\\\":\\\"%s\\\",\\\"TTY\\\":\\\"%s\\\",\\\"STAT\\\":\\\"%s\\\",\\\"START\\\":\\\"%s\\\",\\\"TIME\\\":\\\"%s\\\",\\\"COMMAND\\\":\\\"%s %s %s %s %s\\\"},\\n\",\r\n"
////                		+ "        $1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12, $13, $14, $15\r\n"
////                		+ "    }'"));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
	}
}
