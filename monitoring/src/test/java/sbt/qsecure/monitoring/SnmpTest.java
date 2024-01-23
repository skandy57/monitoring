package sbt.qsecure.monitoring;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.AuthSHA;
import org.snmp4j.security.PrivAES128;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.*;
import org.snmp4j.smi.Address;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class SnmpTest {

//	@Test
//	public void cpu와memory사용량() {
//		 try (Snmp snmp = createSnmpInstance()) {
//	            Address targetAddress = GenericAddress.parse("udp:127.0.0.1/161");
//	            Target<Address> target = createSnmpTarget(targetAddress);
//	            List<VariableBinding> variableBindings = createVariableBindingsForCpu();
//	            PDU pdu = createPDU(variableBindings);
//
//	            ResponseEvent responseEvent = snmp.send(pdu, target);
//	            PDU responsePDU = responseEvent.getResponse();
//
//	            if (responsePDU != null && responsePDU.getErrorStatus() == PDU.noError) {
//	            	List<? extends VariableBinding> vbs = responsePDU.getVariableBindings();
//	                System.out.println("Received: " + vbs);
//	                System.out.println("Payload:  " + vbs.get(0).getVariable());
//	            } else {
//	                System.out.println("Error: " + responsePDU.getErrorStatusText());
//	            }
//	        } catch (Exception e) {
//	            e.printStackTrace();
//	        }
//	    }
//
//	    private static List<VariableBinding> createVariableBindingsForCpu() {
//	        List<VariableBinding> list = new ArrayList<>();
//	        list.add(new VariableBinding(new OID("1.3.6.1.2.1.25.3.3.1.2"))); // CPU Usage
//	        return list;
//	    }
//
//	    private static Snmp createSnmpInstance() throws IOException {
//	        TransportMapping<? extends Address> transport = new DefaultUdpTransportMapping();
//	        MessageDispatcher dispatcher = new MessageDispatcherImpl();
//	        Snmp snmp = new Snmp(transport, dispatcher);
//	        snmp.getMessageDispatcher().addMessageProcessingModel(new MPv3());
//	        transport.listen();
//	        return snmp;
//	    }
//
//	    private static Target<Address> createSnmpTarget(Address targetAddress) {
//	        CommunityTarget<Address> target = new CommunityTarget<>();
//	        target.setSecurityName(new OctetString("public"));
//	        target.setSecurityLevel(SecurityLevel.AUTH_PRIV);
//	        target.setTimeout(500);
//	        target.setRetries(1);
//	        target.setAddress(targetAddress);
//	        target.setVersion(SnmpConstants.version3);
//	        OctetString authPassphrase = new OctetString("yourAuthPassphrase");
//	        OctetString privPassphrase = new OctetString("yourPrivPassphrase");
//	        OID authProtocol = AuthSHA.ID;
//	        OID privProtocol = PrivAES128.ID;
//
//	        UsmUser user = new UsmUser(
//	                new OctetString("yourUserName"),
//	                authProtocol,
//	                authPassphrase,
//	                privProtocol,
//	                privPassphrase
//	        );
//
//	        USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID()), 0);
//	        usm.addUser(user);
//
//	        SecurityModels.getInstance().addSecurityModel(usm);
//
//	        
//	        return target;
//	    }
//
//	    private static PDU createPDU(List<VariableBinding> variableBindings) {
//	        PDU pdu = new PDU();
//	        pdu.setType(PDU.GETNEXT); // Use GETNEXT for CPU usage
//	        pdu.setRequestID(new Integer32(1));
//	        pdu.setVariableBindings(variableBindings);
//	        return pdu;
//	    }
}
