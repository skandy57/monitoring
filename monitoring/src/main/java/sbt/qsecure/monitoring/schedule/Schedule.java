package sbt.qsecure.monitoring.schedule;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import sbt.qsecure.monitoring.service.ScheduleService;

@Slf4j
@Component
public class Schedule {
	@Autowired
	private ScheduleService service;

	private static final DateTimeFormatter format = DateTimeFormatter.ofPattern("mm:ss:SSS");

//	@Scheduled(fixedRate = 1000)
//	public void fixedRate() {
//	    service.test();
//	}
}
