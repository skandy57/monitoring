package sbt.qsecure.monitoring.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sbt.qsecure.monitoring.checker.AIChecker;

public enum Server {
	;
	@Getter
	@RequiredArgsConstructor
	public enum Module {
		SUCCESS, ERR_SAP_TEST, ERR_AIDB_TEST, ERR_MODULE_TEST, ERR_WRONGPATH_INST, ERR_WRONGPATH_MODULE,
		ERR_INSTANCE_CONTROLL, ERR_MODULE_CONTROLL, ERR_NOAUTH, ERR_COTEST, ERR_UNKNOWNOS, NULL;

		public static String getDescription(Module result, String... args) {
			String[] trimmedArgs = new String[args.length];
			for (int i = 0; i < args.length; i++) {
				trimmedArgs[i] = args[i].trim();
			}
			return switch (result) {
			case SUCCESS -> String.format("%s:%s 성공", (Object[])trimmedArgs);
			case ERR_WRONGPATH_INST -> String.format("%s:%s 암호화 인스턴스 경로를 수정해주세요.", (Object[]) trimmedArgs);
			case ERR_WRONGPATH_MODULE -> String.format("%s:%s 암호화 모듈 경로를 수정 해주세요.",(Object[]) trimmedArgs);
			case ERR_NOAUTH -> "해당 기능 사용 권한이 없는 유저입니다";
			case ERR_MODULE_CONTROLL ->String.format("%s:%s 모듈 제어 간 오류가 발생하였습니다", (Object[]) trimmedArgs);
			case ERR_INSTANCE_CONTROLL -> String.format("암호화 인스턴스 %s 제어간 오류가 발생하였습니다.", (Object[]) trimmedArgs);
			case ERR_UNKNOWNOS -> "잘못된 암호화 OS 세팅입니다. 암호화 모니터링 내 암호화 OS 세팅을 수정해주세요";
			case ERR_MODULE_TEST ->
				String.format("%s:%s 의 암호화 모듈 테스트 간 오류가 발생하였습니다. 암호화 모듈 기동 상태를 확인해주세요.", (Object[]) trimmedArgs);
			case ERR_SAP_TEST -> String.format("%s:%s 의 암호화 SAP 통신 테스트 간 오류가 발생하였습니다. A/I <-> SAP간 방화벽을 확인해주세요.",
					(Object[]) trimmedArgs);
			case ERR_AIDB_TEST -> String.format("%s:%s 의 암호화 DB 통신 테스트 간 오류가 발생하였습니다. 암호화서버의 DB세팅이나 방화벽 등을 확인해주세요.",
					(Object[]) trimmedArgs);
			case ERR_COTEST ->
				String.format("%s:%s 의 COTEST 오류가 발생하였습니다. WAS <-> A/I간 네트워크를 확인해주세요.", (Object[]) trimmedArgs);
			case NULL -> String.format("%s:%s 와 통신 간 데이터를 받아오지 못했습니다. WAS <-> A/I간 네트워크를 확인해주세요.",
					(Object[]) trimmedArgs);
			default -> String.format("%s:%s 의 알 수 없는 오류가 발생하였습니다.", (Object[]) trimmedArgs);
			};

		}

	}

	@Getter
	@RequiredArgsConstructor
	public enum Type {

		AI("A/I Server"), SECURITY("Security Server"), MANAGER("Manager Server");

		private final String type;

	}

	@Getter
	@RequiredArgsConstructor
	public enum OS {
		WINDOWS("Windows"), LINUX("Linux");

		private final String os;
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
						"[stopCubeOneModule] Successfull starting CubeOne Module. execute Manager = [%s] Auth = [%s] Target Host = [%s]",
						args);
			}

			@Override
			public String error(String... args) {
				// TODO Auto-generated method stub
				return log(
						"[stopCubeOneModule] Failed starting CubeOne Module. execute Manager = [%s] Auth = [%s] Target Host = [%s]",
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

	public enum Command {
		;

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
			BASH {
				@Override
				public String build(String... args) {
					return "source .bash_profile;";
				}
			},
			COUNT_ENC_ERR {
				@Override
				public String build(String... args) {

					AIChecker checker = new AIChecker();
					if (args[0] != null && args[1] != null && args[2] != null) {
						String directory = checker.addMissingFlash(args[0]);
						String date = args[1];
						String sid = args[2];

						return String.format(BASH.build()
								+ "grep -c '[ERROR]' $COHOME%s%s_%s_ENC_* | awk -F: '{sum += $NF}END {print sum}'",
								directory, date, sid);
					}
					return null;
				}

			},
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

						return String.format(BASH.build() + "$COHOME%scubeone_%s.sh start", directory, instance);
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
			GETPROCESS {

				@Override
				public String build(String... args) {

					String sortBy = args[0];
					if (sortBy.equals("") || sortBy == null) {
						sortBy = "cpu";
					}
					if (sortBy.toLowerCase().contains("mem")) {
						sortBy = "mem";
					}

					if (args[1] != null && args[1] != "") {
						String limit = args[1];
						return String.format("ps aux --sort=-%s | head -n %s", sortBy, limit);
					}

					return String.format("ps aux --sort=-%s | head -n 11", sortBy);
				}

			};

			public enum Windows {

			}

			public abstract String build(String... args);

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
			SYSTEM_UPTIME("1.3.6.1.1.2.1.1.3"), INTERFACE_NAME("1.3.6.1.2.1.2.2.1.2"),
			INTERFACE_TYPE("1.3.6.1.2.1.2.2.1.2"), INTERFACE_MTU("1.3.6.1.2.1.2.2.1.4"),
			INTERFACE_SPEED("1.3.5.1.2.1.2.2.1.5"), INTERFACE_MACADDRESS("1.3.5.1.2.1.2.2.1.6"),
			IP_DEFAULT_TTL("1.3.5.1.2.1.4.2"), INTERFACE_IP("1.3.5.1.2.1.4.20.1.1"),
			INTERFACE_NETMASK("1.3.6.1.2.1.4.20.1.3"), HW_UPTIME("1.3.6.1.2.1.25.1.1"), HW_TIME("1.3.6.1.2.1.25.1.2"),
			NETWORK_DEVICE_INFO("1.3.6.1.2.1.25.3.4"), CPU_USAGE_1MIN("1.3.6.1.4.1.2021.10.1.3.1"),
			CPU_USAGE_5MIN("1.3.6.1.4.1.s2021.10.1.3.2"), CPU_USAGE_15MIN("1.3.6.1.4.1.2021.10.1.3.3"),
			CPU_SYSTEM_TIME("1.3.6.1.4.1.2021.11.52.0"), CPU_IDLE_TIME("1.3.6.1.4.1.2021.11.53.0"),
			DISK_INFO("1.3.6.1.2.1.25.2.3.1.3"), DISK_TYPE("1.3.6.1.2.1.25.2.3.1.2"),
			DISK_TOTAL("1.3.6.1.2.1.25.2.3.1.5"), DISK_USED("1.3.6.1.2.1.25.2.3.1.6"),
			SWAP_TOTAL("1.3.6.1.4.1.2021.4.1.3"), SWAP_FREE("1.3.6.1.4.1.2021.4.1.4"),
			PHYSICAL_TOTAL("1.3.6.1.4.1.2021.4.1.5"), MEMORY_TOTAL("1.3.6.1.2.1.25.2.2"),
			PHYSICAL_FREE("1.3.6.1.4.1.2021.4.1.6"), MEMORY_FREE("1.3.6.1.4.1.2021.4.1.11"),
			SHARED_MEMORY("1.3.6.1.4.1.2021.4.1.13"), BUFFER_MEMORY("1.3.6.1.4.1.2021.4.1.14"),
			CACHE_MEMORY("1.3.6.1.4.1.2021.4.1.15");

			private final String oid;

			private OID(String oid) {
				this.oid = oid;

			}
		}
	}
}
