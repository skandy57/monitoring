package sbt.qsecure.monitoring.filter;

//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LoginFilter 
//extends UsernamePasswordAuthenticationFilter 
{

//	private final AuthenticationManager authenticationManager;
//
//	public LoginFilter(AuthenticationManager authenticationManager) {
//
//		this.authenticationManager = authenticationManager;
//	}
//
//	@Override
//	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
//
//		String userId = obtainUsername(request);
//		String passwd = obtainPassword(request);
//
//		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userId, passwd,
//				null);
//
//		return authenticationManager.authenticate(authToken);
//	}
//
//	@Override
//	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
//			Authentication authentication) {
//
//	}
//
//	@Override
//	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
//			AuthenticationException failed) {
//
//	}
}
