package sbt.qsecure.monitoring.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import sbt.qsecure.monitoring.constant.Server;

@MappedTypes(Server.class)
public class ServerTypeHandler implements TypeHandler<Server> {

	@Override
	public void setParameter(PreparedStatement ps, int i, Server parameter, JdbcType jdbcType) throws SQLException {
		ps.setString(i, parameter.getType());

	}

	@Override
	public Server getResult(ResultSet rs, String columnName) throws SQLException {
		String key = rs.getString(columnName);
		return getServer(key);
	}

	@Override
	public Server getResult(ResultSet rs, int columnIndex) throws SQLException {
		String key = rs.getString(columnIndex);
		return getServer(key);
	}

	@Override
	public Server getResult(CallableStatement cs, int columnIndex) throws SQLException {
		String key = cs.getString(columnIndex);
		return getServer(key);
	}

	private Server getServer(String type) {
		
		return switch(type) {
		case "A/I Server"->Server.AI;
		case "Security Server"->Server.SECURITY;
		case "Manager Server"->Server.MANAGER;
		default -> null;
		};
	}

}
