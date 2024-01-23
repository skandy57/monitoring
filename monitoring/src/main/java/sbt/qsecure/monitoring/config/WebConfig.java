package sbt.qsecure.monitoring.config;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.WebProperties.LocaleResolver;
import org.springframework.boot.web.servlet.server.Encoding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.Ordered;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sbt.qsecure.monitoring.interceptor.AuthInterceptor;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class WebConfig implements WebMvcConfigurer {

	private final AuthInterceptor authInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(authInterceptor);
	}

	@Bean
	public AcceptHeaderLocaleResolver defaultLocaleResolver() {
		AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
		localeResolver.setDefaultLocale(Locale.KOREAN);

		log.info("localeResolver Bean Created.");
		return localeResolver;
	}

	@Bean
	public ReloadableResourceBundleMessageSource messageSource() {
		Locale.setDefault(Locale.KOREAN);

		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("classpath:/messages/messages");
		messageSource.setDefaultEncoding(Encoding.DEFAULT_CHARSET.toString());
		messageSource.setDefaultLocale(Locale.getDefault());
		messageSource.setCacheSeconds(600);

		log.info("messageSource Bean Created. Default Charset is {} and Default Locale is {}",
				Encoding.DEFAULT_CHARSET.toString(), Locale.getDefault());

		return messageSource;
	}

	@Bean
	public MessageSourceAccessor messageSourceAccessor(@Autowired ReloadableResourceBundleMessageSource messageSource) {
		return new MessageSourceAccessor(messageSource);
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
//		registry.addResourceHandler("/static/**") // /static/** 경로로 요청이 들어오면
//				.addResourceLocations("classpath:/static/")
//				.setCacheControl(CacheControl.maxAge(10, TimeUnit.MINUTES));; // classpath:/static/에서 찾도록 설정
//
//		registry.addResourceHandler("/templates/**") // /templates/** 경로로 요청이 들어오면
//				.addResourceLocations("classpath:/templates/")
//				.setCacheControl(CacheControl.maxAge(10, TimeUnit.MINUTES));; // classpath:/templates/에서 찾도록 설정
//
//		registry.setOrder(Ordered.HIGHEST_PRECEDENCE); // ResourceHandlerRegistry의 우선순위 설정
		
	        registry.addResourceHandler("/**")
	                .addResourceLocations("classpath:/templates/", "classpath:/static/")
	                .setCacheControl(CacheControl.maxAge(10, TimeUnit.MINUTES));
	    
	
	}

}
