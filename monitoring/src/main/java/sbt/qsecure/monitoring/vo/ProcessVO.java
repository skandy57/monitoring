package sbt.qsecure.monitoring.vo;

public record ProcessVO(String user, String pid, String cpu, String memory, String vsz, String rss, String tty,
		String status, String start, String time, String command) {

}
