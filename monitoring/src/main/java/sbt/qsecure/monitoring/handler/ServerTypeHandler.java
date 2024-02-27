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
import sbt.qsecure.monitoring.constant.Server.Type;

@MappedTypes(Type.class)
public class ServerTypeHandler implements TypeHandler<Type> {

	@Override
	public void setParameter(PreparedStatement ps, int i, Type parameter, JdbcType jdbcType) throws SQLException {
		ps.setString(i, parameter.getType());

	}

	@Override
	public Type getResult(ResultSet rs, String columnName) throws SQLException {
		String key = rs.getString(columnName);
		return getServer(key);
	}

	@Override
	public Type getResult(ResultSet rs, int columnIndex) throws SQLException {
		String key = rs.getString(columnIndex);
		return getServer(key);
	}

	@Override
	public Type getResult(CallableStatement cs, int columnIndex) throws SQLException {
		String key = cs.getString(columnIndex);
		return getServer(key);
	}

	private Type getServer(String type) {
		type = type.toLowerCase();
		return switch (type) {
		case "a/i server" -> Type.AI;
		case "security server" -> Type.SECURITY;
		case "manager server" -> Type.MANAGER;
		default -> null;
		};
	}

}
