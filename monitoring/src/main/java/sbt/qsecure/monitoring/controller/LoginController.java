package sbt.qsecure.monitoring.controller;

import static org.hamcrest.CoreMatchers.allOf;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sbt.qsecure.monitoring.constant.Server;
import sbt.qsecure.monitoring.os.LinuxConnector;
import sbt.qsecure.monitoring.service.MemberService;
import sbt.qsecure.monitoring.service.ServerService;
import sbt.qsecure.monitoring.vo.MemberVO;
import sbt.qsecure.monitoring.vo.ServerVO;

@Slf4j
@RequiredArgsConstructor
@Controller
public class LoginController {

	private final MemberService memberService;
	private final ServerService serverService;
	
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }
	@PostMapping("/login")
	public String login(@RequestParam("userId") String userId, @RequestParam("passwd") String passwd,
			HttpServletRequest request, Model model) {
		HttpSession session = request.getSession();
		MemberVO login = memberService.login(userId, passwd);
		
		try {
			if (login != null) {
				session.setAttribute("memberSequence", login.memberSequence());
				session.setAttribute("company", login.company());
				session.setAttribute("managerName", login.managerName());
				session.setAttribute("userId", login.userId());
				session.setAttribute("phoneNumber", login.phoneNumber());
				session.setAttribute("email", login.email());
				session.setAttribute("regDate", login.regDate());
				session.setAttribute("authGrade", login.authGrade().trim());
				log.info("loginSuccess User : " + login.userId());
				log.info("authGrade in session: " + session.getAttribute("authGrade"));
			}else {
				model.addAttribute("Error", "아이디 또는 비밀번호를 확인하세요");
				return "login";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
		return "redirect:/main";

	}
}
