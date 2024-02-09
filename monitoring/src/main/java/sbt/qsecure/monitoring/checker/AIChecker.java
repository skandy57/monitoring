package sbt.qsecure.monitoring.checker;

import org.springframework.stereotype.Component;

import sbt.qsecure.monitoring.constant.Auth.AuthGrade;

@Component
public class AIChecker {

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
			path = "/"+path;
		}
		if (!path.endsWith("/")) {
			path = path+"/";
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
		return (result.isBlank() || result == "");
	}
	
	/**
	 * 관리자 권한 여부를 반환한다
	 * 
	 * @param authGrade 현재 접속중인 계정
	 * @return 관리자 권한 소유 여부
	 */
	public boolean isAdmin(String authGrade) {
		return authGrade.equals(AuthGrade.ADMIN.toString());
	}

}
