package sbt.qsecure.monitoring.checker;

import static org.junit.Assert.assertTrue;

import java.util.Objects;

import org.springframework.stereotype.Component;

import sbt.qsecure.monitoring.constant.Server;
import sbt.qsecure.monitoring.constant.Auth.AuthGrade;
import sbt.qsecure.monitoring.constant.Server.Module;;

@Component
public class AIChecker {

	private static String MODULESTART = "MODULESTART";
	private static String MODULESTOP = "MODULESTOP";
	private static String INSTANCESTART = "INSTANCESTART";
	private static String INSTANCESTOP = "INSTANCESTOP";

	/**
	 * 암호화 모듈 중지 성공 여부를 반환한다
	 * 
	 * @param result 암호화 서버로부터 전달 받은 결과값
	 * @return 성공 여부
	 */
	public boolean isStopModule(String result) {
		return result.toLowerCase().contains("killing cubeone processes")
				&& result.toLowerCase().contains("killing cubeone_guard")
				&& result.toLowerCase().contains("killing cubeoned")
				&& result.toLowerCase().contains("killing cubeone_auditor");
	}

	/**
	 * 암호화 모듈 기동 성공 여부를 반환한다
	 * 
	 * @param result 암호화 서버로부터 전달 받은 결과값
	 * @return 성공 여부
	 */
	public boolean isStartModule(String result) {
		return result.toLowerCase().contains("starting cubeone daemon")
				&& result.toLowerCase().contains("running cubeone_guard");
	}

	/**
	 * 암호화 인스턴스 중지 성공 여부를 반환한다
	 * 
	 * @param result 암호화 서버로부터 전달 받은 결과값
	 * @return 성공 여부
	 */
	public boolean isStopInstance(String result) {
		return result.toLowerCase().contains("killing jcocubeoneserver")
				&& result.toLowerCase().contains("killing jcocubeone processes");
	}

	/**
	 * 암호화 인스턴스 기동 성공 여부를 반환한다
	 * 
	 * @param result 암호화 서버로부터 전달 받은 결과값
	 * @return 성공 여부
	 */
	public boolean isStartInstance(String result) {
		return result.toLowerCase().contains("starting jcocubeoneserver")
				&& result.toLowerCase().contains("running jcocubeoneserver");
	}

	/**
	 * 사용자가 디렉토리 "/"를 실수로 입력한 경우 재조정한다
	 * 
	 * @param path
	 * @return 올바른 "/"
	 */
	public String addMissingFlash(String path) {
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		if (!path.endsWith("/")) {
			path = path + "/";
		}
		return path;
	}

	/**
	 * cotest 성공여부를 반환한다
	 * 
	 * @param result 암호화 서버로부터 전달 받은 cotest 결과값
	 * @return 성공 여부
	 */
	public boolean isSuccess(String result) {
		return (result != null && (result.contains("Hello SAP") || result.contains("complete") || result.contains("ok"))
				&& !result.contains("failed"));
	}

	/**
	 * 잘못된 경로 여부를 반환한다
	 * 
	 * @param result 명령어의 결과값
	 * @return 공백을 리턴받았다면 의도한 경로가 아님
	 */
	public boolean isWrongPath(String result) {
		return (result.isBlank() || result == "" || result.isEmpty() || result == null);
	}

	/**
	 * 관리자 권한 여부를 반환한다
	 * 
	 * @param authGrade 현재 접속중인 계정
	 * @return 관리자 권한 소유 여부
	 */
	public boolean isAdmin(String authGrade) {
		return authGrade.trim().equals("ADMIN");
	}

	public boolean isAlreadyRunning(String result) {
		return result != null && (result.toLowerCase().contains("already starded")
				|| result.toLowerCase().contains("already running"));
	}

	public boolean isNotRunning(String result) {
		return result != null && result.toLowerCase().contains("is not running");
	}

	public Module check(String authGrade, String result) {
		if (isAdmin(authGrade)) {
			return Module.ERR_NOAUTH;
		}

		String type = null;
		String invokeMethod = Thread.currentThread().getStackTrace()[1].toString().toLowerCase();

		if (invokeMethod.contains("module") && invokeMethod.startsWith("start")) {
			type = MODULESTART;
		} else if (invokeMethod.contains("module") && invokeMethod.startsWith("stop")) {
			type = MODULESTOP;
		} else if (invokeMethod.contains("instance") && invokeMethod.startsWith("start")) {
			type = INSTANCESTART;
		} else if (invokeMethod.contains("instance") && invokeMethod.startsWith("stop")) {
			type = INSTANCESTOP;
		}

		switch (type) {
		case "MODULESTART":

			if (isWrongPath(result)) {
				if (isAlreadyRunning(result)) {
					return isStartModule(result) ? Module.SUCCESS : Module.ERR_MODULE_CONTROLL;
				}
				return Module.ALREADY_RUN;
			}
			return Module.ERR_WRONGPATH_MODULE;

		case "MODULESTOP":
			if (isWrongPath(result)) {
				if (isNotRunning(result)) {
					return isStopModule(result) ? Module.SUCCESS : Module.ERR_MODULE_CONTROLL;
				}
				return Module.NOT_RUNNING;
			}
			return Module.ERR_WRONGPATH_MODULE;

		case "INSTANCESTART":
			if (isWrongPath(result)) {
				if (isAlreadyRunning(result)) {
					return isStartInstance(result) ? Module.SUCCESS : Module.ERR_INSTANCE_CONTROLL;
				}
				return Module.ALREADY_RUN;
			}
			return Module.ERR_WRONGPATH_INST;

		case "INSTANCESTOP":
			if (isWrongPath(result)) {
				if (isNotRunning(result)) {
					return isStopInstance(result) ? Module.SUCCESS : Module.ERR_INSTANCE_CONTROLL;
				}
				return Module.NOT_RUNNING;
			}
			return Module.ERR_WRONGPATH_INST;
		default:
			return null;
		}

	}

	public boolean isNumeric(String result) {
		if (result == null || result.isEmpty()) {
			return false;
		}
		return result.matches("-?\\d+");
	}

	public boolean isNumber(String result) {
		for (char c : result.toCharArray()) {
			if (!Character.isDigit(c)) {
				return true;
			}
		}
		return false;
	}

	public boolean isDirectory(String result) {
		return result.startsWith("d") && !result.contains("log") && !result.contains("bak");
	}

	public void requireNonNullParams(Object... params) {
		for (Object param : params) {
			Objects.requireNonNull(param);
		}
	}

//	public boolean isDecrypted(String result) {
//		return !result.contains("CubeOne for SAP") && isNumber(result);
//	}
}
