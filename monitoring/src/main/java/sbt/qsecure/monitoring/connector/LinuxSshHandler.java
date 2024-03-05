package sbt.qsecure.monitoring.connector;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sbt.qsecure.monitoring.vo.TerminalVO;

@Slf4j
@RequiredArgsConstructor
public class LinuxSshHandler implements WebSocketHandler {
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		// TODO Auto-generated method stub
		log.info("{} 연결됨", session.getId());
	}

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
//		if (message.getPayload().toString().contains("OPEN WEB SOCKET")) {
//			ObjectMapper om = new ObjectMapper();
//			TerminalVO vo = om.readValue(message.getPayload().toString(), SshDto.class);
//			vo = service.getHostInfo(dto);
//			conService.initConnection(session, vo);
//		}
//		
//		conService.recvHandle(session, message.getPayload().toString());
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean supportsPartialMessages() {
		// TODO Auto-generated method stub
		return false;
	}

}
