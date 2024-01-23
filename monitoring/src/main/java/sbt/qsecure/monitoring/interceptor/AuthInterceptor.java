package sbt.qsecure.monitoring.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import sbt.qsecure.monitoring.constant.Auth;
import sbt.qsecure.monitoring.constant.Auth.AuthGrade;

//import sbt.qsecure.monitoring.interceptor.Auth.Role;


@RequiredArgsConstructor
@Component
public class AuthInterceptor implements HandlerInterceptor{
	
	  @Override
	    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
	            throws Exception {
	        if (handler instanceof HandlerMethod) {
	            HandlerMethod handlerMethod = (HandlerMethod) handler;
	            Auth auth = handlerMethod.getMethodAnnotation(Auth.class);

	            if (auth != null) {
	            	AuthGrade requiredAuth = auth.authGrade();
	            	
	            	if (request.getSession().getAttribute("authGrade")==AuthGrade.NOT_LOGIN){
	            		response.sendError(HttpServletResponse.SC_FORBIDDEN, "로그인을 해주세요");
	            	}
						
					
	                if (!checkAuth(request, requiredAuth)) {
	                	response.sendError(HttpServletResponse.SC_FORBIDDEN, "권한이 없습니다");
						return false;
					}
	            }
	        }
	        return true; 
	    }

	    @Override
	    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
	            ModelAndView modelAndView) throws Exception {

	    }

	    @Override
	    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
	            throws Exception {

	    }
	
	    private boolean checkAuth(HttpServletRequest request, AuthGrade requiredAuth) {
	        AuthGrade userAuth = (AuthGrade) request.getSession().getAttribute("authGrade");

	        return userAuth != null && userAuth == requiredAuth;
	    }
	
}
