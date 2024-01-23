package sbt.qsecure.monitoring.constant;

import lombok.Getter;

@Getter
public enum OID {
	
	SYSTEM_INFOMATION("1.3.6.1.1.2.1.1.1"),
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
    CPU_USAGE_5MIN("1.3.6.1.4.1.2021.10.1.3.2"),
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
