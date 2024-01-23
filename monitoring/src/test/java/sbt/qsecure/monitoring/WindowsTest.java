package sbt.qsecure.monitoring;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.net.InetAddress;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;
import java.net.*;
import java.util.*;
import org.snmp4j.*;
import org.snmp4j.PDU;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.fluent.SnmpBuilder;
import org.snmp4j.fluent.SnmpCompletableFuture;
import org.snmp4j.fluent.TargetBuilder;
import org.snmp4j.transport.*;
import org.snmp4j.smi.*;
import org.springframework.boot.test.context.SpringBootTest;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class WindowsTest {

	static int defaultPort = 9999;
	static String defaultIP = "127.0.0.1";
	static String defaultOID = "1.3.6.1.4.1.14586.100.77.1";

	@Test
	public void snmp1() throws InterruptedException, ExecutionException, IOException {
	    System.out.println(1);

	    try (Snmp snmp = createSnmpInstance()) {
	        System.out.println(2);

	        Address targetAddress = GenericAddress.parse("udp:127.0.0.1/161");

	        Target<Address> target = createSnmpTarget(targetAddress);
	        System.out.println(2);

	        List<VariableBinding> variableBindings = createVariableBindingsForCpu();
	        System.out.println(3);

	        PDU pdu = createPDU(variableBindings);

	        SnmpCompletableFuture snmpRequestFuture = SnmpCompletableFuture.send(snmp, target, pdu);
	        List<VariableBinding> vbs = snmpRequestFuture.get().getAll();

	        if (snmpRequestFuture.getResponseEvent().getResponse().getErrorStatus() == PDU.noError) {
	            System.out.println("Received: " + vbs);
//	            System.out.println("Payload:  " + vbs.get(0).getVariable());
	            for(VariableBinding vb : vbs) {
	            	System.out.println("PayLoad :"+vb.getOid() +" "+vb.getVariable());
	            }
	            
	        } else {
	            System.out.println("Error: " + snmpRequestFuture.getResponseEvent().getResponse().getErrorStatusText());
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	private static List<VariableBinding> createVariableBindingsForCpu() {
	    List<VariableBinding> list = new ArrayList<>();
	    // Use the correct OID for CPU usage on your device, below is an example for Cisco devices
	    list.add(new VariableBinding(new OID("1.3.6.1.1.2.1.1.1"))); // CPU 5-minute load
	    return list;
	}

	    private static Snmp createSnmpInstance() throws IOException {
	        SnmpBuilder snmpBuilder = new SnmpBuilder();
	        return snmpBuilder.udp().v2c().threads(2).build();
	        // For TCP, use: snmpBuilder.tcp().v2c().threads(2).build();
	    }

	    private static Target<Address> createSnmpTarget(Address targetAddress) {
	        CommunityTarget<Address> target = new CommunityTarget<>();
	        target.setSecurityName(new OctetString("public"));
	        target.setTimeout(500);
	        target.setRetries(1);
	        target.setAddress(targetAddress);
	        target.setVersion(TargetBuilder.SnmpVersion.v2c.getVersion());
	        return target;
	    }

	    private static List<VariableBinding> createVariableBindings() {
	        List<VariableBinding> list = new ArrayList<>();
	        list.add(new VariableBinding(new OID("1.3.6.1.4.1.9.9.109.1.1.1.1.6"))); // CPU Usage
	        // Add more OIDs for memory usage or other metrics if needed
	        return list;
	    }

	    private static PDU createPDU(List<VariableBinding> variableBindings) {
	        PDU pdu = new PDU();
	        pdu.setType(PDU.GET);
	        pdu.setRequestID(new Integer32(1));
	        pdu.setVariableBindings(variableBindings);
	        return pdu;
	    } 

//	@Test
//	public void 운영체제정보출력() {
//
//		// 원격 서버에서 실행할 명령
//		String command = "systeminfo";
//		try {
//			// 명령 실행을 위한 ProcessBuilder 생성
//			ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", command);
//
//			// 원격 서버로 접속하기 위해 SSH 또는 다른 안전한 프로토콜을 사용해야 합니다.
//
//			// 명령 실행
//			Process process = processBuilder.start();
//
//			// 명령 실행 결과를 읽기 위한 BufferedReader 생성
//			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//			String line;
//
//			// 명령 실행 결과를 출력
//			while ((line = reader.readLine()) != null) {
//				System.out.println(line);
//			}
//
//			// 프로세스 종료를 기다림
//			int exitCode = process.waitFor();
//			System.out.println("Exit Code: " + exitCode);
//
//		} catch (IOException | InterruptedException e) {
//			e.printStackTrace();
//		}
//	}

//	@Test
//	public void 현재컴퓨터의cpu사용량출력() {
//		OperatingSystemMXBean osBean = (com.sun.management.OperatingSystemMXBean) ManagementFactory
//				.getOperatingSystemMXBean();
//
//		double load = 0;
//
//		while (true) {
//
//			load = ((com.sun.management.OperatingSystemMXBean) osBean).getSystemCpuLoad();
//
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//
//			if (load * 100.0 > 0.0)
//				break;
//
//		}
//
//		File f = new File("/");
//
//		System.out.println("CPU ==============================");
//
//		System.out.println("- Usage : " + load * 100.0);
//
//		System.out.println("- Usage Percent : " + Math.round(load * 100.0) + "%");
//
//		System.out.println("- Idle  Percent : " + (100 - Math.round(load * 100.0)) + "%\n");
//
//		System.out.println("HDD ==============================");
//
//		System.out.println("- Total : " + Math.round(f.getTotalSpace() / (1024 * 1024) / 1000.0) + "(GB)");
//
//		System.out.println(
//				"- Usage : " + Math.round((f.getTotalSpace() - f.getUsableSpace()) / (1024 * 1024) / 1000.0) + "(GB)");
//
//		System.out.println("- Idle  : " + Math.round(f.getUsableSpace() / (1024 * 1024) / 1000.0) + "(GB)");
//
//		System.out.println("- Usage Percent : " + Math
//				.round(Double.valueOf(f.getTotalSpace() - f.getUsableSpace()) / Double.valueOf(f.getTotalSpace()) * 100)
//				+ "%");
//
//		System.out.println("- Idle  Percent : "
//				+ Math.round(Double.valueOf(f.getUsableSpace()) / Double.valueOf(f.getTotalSpace()) * 100) + "%\n");
//
//		System.out.println("Memory============================");
//
//		System.out.println("- TotalPhysicalMemorySize: "
//				+ Math.round(((com.sun.management.OperatingSystemMXBean) osBean).getTotalPhysicalMemorySize()
//						/ (1024 * 1024) / 1000.0)
//				+ "(GB)");
//
//		System.out.println("- FreePhysicalMemorySize: "
//				+ Math.round(((com.sun.management.OperatingSystemMXBean) osBean).getFreePhysicalMemorySize()
//						/ (1024 * 1024) / 1000.0)
//				+ "(GB)");
//
//		System.out.println("- Usage Percent : "
//				+ Math.round(
//						(Double.valueOf(((com.sun.management.OperatingSystemMXBean) osBean).getTotalPhysicalMemorySize()
//								- ((com.sun.management.OperatingSystemMXBean) osBean).getFreePhysicalMemorySize()))
//								/ Double.valueOf(((com.sun.management.OperatingSystemMXBean) osBean)
//										.getTotalPhysicalMemorySize())
//								* 100)
//				+ "%");
//
//		System.out.println("- Idle  Percent : " + Math.round(
//				Double.valueOf(((com.sun.management.OperatingSystemMXBean) osBean).getFreePhysicalMemorySize()) / Double
//						.valueOf(((com.sun.management.OperatingSystemMXBean) osBean).getTotalPhysicalMemorySize())
//						* 100)
//				+ "%");
//
//	}

//	@Test
//	public void ssh통신으로운영체제정보출력() {
//		try {
//			JSch jsch = new JSch();
//			Session session = jsch.getSession("Administrator", "127.0.0.1", 33054);
//			session.setPassword("Adumin123");
//			session.setConfig("StrictHostKeyChecking", "no");
//			session.connect();
//
//			// 명령 실행
//			String command = "systeminfo";
//			ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
//			channelExec.setCommand(command);
//
//			// 명령 실행 결과 받기
//			java.io.InputStream in = channelExec.getInputStream();
//			channelExec.connect();
//
//			byte[] tmp = new byte[1024];
//			while (true) {
//				while (in.available() > 0) {
//					int i = in.read(tmp, 0, 1024);
//					if (i < 0)
//						break;
//					System.out.print(new String(tmp, 0, i));
//				}
//				if (channelExec.isClosed()) {
//					if (in.available() > 0)
//						continue;
//					System.out.println("Exit Status: " + channelExec.getExitStatus());
//					break;
//				}
//				try {
//					Thread.sleep(1000);
//				} catch (Exception ee) {
//				}
//			}
//			channelExec.disconnect();
//			session.disconnect();
//		} catch (JSchException | java.io.IOException e) {
//			e.printStackTrace();
//		}
//	}

}
