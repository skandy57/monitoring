package sbt.qsecure.monitoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.scheduling.annotation.EnableScheduling;

//@EnableScheduling
@SpringBootApplication
@ComponentScans({
	@ComponentScan(basePackages = "sbt.qsecure.monitoring.controller"),
	@ComponentScan(basePackages = "sbt.qsecure.monitoring.service"),
})
public class MonitoringApplication {

	public static void main(String[] args) {
		SpringApplication.run(MonitoringApplication.class, args);
	}

}
