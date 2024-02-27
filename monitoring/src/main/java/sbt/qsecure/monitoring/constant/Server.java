package sbt.qsecure.monitoring.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sbt.qsecure.monitoring.checker.AIChecker;

/**
 * 암호화 서버 정의 상수
 */
public enum Server {
	;
	/**
	 * 암호화 모듈 정의 상수
	 */
	@Getter
	@RequiredArgsConstructor
	public enum Module {
		SUCCESS("success"),
		/**
		 * 암호화 SAP 통신 테스트간 에러발생
		 */
		ERR_SAP_TEST(
				"[%s:%s] An error occurred during encryption SAP communication testing. Please check the firewall between A/I and SAP."),
		/**
		 * 암호화 DB 통신 테스트간 에러발생
		 */
		ERR_AIDB_TEST(
				"[%s:%s] An error occurred during encryption DB communication testing. Please check the A/I server's DB settings or firewall."),
		/**
		 * 암호화 모듈 테스트간 에러발생
		 */
		ERR_MODULE_TEST(
				"[%s:%s] An error occurred during encryption module testing. Please check the A/I module Running."),
		/**
		 * WAS에 등록된 암호화 인스턴스 경로가 틀림 
		 */
		ERR_WRONGPATH_INST("[%s:%s] modify the encryption instance path. %s"),
		/**
		 * WAS에 등록된 암호화 모듈 경로가 틀림
		 */
		ERR_WRONGPATH_MODULE("[%s:%s] modify the encryption module path. %s"),
		/**
		 * 권한 없는 유저가 제어를 시도함
		 */
		ERR_NOAUTH("This [%s] User does not have permission"),
		/**
		 * 모듈 조작간 에러가 발생함
		 */
		ERR_MODULE_CONTROLL("[%s:%s] an error occurred during module control"),
		/**
		 * 인스턴스 조작간 에러가 발생함
		 */
		ERR_INSTANCE_CONTROLL("[%s:%s] error occurred during encryption instance %s control."),
		/**
		 * WAS에 등록된 암호화 서버 OS가 오타거나 잘못입력함
		 */
		ERR_UNKNOWNOS(
				"Incorrect encryption OS settings [%s]. Please modify the encryption OS settings in the encryption monitoring."),
		/**
		 * COTEST간 오류가 발생함
		 */
		ERR_COTEST("[%s:%s] COTEST error occurred. Please check the network between WAS and A/I."),
		/**
		 * 암호화 서버와 통신간 NULL값을 리턴받음
		 */
		NULL("[%s:%s] COTEST error occurred. Please check the network between WAS and A/I."),
		/**
		 * 기동되지 않은 모듈이나 인스턴스를 중지를 시도함
		 */
		NOT_RUNNING("The operation cannot be performed because the module is not running."),
		/**
		 * 기동중인 모듈이나 인스턴스를 기동을 시도함
		 */
		ALREADY_RUN("The operation cannot be performed because the module is already running.");

		private final String description;

		public String getDescription(String... args) {
			Object[] trimmedArgs = new String[args.length];
			for (int i = 0; i < args.length; i++) {
				trimmedArgs[i] = args[i].trim();
			}
			return String.format(description, trimmedArgs);
		}

		public static String getDescription(Module module, String... args) {
			Object[] trimmedArgs = new String[args.length];
			for (int i = 0; i < args.length; i++) {
				trimmedArgs[i] = args[i].trim();
			}
			return String.format(module.getDescription(), trimmedArgs);
		}

	}

	/**
	 * 암호화 관련 서버들의 타입 상수
	 */
	@Getter
	@RequiredArgsConstructor
	public enum Type {

		/**
		 * 암호화 서버
		 */
		AI("A/I Server"),
		/**
		 * 시큐리티 서버
		 */
		SECURITY("Security Server"),
		/**
		 * 매니저 서버
		 */
		MANAGER("Manager Server");

		private final String type;

	}

	/**
	 * 암호화 관련 서버들의 OS 상수
	 */
	@Getter
	@RequiredArgsConstructor
	public enum OS {
		WINDOWS("Windows"), LINUX("Linux");

		private final String os;
	}

	/**
	 * 암호화 서버 버전 상수
	 */
	@Getter
	@RequiredArgsConstructor
	public enum Version {
		/**
		 * 신버전
		 */
		NEW("NEW"),
		/**
		 * 구버전
		 */
		OLD("OLD");

		private final String version;
	}

	@RequiredArgsConstructor
	public enum SSH {

		SUCCESS, ERR_UNKNOWN_HOSTKEY, ERR_INVALID_PRIVATEKEY, ERR_AUTH_CANCEL, ERR_BUFFER, ERR_SESSION_REUSE,
		ERR_CHANNEL, ERR_RUNTIME, ERR_IDFILE, ERR_PARTIAL_AUTH, ERR_PROXYHTTP, ERR_PROXYSOCK, ERR_IO, ERR_NULLPOINTER

	}

	public enum Log {
		GETSERVERINFO {
			@Override
			public String error(String... args) {
				return log("[getServerDetailInfo] Failed to get server details infomation. Target Host=[%s] ", args);
			}

			@Override
			public String success(String... args) {
				// TODO Auto-generated method stub
				return log(
						"[getServerDetailInfo] Successfully to get server details infomation. Target Host=[%s] cause=[%s]",
						args);
			}

		},
		COTEST {

			@Override
			public String success(String... args) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String error(String... args) {
				// TODO Auto-generated method stub
				return null;
			}

//			public String error(Server.Module result, String... args) {
//				String host = null;
//				String port = null;
//				if (args != null) {
//					host = args[0];
//					port = args[1];
//				}
//				return switch (result) {
//				case ERR_WRONGPATH -> log("%s:%s 는 잘못된 암호화 인스턴스 경로입니다. 암호화 인스턴스 경로를 수정해주세요.", host, port);
//				case ERR_MODULE_TEST -> log("%s:%s 의 암호화 모듈 테스트 간 오류가 발생하였습니다. 암호화 모듈 기동 상태를 확인해주세요.", host, port);
//				case ERR_SAP_TEST -> log("%s:%s 의 암호화 SAP 통신 테스트 간 오류가 발생하였습니다. A/I <-> SAP간 방화벽을 확인해주세요.", host, port);
//				case ERR_AIDB_TEST ->
//					log("%s:%s 의 암호화 DB 통신 테스트 간 오류가 발생하였습니다. 암호화서버의 DB세팅이나 방화벽 등을 확인해주세요.", host, port);
//				case ERR_COTEST -> log("%s:%s 의 COTEST 오류가 발생하였습니다. WAS <-> A/I간 네트워크를 확인해주세요.", host, port);
//				case NULL ->
//					log("%s:%s 와 통신 시도 간 NullPointerException이 발생하였습니다. WAS <-> WASDB간 네트워크를 확인해주세요.", host, port);
//				default -> log("%s:%s 의 알 수 없는 오류가 발생하였습니다.", host, port);
//				};
//
//			}
		},
		/**
		 * 접속중인 유저의 성함, 권한, 타겟 서버의 IP를 받아 성공, 에러의 로그를 반환한다.
		 */
		STARTMODULE {

			@Override
			public String success(String... args) {
				// TODO Auto-generated method stub
				return log(
						"[startCubeOneModule] Successfull starting CubeOne Module. execute Manager = [%s] Auth = [%s] Target Host = [%s]",
						args);
			}

			@Override
			public String error(String... args) {
				// TODO Auto-generated method stub
				return log(
						"[startCubeOneModule] Failed starting CubeOne Module. execute Manager = [%s] Auth = [%s] Target Host = [%s]",
						args);
			}

		},
		/**
		 * 접속중인 유저의 성함, 권한, 타겟 서버의 IP를 받아 성공, 에러의 로그를 반환한다.
		 */
		STOPMODULE {

			@Override
			public String success(String... args) {
				return log(
						"[stopCubeOneModule] Successfull stop CubeOne Module. execute Manager = [%s] Auth = [%s] Target Host = [%s]",
						args);
			}

			@Override
			public String error(String... args) {
				// TODO Auto-generated method stub
				return log(
						"[stopCubeOneModule] Failed stop CubeOne Module. execute Manager = [%s] Auth = [%s] Target Host = [%s]",
						args);
			}

		},
		/**
		 * 접속중인 유저의 성함, 권한, 타겟 서버의 IP를 받아 성공, 에러의 로그를 반환한다.
		 */
		STARTINSTANCE {

			@Override
			public String success(String... args) {
				return log(
						"[startCubeOneInstance] Successfull starting CubeOne [%s] Instance. execute Manager = [%s] Auth = [%s] Target Host = [%s]",
						args);
			}

			@Override
			public String error(String... args) {

				return log(
						"[startCubeOneInstance] Failed starting CubeOne [%s] Instance. execute Manager = [%s] Auth = [%s] Target Host = [%s]",
						args);
			}

		},
		/**
		 * 접속중인 유저의 성함, 권한, 타겟 서버의 IP를 받아 성공, 에러의 로그를 반환한다.
		 */
		STOPINSTANCE {

			@Override
			public String success(String... args) {
				return log(
						"[stopCubeOneInstance] Successfull stop CubeOne [%s] Instance. Execute Manager = [%s] Auth = [%s] Target Host = [%s]",
						args);
			}

			@Override
			public String error(String... args) {
				return log(
						"[stopCubeOneInstance] Failed stop CubeOne [%s] Instance. Execute Manager = [%s] Auth = [%s] Target Host = [%s]",
						args);
			}

		},
		/**
		 * 암호화 에러 추출 메소드 결과 로그
		 */
		COUNTENCERROR {

			@Override
			public String success(String... args) {
				// TODO Auto-generated method stub
				return log("[getCountEncError] IP=[%s] DIRECTORY=[%s] CNT=[%s], DATE=[%s]", args);
			}

			@Override
			public String error(String... args) {
				// TODO Auto-generated method stub
				return log(
						"[getCountEncError] Failed get Count Error From A/I Servers Target Host=[%s] Target Directory=[%s] CNT=[%s], DATE=[%s]",
						args);
			}

		},
		/**
		 * 암호화 서버의 프로세스 추출 메소드 결과 로그
		 */
		GETPROCESS {

			@Override
			public String success(String... args) {
				// TODO Auto-generated method stub
				return log("[getProcess] Successfully getProcess target Server [%s]", args);
			}

			@Override
			public String error(String... args) {
				return log("[getProcess] Failed getProcess target Server [%s], Check A/I Server and Network Settings",
						args);
			}

		},
		/**
		 * A/I <-> WAS간 통신 결과 로그
		 */
		CONNECT {

			@Override
			public String success(String... args) {
				return log("[connect] Success Connect Session, Target Host=[%s] port=[%s]", args);
			}

			@Override
			public String error(String... args) {
				return log(
						"[connect] Failed Connect Session, Target Host=[%s] port=[%s], Check A/I Server and Network Settings",
						args);
			}

		},
		/**
		 * A/I <-> WAS 간 통신 테스트 여부 결과 로그
		 */
		ISCONNECT {

			@Override
			public String success(String... args) {
				return log("[isConnected] WAS <-> A/I Server Success Connected, Target Host=[%s]", args);
			}

			@Override
			public String error(String... args) {
				return log(
						"[isConnected] WAS <-> A/I Server is Not Connected, Target Host=[%s], Check A/I Server and Network Settings",
						args);
			}

		},
		/**
		 * CPU의 사용량을 구하는 메소드의 성공 결과 로그
		 */
		GETCPUUSAGE {

			@Override
			public String success(String... args) {
				return log("[getCpuUsage] Successfull get A/I Cpu Usage, Target Host=[%s]");
			}

			@Override
			public String error(String... args) {
				return log("[getCpuUsage] Failed get A/I Cpu Usage, Target Host=[%s]");
			}

		},
		/**
		 * 메모리의 사용량을 구하는 메소드의 성공 결과 로그
		 */
		GETMEMORYUSAGE {

			@Override
			public String success(String... args) {
				return log("[getMemoryUsage] Successfull get A/I Memory Usage, Target Host=[%s]");
			}

			@Override
			public String error(String... args) {
				return log("[getMemoryUsage] Failed get A/I Memory Usage, Target Host=[%s]");
			}

		},
		/**
		 * 디스크의 사용량을 구하는 메소드의 성공 결과 로그
		 */
		GETDISKUSAGE {

			@Override
			public String success(String... args) {
				return log("[getDiskUsage] Successfull get A/I Disk Usage, Target Host=[%s]");
			}

			@Override
			public String error(String... args) {
				return log("[getDiskUsage] Successfull get A/I Disk Usage, Target Host=[%s]");
			}

		};

		/**
		 * 성공시 반환할 로그 추상화메소드
		 * 
		 * @param args 로그에 찍을 매개변수
		 * 
		 */
		public abstract String success(String... args);

		/**
		 * 에러 발생시 반환할 로그 추상화메소드
		 * 
		 * @param args 로그에 찍을 매개변수
		 * 
		 */
		public abstract String error(String... args);

		public String error(Server.Module result, String... args) {
			return null;
		};

		/**
		 * 매개변수 args 배열을 trim처리 후 String.format을 반환한다
		 * 
		 * @param log  로그 내용
		 * @param args 입력받은 매개변수
		 * @return String.format처리한 log
		 */
		public static String log(String log, String... args) {
			String[] trimmedArgs = new String[args.length];
			for (int i = 0; i < args.length; i++) {
				trimmedArgs[i] = args[i].trim();
			}
			return String.format(log, (Object[]) trimmedArgs);
		}
	}

	/**
	 * OS 명령어들의 상수 ENUM
	 */
	public enum Command {
		;

		/**
		 * 리눅스 명령어들의 상수 ENUM
		 */
		public enum Linux {

			COTEST {
				@Override
				public String build(String... args) {
					AIChecker checker = new AIChecker();

					if (args[0] != null && args[1] != null) {
						String directory = checker.addMissingFlash(args[0]);
						String type = args[1];

						return String.format(BASH.build() + "$COHOME%scotest.sh %s", directory, type);
					}
					return null;
				}

				@Override
				public String retry(String directory) {

					AIChecker checker = new AIChecker();

					directory = checker.addMissingFlash(directory);

					return String.format(BASH.build() + "$COHOME%scotest.sh ora", directory);
				}

			},
			/**
			 * 환경변수를 사용하기 위해 bash_profile을 source처리한다. 
			 */
			BASH {
				@Override
				public String build(String... args) {
					return "source .bash_profile;";
				}
			},
			/**
			 * 암호화 로그의 에러갯수를 추출하는 명령어를 조합한다.
			 */
			COUNT_ENC_ERR {
				@Override
				public String build(String... args) {

					AIChecker checker = new AIChecker();
					if (args[0] != null && args[1] != null ) {
						String directory = checker.addMissingFlash(args[0]);
						String date = args[1];
						

						return String.format(BASH.build()
								+ "grep -c '[ERROR]' $COHOME%s%s_*_ENC_* | awk -F: '{sum += $NF}END {print sum}'",
								directory, date);
					}
					return null;
				}

			},
			/**
			 * 복호화 로그의 에러갯수를 추출하는 명령어를 조합한다.
			 */
			COUNT_DEC_ERR {
				@Override
				public String build(String... args) {
					AIChecker checker = new AIChecker();
					String directory = checker.addMissingFlash(args[0]);
					String date = args[1];
					String sid = args[2];

					return String.format(
							BASH.build()
									+ "grep -c '[ERROR]' $COHOME%s%s_%s_DEC_* | awk -F: '{sum += $NF}END {print sum}'",
							directory, date, sid);
				}
			},
			/**
			 * CPU 사용량 추출 명령어
			 */
			CPU_USAGE {
				@Override
				public String build(String... args) {
					return "mpstat | awk '/all/ {print 100- $NF}'";
				}

			},
			/**
			 * 메모리 사용량 추출 명령어
			 */
			MEMORY_USAGE {
				@Override
				public String build(String... args) {
					return "free | awk '/Mem:/ {used = $2 - $4 - $6; print used / $2 * 100}'";
				}

			},
			/**
			 * 암호화 모듈 설치된 파일시스템의 디스크 사용량 추출 명령어
			 */
			DISK_USAGE {
				@Override
				public String build(String... args) {
					return BASH.build() + "df -h $COHOME | awk 'NR==2 {print $5}'";
				}

			},
			/**
			 * 암호화 모듈 기동 명령어
			 */
			STARTMODULE {
				@Override
				public String build(String... args) {
					return BASH.build() + "$COHOME/bin/cubeone.sh start API";
				}
			},
			/**
			 * 암호화 모듈 중지 명령어
			 */
			STOPMODULE {
				@Override
				public String build(String... args) {
					return BASH.build() + "$COHOME/bin/cubeone.sh stop API";
				}

			},
			/**
			 * 암호화 인스턴스 기동 명령어를 조합한다
			 * 
			 * @param directory 기동할 인스턴스 디렉토리
			 *                  <p>
			 *                  매개변수 directory에서 "jco_" 오른쪽의 있는 문자열을 인스턴스로 가정하여 실행할 쉘 파일의
			 *                  이름을 조합한다.
			 *                  </p>
			 */
			STARTINSTANCE {

				@Override
				public String build(String... args) {
					AIChecker checker = new AIChecker();
					if (args[0] != null) {
						String directory = checker.addMissingFlash(args[0]);

						String instance = null;
						try {
							instance = getInstance(directory);
						} catch (Exception e) {
							e.printStackTrace();
						}

						return String.format(BASH.build() + "$COHOME/aisvr%scubeone_%s.sh start", directory, instance);
					}

					return null;
				}

			},
			/**
			 * 암호화 인스턴스 중지 명령어를 조합한다
			 * 
			 * @param directory 중지할 인스턴스 디렉토리
			 *                  <p>
			 *                  매개변수 directory에서 "_"기준으로 오른쪽의 있는 문자열을 인스턴스로 가정하여 실행할 쉘 파일의
			 *                  이름을 조합한다.
			 *                  </p>
			 */
			STOPINSTANCE {
				@Override
				public String build(String... args) {
					AIChecker checker = new AIChecker();
					if (args[0] != null) {
						String directory = checker.addMissingFlash(args[0]);

						String instance = null;
						try {
							instance = getInstance(directory);
						} catch (Exception e) {
							e.printStackTrace();
						}

						return String.format(BASH.build() + "$COHOME%scubeone_%s.sh stop", directory, instance);
					}
					return null;
				}
			},
			/**
			 * 암호화 서버의 프로세스 목록을 추출하는 명령어를 조합한다.<p>
			 * 기본 정렬은 cpu 사용량 상위 11개를 추출하고, 첫번째 매개변수를 mem 또는 cpu로 지정한 이후,<p>
			 * 그 다음 매개변수로 추출하고자 하는 갯수를 문자열로 받아 명령어를 조합하여 리턴한다.
			 */
			GETPROCESS {

				@Override
				public String build(String... args) {

					String sortBy = null;
					if (args[0].equals("") || args[0] == null) {
						sortBy = "cpu";
					}
					sortBy = args[0];
					if (sortBy.toLowerCase().contains("mem")) {
						sortBy = "mem";
					} else if (sortBy.toLowerCase().contains("cpu")) {
						sortBy = "cpu";
					}
					try {
						if ((args[1] != null && args[1] != "")) {
							String limit = args[1];
							return String.format("ps aux --sort=-%" + sortBy + " | head -n " + limit);
						}
					} catch (Exception e) {
						return String.format("ps aux --sort=-" + sortBy + " | head -n 11");
					}

					return String.format("ps aux --sort=-" + sortBy + " | head -n 11");
				}

			},
			/**
			 * 암호화 모듈의 버전과 인스턴스 디렉토리명을 매개변수로 받아,
			 * <p>
			 * JCO세팅값을 서버로 부터 가져오는 명령어를 버전 별로 조합하여 리턴한다.
			 */
			GETJCOSETTING {

				@Override
				public String build(Version version, String... args) {
					String instance = args[0];
					return switch (version) {
					case NEW ->
						String.format(BASH.build() + "cat " + NEW.build() + instance + "/CubeOneJcoServer.jcoServer");
					case OLD ->
						String.format(BASH.build() + "cat " + OLD.build() + instance + "/CubeOneJcoServer.jcoServer");
					};

				}

				@Override
				public String build(String... args) {
					String instance = args[0];
					return String.format(BASH.build() + NEW.build() + instance + "/CubeOneJcoServer.jcoServer");

				}

			},
			/**
			 * 암호화 모듈의 버전과 인스턴스 디렉토리명(ex: jco_prd)을 매개변수로 받아
			 * <p>
			 * 암호화 DB의 세팅값을 서버로부터 가져오는 명령어를 버전 별로 조합하여 리턴한다.
			 */
			GETDBSETTING {
				@Override
				public String build(Version version, String... args) {
					String instance = args[0];
					return switch (version) {
					case NEW -> String.format(BASH.build() + "cat " + NEW.build() + instance + "/CubeOneJcoServer.db");
					case OLD -> String.format(BASH.build() + "cat " + OLD.build() + instance + "/CubeOneJcoServer.db");
					};

				}

				@Override
				public String build(String... args) {
					String instance = args[0];
					return String.format(BASH.build() + "cat " + NEW.build() + instance + "/CubeOneJcoServer.db");
				}

			},
			/**
			 * 암호화 특정 인스턴스의 SAP 세팅을 가져오는 명령어를 버전 별로 조합한다.
			 * 
			 */
			GETSAPSETTING {
				@Override
				public String build(Version version, String... args) {
					String instance = args[0];
					return switch (version) {
					case NEW ->
						String.format(BASH.build() + "cat " + NEW.build() + instance + "/SAP_SERVER.jcoDestination");
					case OLD ->
						String.format(BASH.build() + "cat " + OLD.build() + instance + "/SAP_SERVER.jcoDestination");
					};

				}

				@Override
				public String build(String... args) {
					String instance = args[0];
					return String.format(BASH.build() + "cat " + NEW.build() + instance + "/SAP_SERVER.jcoDestination");
				}
			},
			/**
			 * 암호화 인스턴스의 리스트 목록을 출력하는 명령어를 버전 별로 조합한다.
			 */
			GETINSTANCELIST {

				@Override
				public String build(Version version, String... args) {
					return switch (version) {
					case NEW -> String.format(BASH.build() + "ls -l " + NEW.build());
					case OLD -> String.format(BASH.build() + "ls -l " + OLD.build());
					};

				}

				@Override
				public String build(String... args) {
					return String.format(BASH.build() + "ls -l " + NEW.build());
				}
			},
			/**
			 * 암호화 서버의 인스턴스 디렉토리를 버전별로 조합한다.
			 */
			GETINSTANCEDIRECTORY {

				@Override
				public String build(Version version, String... args) {
					String instance = args[0];
					return switch (version) {
					case NEW -> String.format(NEW.build() + instance);
					case OLD -> String.format(OLD.build() + instance);
					};
				}

				@Override
				public String build(String... args) {
					// TODO Auto-generated method stub
					return null;
				}

			},
			/**
			 * 암호화 서버의 환경변수인 COHOME의 경로를 구하는 명령어
			 */
			GETCOHOME{

				@Override
				public String build(String... args) {

					return BASH.build()+"pwd $COHOME";
				}
				
			},
			
			/**
			 * 암호화 구형 모듈의 인스턴스 디렉토리 
			 */
			OLD {

				@Override
				public String build(String... args) {

					return "$COHOME/JCOCubeOneServer/";
				}

			},
			/**
			 * 암호화 신형 모듈의 인스턴스 디렉토리
			 */
			NEW {

				@Override
				public String build(String... args) {
					return "$COHOME/aisvr/";
				}

			},
			DECRYPT {

				@Override
				public String build(String... args) {
					String instance = args[0];
					String item = args[1];
					String encryptText = args[2];
					return String.format(BASH.build() + NEW.build() + "%s/cotest.sh enc dec %s %s", instance, item,
							encryptText);
				}

			},
			GETMODULEVERSION {

//				public String build(Version version, String...args) {
//					return switch(version) {
//					case NEW -> 
//					};
//					
//				}
//				
				@Override
				public String build(String... args) {
					// TODO Auto-generated method stub
					return null;
				}

			};

			public enum Windows {

			}

			/**
			 * 명령어를 조합한다
			 * 
			 * @param args 명령어 조합에 필요한 매개변수
			 * @return 조합한 명령어
			 */
			public abstract String build(String... args);

			/**
			 * 버전별로 다른 명령어를 조합한다
			 * 
			 * @param version 암호화 모듈 버전
			 * @param args    명령어 조합에 필요한 매개변수
			 * @return 조합한 명령어
			 */
			public String build(Version version, String... args) {
				return null;
			}

			public String retry(String path) {
				return null;
			}

			public String getInstance(String directory) {
				String instance = null;

				int index = directory.indexOf("_");
				if (index != -1) {
					instance = directory.substring(index + 1);
				}
				if (instance.endsWith("/")) {
					instance = instance.substring(0, instance.length() - 1);
				}
				return instance;
			}
		}

		@Getter
		public enum OID {
			/**
			 * 시스템 정보
			 */
			SYSTEM_INFOMATION("1.3.6.1.1.2.1.1.1"),

			/**
			 * 시스템 기동시간
			 */
			SYSTEM_UPTIME("1.3.6.1.1.2.1.1.3"), 
			INTERFACE_NAME("1.3.6.1.2.1.2.2.1.2"),
			INTERFACE_TYPE("1.3.6.1.2.1.2.2.1.2"), 
			INTERFACE_MTU("1.3.6.1.2.1.2.2.1.4"),
			INTERFACE_SPEED("1.3.5.1.2.1.2.2.1.5"), 
			INTERFACE_MACADDRESS("1.3.5.1.2.1.2.2.1.6"),
			IP_DEFAULT_TTL("1.3.5.1.2.1.4.2"), 
			INTERFACE_IP("1.3.5.1.2.1.4.20.1.1"),
			INTERFACE_NETMASK("1.3.6.1.2.1.4.20.1.3"), 
			HW_UPTIME("1.3.6.1.2.1.25.1.1"), 
			HW_TIME("1.3.6.1.2.1.25.1.2"),
			NETWORK_DEVICE_INFO("1.3.6.1.2.1.25.3.4"), 
			CPU_USAGE_1MIN("1.3.6.1.4.1.2021.10.1.3.1"),
			CPU_USAGE_5MIN("1.3.6.1.4.1.s2021.10.1.3.2"), 
			CPU_USAGE_15MIN("1.3.6.1.4.1.2021.10.1.3.3"),
			CPU_SYSTEM_TIME("1.3.6.1.4.1.2021.11.52.0"), 
			CPU_IDLE_TIME("1.3.6.1.4.1.2021.11.53.0"),
			DISK_INFO("1.3.6.1.2.1.25.2.3.1.3"), 
			DISK_TYPE("1.3.6.1.2.1.25.2.3.1.2"),
			DISK_TOTAL("1.3.6.1.2.1.25.2.3.1.5"), 
			DISK_USED("1.3.6.1.2.1.25.2.3.1.6"),
			SWAP_TOTAL("1.3.6.1.4.1.2021.4.1.3"), 
			SWAP_FREE("1.3.6.1.4.1.2021.4.1.4"),
			PHYSICAL_TOTAL("1.3.6.1.4.1.2021.4.1.5"), 
			MEMORY_TOTAL("1.3.6.1.2.1.25.2.2"),
			PHYSICAL_FREE("1.3.6.1.4.1.2021.4.1.6"), 
			MEMORY_FREE("1.3.6.1.4.1.2021.4.1.11"),
			SHARED_MEMORY("1.3.6.1.4.1.2021.4.1.13"), 
			BUFFER_MEMORY("1.3.6.1.4.1.2021.4.1.14"),
			CACHE_MEMORY("1.3.6.1.4.1.2021.4.1.15");

			private final String oid;

			private OID(String oid) {
				this.oid = oid;

			}
		}
	}
}
