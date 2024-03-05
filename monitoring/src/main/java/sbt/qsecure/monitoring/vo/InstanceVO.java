package sbt.qsecure.monitoring.vo;

import java.util.Objects;

import sbt.qsecure.monitoring.checker.AIValidator;

public record InstanceVO(String aiHost, String instance, String directory) {

	public InstanceVO(String aiHost, String instance, String directory) {
		AIValidator.validateParameters(aiHost, instance, directory);

		this.aiHost = aiHost;
		this.instance = instance;
		this.directory = directory;
	}
}
