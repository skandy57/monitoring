package sbt.qsecure.monitoring.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;
import sbt.qsecure.monitoring.constant.Server;

@MappedTypes(Server.Command.OID.class)
public class OIDTypeHandler implements TypeHandler<Server.Command.OID> {

	@Override
	public void setParameter(PreparedStatement ps, int i, Server.Command.OID parameter, JdbcType jdbcType) throws SQLException {
		ps.setString(i, parameter.getOid());

	}

	@Override
	public Server.Command.OID getResult(ResultSet rs, String columnName) throws SQLException {
		String key = rs.getString(columnName);
		return getOID(key);
	}

	@Override
	public Server.Command.OID getResult(ResultSet rs, int columnIndex) throws SQLException {
		String key = rs.getString(columnIndex);
		return getOID(key);
	}

	@Override
	public Server.Command.OID getResult(CallableStatement cs, int columnIndex) throws SQLException {
		String key = cs.getString(columnIndex);
		return getOID(key);
	}

	private Server.Command.OID getOID(String type) {
		return switch (type) {
		case "SYSTEM_INFOMATION" -> Server.Command.OID.SYSTEM_INFOMATION;
		case "SYSTEM_UPTIME" -> Server.Command.OID.SYSTEM_UPTIME;
		case "INTERFACE_NAME" -> Server.Command.OID.INTERFACE_NAME;
		case "INTERFACE_MTU" -> Server.Command.OID.INTERFACE_MTU;
		case "INTERFACE_SPEED" -> Server.Command.OID.INTERFACE_SPEED;
		case "INTERFACE_MACADDRESS" -> Server.Command.OID.INTERFACE_MACADDRESS;
		case "IP_DEFAULT_TTL" -> Server.Command.OID.IP_DEFAULT_TTL;
		case "INTERFACE_IP" -> Server.Command.OID.INTERFACE_IP;
		case "INTERFACE_NETMASK" -> Server.Command.OID.INTERFACE_NETMASK;
		case "HW_UPTIME" -> Server.Command.OID.HW_UPTIME;
		case "HW_TIME" -> Server.Command.OID.HW_TIME;
		case "NETWORK_DEVICE_INFO" -> Server.Command.OID.NETWORK_DEVICE_INFO;
		case "CPU_USAGE_1MIN" -> Server.Command.OID.CPU_USAGE_1MIN;
		case "CPU_USAGE_5MIN" -> Server.Command.OID.CPU_USAGE_5MIN;
		case "CPU_USAGE_15MIN" -> Server.Command.OID.CPU_USAGE_15MIN;
		case "CPU_SYSTEM_TIME" -> Server.Command.OID.CPU_SYSTEM_TIME;
		case "CPU_IDLE_TIME" -> Server.Command.OID.CPU_IDLE_TIME;
		case "DISK_INFO" -> Server.Command.OID.DISK_INFO;
		case "DISK_TYPE" -> Server.Command.OID.DISK_TYPE;
		case "DISK_TOTAL" -> Server.Command.OID.DISK_TOTAL;
		case "DISK_USED" -> Server.Command.OID.DISK_USED;
		case "SWAP_TOTAL" -> Server.Command.OID.SWAP_TOTAL;
		case "SWAP_FREE" -> Server.Command.OID.SWAP_FREE;
		case "PHYSICAL_TOTAL" -> Server.Command.OID.PHYSICAL_TOTAL;
		case "MEMORY_TOTAL" -> Server.Command.OID.MEMORY_TOTAL;
		case "PHYSICAL_FREE" -> Server.Command.OID.PHYSICAL_FREE;
		case "MEMORY_FREE" -> Server.Command.OID.MEMORY_FREE;
		case "SHARED_MEMORY" -> Server.Command.OID.SHARED_MEMORY;
		case "BUFFER_MEMORY" -> Server.Command.OID.BUFFER_MEMORY;
		case "CACHE_MEMORY" -> Server.Command.OID.CACHE_MEMORY;
		default -> null;
		};
	}

}
