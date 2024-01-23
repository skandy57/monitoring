package sbt.qsecure.monitoring.constant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import lombok.Getter;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Auth {
//	
	AuthGrade authGrade() default AuthGrade.NOT_LOGIN;

//	
	@Getter
	enum AuthGrade {
		NOT_LOGIN, MEMBER, ADMIN;

	}

}