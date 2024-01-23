package sbt.qsecure.monitoring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class HomeController {

	@GetMapping("/")
	public String home(HttpServletRequest request) {

		HttpSession session = request.getSession();
		if (session != null && session.getAttribute("userId") != null) {
			return "main";
		}

		return "login";
	}

}
class a{
	
}
