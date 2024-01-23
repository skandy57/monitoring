package sbt.qsecure.monitoring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;
import sbt.qsecure.monitoring.filter.LoginFilter;

//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
public class SecurityConfig {
	
//	private final TokenProvider tokenProvider;
//    private final JWTAuthenticationEntryPoint jwtAuthenticationEntryPoint;
//    private final JWTAccessDeniedHandler jwtAccessDeniedHandler;
//	private final AuthenticationConfiguration authenticationConfiguration;
//
////	public SecurityConfig(AuthenticationConfiguration authenticationConfiguration) {
////
////		this.authenticationConfiguration = authenticationConfiguration;
////	}
//
//	@Bean
//	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
//		return configuration.getAuthenticationManager();
//	}
//
//	@Bean
//	public BCryptPasswordEncoder bCryptPasswordEncoder() {
//		return new BCryptPasswordEncoder();
//	}
//
//	@Bean
//	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//		http.csrf((auth) -> auth.disable());
////
////		http.formLogin((auth) -> auth.disable());
////
////		http.httpBasic((auth) -> auth.disable());
////
////		http.authorizeHttpRequests((auth) -> auth.requestMatchers("/login", "/", "/join").permitAll()
////				.requestMatchers("/admin").hasRole("ADMIN").anyRequest().authenticated());
//
//		http.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration)),
//				UsernamePasswordAuthenticationFilter.class);
//
////		http.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//		
//		http.apply(new JWTConfig(tokenProvider));
//
//		return http.build();
//	}

}
