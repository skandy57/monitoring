package sbt.qsecure.monitoring.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import sbt.qsecure.monitoring.constant.OperationSystem;

@MappedTypes(OperationSystem.class)
public class OperationSystemTypeHandler implements TypeHandler<OperationSystem> {

	@Override
	public void setParameter(PreparedStatement ps, int i, OperationSystem parameter, JdbcType jdbcType)
			throws SQLException {
		ps.setString(i, parameter.getOs());
	}

	@Override
	public OperationSystem getResult(ResultSet rs, String columnName) throws SQLException {
		String key = rs.getString(columnName);
		return getOS(key);
	}

	@Override
	public OperationSystem getResult(ResultSet rs, int columnIndex) throws SQLException {
		String key = rs.getString(columnIndex);
		return getOS(key);
	}

	@Override
	public OperationSystem getResult(CallableStatement cs, int columnIndex) throws SQLException {
		String key = cs.getString(columnIndex);
		return getOS(key);
	}

	private OperationSystem getOS(String type) {
		return switch (type) {
		case "Windows" -> OperationSystem.WINDOWS;
		case "Linux" -> OperationSystem.LINUX;
		default -> null;
		};
	}

}
