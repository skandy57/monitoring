package sbt.qsecure.monitoring.connector;

import java.util.List;
import sbt.qsecure.monitoring.constant.Server.OS;

public interface OSConnector {

	/**
	 * A/I서버로 명령어를 던진다.
	 * 
	 * @param command A/I서버로 던질 명령어
	 * @return 명령어의 결과값
	 * @throws Exception
	 */
	String sendCommand(String command) throws Exception;

	/**
	 * A/I서버로 다수의 명령어를 던진다.
	 * 
	 * @param command A/I서버로 던질 명령어 배열
	 * @return 명령어의 결과값 List
	 * @throws Exception
	 */
	List<String> sendCommandList(String... command) throws Exception;

	/**
	 * A/I <-> WAS간 통신 테스트를 진행한다
	 * 
	 * @return 통신 가능 여부
	 */
	boolean isConnected();

	OS getOSType();

}
