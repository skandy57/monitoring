package sbt.qsecure.monitoring.vo;

import org.springframework.web.socket.WebSocketSession;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;

import lombok.Data;

@Data
public class TerminalConnectionVO {
	private WebSocketSession session;
	private JSch jsch;
	private Channel channel;
	private TerminalVO vo;
}
