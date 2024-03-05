package sbt.qsecure.monitoring.vo;

import java.util.Objects;

public record SapSettingVO(String aiHost, String instance, String lang, String client, String peakLimit, String passwd, String user, String sysnr,
		String poolCapacity, String asHost) {

	public SapSettingVO(String aiHost, String instance, String lang, String client, String peakLimit, String passwd, String user, String sysnr,
			String poolCapacity, String asHost) {

		String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();

		this.aiHost = Objects.requireNonNull(aiHost,
				"[" + methodName + "] The value of parameter 'aiHost' is required. Provided value = [" + aiHost + "]");

		this.instance = Objects.requireNonNull(instance,
				"[" + methodName + "] The value of parameter 'instance' is required. Provided value = " + instance);

		this.lang = Objects.requireNonNull(lang, "[" + methodName + "] The value of parameter 'lang' is required. Provided value = [" + lang + "]");

		this.client = Objects.requireNonNull(client,
				"[" + methodName + "] The value of parameter 'client' is required. Provided value = [" + client + "]");

		this.peakLimit = Objects.requireNonNull(peakLimit,
				"[" + methodName + "] The value of parameter 'peakLimit' is required. Provided value = [" + peakLimit + "]");

		this.passwd = Objects.requireNonNull(passwd,
				"[" + methodName + "] The value of parameter 'passwd' is required. Provided value = [" + passwd + "]");

		this.user = Objects.requireNonNull(user, "[" + methodName + "] The value of parameter 'user' is required. Provided value = [" + user + "]");

		this.sysnr = Objects.requireNonNull(sysnr,
				"[" + methodName + "] The value of parameter 'sysnr' is required. Provided value = [" + sysnr + "]");

		this.poolCapacity = Objects.requireNonNull(poolCapacity,
				"[" + methodName + "] The value of parameter 'poolCapacity' is required. Provided value = [" + poolCapacity + "]");

		this.asHost = Objects.requireNonNull(asHost,
				"[" + methodName + "] The value of parameter 'asHost' is required. Provided value = [" + asHost + "]");

	}
}
