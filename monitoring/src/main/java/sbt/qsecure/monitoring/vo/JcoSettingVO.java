package sbt.qsecure.monitoring.vo;

import java.util.Objects;

public record JcoSettingVO(String aiHost, String instance, String connectionCount, String gwHost, String progId, String gwPort,
		String repositoryDestination, String workerThreadCount) {

	public JcoSettingVO(String aiHost, String instance, String connectionCount, String gwHost, String progId, String gwPort,
			String repositoryDestination, String workerThreadCount) {

		String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();

		this.aiHost = Objects.requireNonNull(aiHost,
				"[" + methodName + "] The value of parameter 'aiHost' is required. Provided value = [" + aiHost + "]");

		this.instance = Objects.requireNonNull(instance,
				"[" + methodName + "] The value of parameter 'instance' is required. Provided value = " + instance);

		this.connectionCount = Objects.requireNonNull(connectionCount,
				"[" + methodName + "] The value of parameter 'connectionCount' is required. Provided value = [" + connectionCount + "]");

		this.gwHost = Objects.requireNonNull(gwHost,
				"[" + methodName + "] The value of parameter 'gwHost' is required. Provided value = [" + gwHost + "]");

		this.progId = Objects.requireNonNull(progId,
				"[" + methodName + "] The value of parameter 'progId' is required. Provided value = [" + progId + "]");

		this.gwPort = Objects.requireNonNull(gwPort,
				"[" + methodName + "] The value of parameter 'gwPort' is required. Provided value = [" + gwPort + "]");

		this.repositoryDestination = Objects.requireNonNull(repositoryDestination,
				"[" + methodName + "] The value of parameter 'repositoryDestination' is required. Provided value = [" + repositoryDestination + "]");

		this.workerThreadCount = Objects.requireNonNull(workerThreadCount,
				"[" + methodName + "] The value of parameter 'workerThreadCount' is required. Provided value = [" + workerThreadCount + "]");

	}

}
