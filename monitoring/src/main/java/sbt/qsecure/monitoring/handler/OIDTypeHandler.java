package sbt.qsecure.monitoring.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import sbt.qsecure.monitoring.constant.OID;

@MappedTypes(OID.class)
public class OIDTypeHandler implements TypeHandler<OID> {

	@Override
	public void setParameter(PreparedStatement ps, int i, OID parameter, JdbcType jdbcType) throws SQLException {
		ps.setString(i, parameter.getOid());

	}

	@Override
	public OID getResult(ResultSet rs, String columnName) throws SQLException {
		String key = rs.getString(columnName);
		return getOID(key);
	}

	@Override
	public OID getResult(ResultSet rs, int columnIndex) throws SQLException {
		String key = rs.getString(columnIndex);
		return getOID(key);
	}

	@Override
	public OID getResult(CallableStatement cs, int columnIndex) throws SQLException {
		String key = cs.getString(columnIndex);
		return getOID(key);
	}

	private OID getOID(String type) {
		return switch (type) {
		case "SYSTEM_INFOMATION" -> OID.SYSTEM_INFOMATION;
		case "SYSTEM_UPTIME" -> OID.SYSTEM_UPTIME;
		case "INTERFACE_NAME" -> OID.INTERFACE_NAME;
		case "INTERFACE_MTU" -> OID.INTERFACE_MTU;
		case "INTERFACE_SPEED" -> OID.INTERFACE_SPEED;
		case "INTERFACE_MACADDRESS" -> OID.INTERFACE_MACADDRESS;
		case "IP_DEFAULT_TTL" -> OID.IP_DEFAULT_TTL;
		case "INTERFACE_IP" -> OID.INTERFACE_IP;
		case "INTERFACE_NETMASK" -> OID.INTERFACE_NETMASK;
		case "HW_UPTIME" -> OID.HW_UPTIME;
		case "HW_TIME" -> OID.HW_TIME;
		case "NETWORK_DEVICE_INFO" -> OID.NETWORK_DEVICE_INFO;
		case "CPU_USAGE_1MIN" -> OID.CPU_USAGE_1MIN;
		case "CPU_USAGE_5MIN" -> OID.CPU_USAGE_5MIN;
		case "CPU_USAGE_15MIN" -> OID.CPU_USAGE_15MIN;
		case "CPU_SYSTEM_TIME" -> OID.CPU_SYSTEM_TIME;
		case "CPU_IDLE_TIME" -> OID.CPU_IDLE_TIME;
		case "DISK_INFO" -> OID.DISK_INFO;
		case "DISK_TYPE" -> OID.DISK_TYPE;
		case "DISK_TOTAL" -> OID.DISK_TOTAL;
		case "DISK_USED" -> OID.DISK_USED;
		case "SWAP_TOTAL" -> OID.SWAP_TOTAL;
		case "SWAP_FREE" -> OID.SWAP_FREE;
		case "PHYSICAL_TOTAL" -> OID.PHYSICAL_TOTAL;
		case "MEMORY_TOTAL" -> OID.MEMORY_TOTAL;
		case "PHYSICAL_FREE" -> OID.PHYSICAL_FREE;
		case "MEMORY_FREE" -> OID.MEMORY_FREE;
		case "SHARED_MEMORY" -> OID.SHARED_MEMORY;
		case "BUFFER_MEMORY" -> OID.BUFFER_MEMORY;
		case "CACHE_MEMORY" -> OID.CACHE_MEMORY;
		default -> null;
		};
	}

}
