package sbt.qsecure.monitoring.os;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;

import lombok.extern.slf4j.Slf4j;
import sbt.qsecure.monitoring.constant.Server;
import sbt.qsecure.monitoring.constant.Server.Command.Linux;
import sbt.qsecure.monitoring.constant.Server.Version;
import sbt.qsecure.monitoring.vo.DbSettingVO;
import sbt.qsecure.monitoring.vo.JcoSettingVO;
import sbt.qsecure.monitoring.vo.SapSettingVO;
import sbt.qsecure.monitoring.vo.ServerVO;

@Slf4j
public class LinuxConfigInitializer extends LinuxConnector implements OSInitializer {

	public LinuxConfigInitializer(ServerVO server) {
		super(server);
	}

	public Version getModuleVersion() throws Exception {
		String result = sendCommand("source .bash_profile;ls -l $COHOME");

		if (result.contains("JCOCubeOneServer")) {
			return Version.OLD;
		} else if (result.contains("aisvr")) {
			return Version.NEW;
		}

		return null;
	}

	public SapSettingVO getSapSetting(String instance) throws Exception {
		String lang = null;
		String client = null;
		String peakLimit = null;
		String passwd = null;
		String user = null;
		String sysnr = null;
		String poolCapacity = null;
		String asHost = null;

		String directory = null;
		String command = null;

		String fileExtend = null;
		switch (getModuleVersion()) {
		case NEW -> directory = "$COHOME/aisvr/" + instance + "/CubeOneJcoServer.db";
		case OLD -> directory = "$COHOME/JCOCubeOneServer/" + instance + "/CubeOneJcoServer.db";
		}
		;
		((ChannelExec) channel)
				.setCommand("source .bash_profile;cat $COHOME/aisvr/" + instance + "/SAP_SERVER.jcodestination");

		try (InputStream inputStream = channel.getInputStream();
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader reader = new BufferedReader(inputStreamReader)) {

			String line;

			while ((line = reader.readLine()) != null) {
				String[] parts = line.split("=", 2);
				if (parts.length == 2) {

					String key = parts[0].trim();
					String value = parts[1].trim();

					switch (key.toLowerCase().replaceAll("[^a-zA-Z0-9]", "")) {
					case "lang" -> lang = value;
					case "client" -> client = value;
					case "peaklimit" -> peakLimit = value;
					case "passwd" -> passwd = value;
					case "user" -> user = value;
					case "sysnr" -> sysnr = value;
					case "poolcapacity" -> poolCapacity = value;
					case "ashost" -> asHost = value;
					}
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			channel.disconnect();
			session.disconnect();
		}

		try {
			requireNonNullParams(lang, client, peakLimit, passwd, user, sysnr, poolCapacity, asHost);
		} catch (NullPointerException e) {
			e.printStackTrace();
			return null;
		}

		return new SapSettingVO(this.host, instance, lang, client, peakLimit, passwd, user, sysnr, poolCapacity,
				asHost);
	}

	public DbSettingVO getDbSetting(String instance) throws Exception {
		String ip = null;
		String indicator = null;
		String port = null;
		String encLogFile = null;
		String userId = null;
		String logFile = null;
		String db = null;
		String encLog = null;
		String maxPoolSize = null;
		String item = null;
		String log = null;
		String dbName = null;
		String password = null;
		String oldVersion = null;
		String difTab = null;
		String oldApi = null;
		String fixUser = null;

		String directory = null;

		switch (getModuleVersion()) {
		case NEW -> directory = "$COHOME/aisvr/" + instance + "/CubeOneJcoServer.db";
		case OLD -> directory = "$COHOME/JCOCubeOneServer/" + instance + "/CubeOneJcoServer.db";
		}
		;
		String command = Linux.BASH + "cat " + directory;
		String result = sendCommand(command);
		String[] lines = result.split("\\r?\\n");

		for (String line : lines) {
			String[] parts = line.split("=", 2);
			if (parts.length == 2) {

				String key = parts[0].trim();
				String value = parts[1].trim();

				switch (key.toLowerCase().replaceAll("[^a-zA-Z0-9]", "")) {
				case "ip" -> ip = value;
				case "indicator" -> indicator = value;
				case "port" -> port = value;
				case "enclogfile" -> encLogFile = value;
				case "userid" -> userId = value;
				case "logfile" -> logFile = value;
				case "db" -> db = value;
				case "enclog" -> encLog = value;
				case "maxpoolsize" -> maxPoolSize = value;
				case "item" -> item = value;
				case "log" -> log = value;
				case "dbname" -> dbName = value;
				case "password" -> password = value;
				case "oldversion" -> oldVersion = value;
				case "diftab" -> difTab = value;
				}
			}
		}

		try {
			requireNonNullParams(indicator, encLogFile, userId, logFile, db, encLog, maxPoolSize, item, log, dbName,
					password);
		} catch (NullPointerException e) {
			e.printStackTrace();
			return null;
		}

		return new DbSettingVO(this.host, instance, ip, indicator, port, encLogFile, userId, logFile, db, encLog,
				maxPoolSize, item, log, dbName, password, oldVersion, difTab, oldApi, fixUser);
	}

	public JcoSettingVO getJcoSetting(String instance) throws Exception {

		String connectionCount = null;
		String gwHost = null;
		String progId = null;
		String gwPort = null;
		String repositoryDestination = null;
		String workerThreadCount = null;

		super.connect();

		if (super.channel == null || !super.channel.isConnected()) {
			try {
				channel = session.openChannel("exec");
			} catch (JSchException e) {
				e.printStackTrace();
			}
			((ChannelExec) channel)
					.setCommand("source .bash_profile;cat $COHOME/aisvr/" + instance + "/CubeOneJcoServer.jcoServer");
			try {
				channel.connect();
			} catch (JSchException e) {
				e.printStackTrace();
			}
		}

		try (InputStream inputStream = channel.getInputStream();
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader reader = new BufferedReader(inputStreamReader)) {

			String line;

			while ((line = reader.readLine()) != null) {
				String[] parts = line.split("=", 2);
				if (parts.length == 2) {

					String key = parts[0].trim();
					String value = parts[1].trim();

					switch (key.toLowerCase().replaceAll("[^a-zA-Z0-9]", "")) {
					case "connectioncount":
						connectionCount = value;
						break;
					case "gwhost":
						gwHost = value;
						break;
					case "progid":
						progId = value;
						break;
					case "gwport":
						gwPort = value;
						break;
					case "repositorydestination":
						repositoryDestination = value;
						break;
					case "workerthreadcount":
						workerThreadCount = value;
						break;
					}
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			channel.disconnect();
			session.disconnect();
		}

		try {
			requireNonNullParams(gwHost, progId, gwPort, repositoryDestination, workerThreadCount);
		} catch (NullPointerException e) {
			e.printStackTrace();
			return null;
		}

		return new JcoSettingVO(this.host, instance, connectionCount, gwHost, progId, gwPort, repositoryDestination,
				workerThreadCount);

	}

	public List<String> getInstanceDirectoryList() {
		connect();
		List<String> directories = new ArrayList<>();
		if (super.channel == null || !channel.isConnected()) {
			try {
				channel = session.openChannel("exec");
			} catch (JSchException e) {
				e.printStackTrace();
			}
			((ChannelExec) channel).setCommand("source .bash_profile;ls -l $COHOME/aisvr");
			try {
				channel.connect();
			} catch (JSchException e) {
				e.printStackTrace();
			}
		}

		try (InputStream inputStream = channel.getInputStream();
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader reader = new BufferedReader(inputStreamReader)) {

			String line;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("d") && !line.contains("log")) {
					String[] parts = line.split("\\s+");
					if (parts.length > 8) {
						directories.add(parts[8]);
					}
				}
			}
			reader.close();

			return directories;
		} catch (Exception e) {

		} finally {
			channel.disconnect();
			session.disconnect();

		}

		return directories;
	}

	private void requireNonNullParams(Object... params) {
		for (Object param : params) {
			Objects.requireNonNull(param);
		}
	}
}
