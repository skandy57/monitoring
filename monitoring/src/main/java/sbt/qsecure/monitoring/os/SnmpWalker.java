package sbt.qsecure.monitoring.os;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.fluent.SnmpBuilder;
import org.snmp4j.fluent.SnmpCompletableFuture;
import org.snmp4j.fluent.TargetBuilder;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.Integer32;
import sbt.qsecure.monitoring.constant.*;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;

public class SnmpWalker {
    private static final int DEFAULT_PORT = 9999;
    private static final String DEFAULT_IP = "127.0.0.1";
    private static final String DEFAULT_OID = "1.3.6.1.4.1.14586.100.77.1";

    public void snmp1() {
        System.out.println(1);

        try (Snmp snmp = createSnmpInstance()) {
            System.out.println(2);

            Address targetAddress = GenericAddress.parse("udp:127.0.0.1/161");

            Target<Address> target = createSnmpTarget(targetAddress);
            System.out.println(2);

            List<VariableBinding> variableBindings = createVariableBindingsForCpu();
            System.out.println(3);

            performSnmpOperation(snmp, target, variableBindings);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void snmpWalk(sbt.qsecure.monitoring.constant.OID oidEnum) {
        System.out.println("SNMP Walk for OID: " + oidEnum.getOid());

        try (Snmp snmp = createSnmpInstance()) {
            Address targetAddress = GenericAddress.parse("udp:127.0.0.1/161");
            Target<Address> target = createSnmpTarget(targetAddress);
            List<VariableBinding> variableBindings = createVariableBindingsForWalk(oidEnum);

            performSnmpOperation(snmp, target, variableBindings);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void performSnmpOperation(Snmp snmp, Target<Address> target, List<VariableBinding> variableBindings) throws IOException {
        try {
            PDU pdu = createPDU(variableBindings);

            SnmpCompletableFuture snmpRequestFuture = SnmpCompletableFuture.send(snmp, target, pdu);
            List<VariableBinding> vbs = snmpRequestFuture.get().getAll();

            if (snmpRequestFuture.getResponseEvent().getResponse().getErrorStatus() == PDU.noError) {
                System.out.println("Received: " + vbs);
                for (VariableBinding vb : vbs) {
                    System.out.println("Payload: " + vb.getOid() + " " + vb.getVariable());
                }
            } else {
                System.out.println("Error: " + snmpRequestFuture.getResponseEvent().getResponse().getErrorStatusText());
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private static List<VariableBinding> createVariableBindingsForWalk(sbt.qsecure.monitoring.constant.OID oidEnum) {
        List<VariableBinding> list = new ArrayList<>();
        // Use the specified OID for SNMP walk
        list.add(new VariableBinding(new OID(oidEnum.getOid())));
        return list;
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
}
