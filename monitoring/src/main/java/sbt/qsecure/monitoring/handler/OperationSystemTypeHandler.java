package sbt.qsecure.monitoring.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;
import sbt.qsecure.monitoring.constant.Server;
import sbt.qsecure.monitoring.constant.Server.OS;

@MappedTypes(OS.class)
public class OperationSystemTypeHandler implements TypeHandler<OS> {

	@Override
	public void setParameter(PreparedStatement ps, int i, OS parameter, JdbcType jdbcType)
			throws SQLException {
		ps.setString(i, parameter.getOs());
	}

	@Override
	public OS getResult(ResultSet rs, String columnName) throws SQLException {
		String key = rs.getString(columnName);
		return getOS(key);
	}

	@Override
	public OS getResult(ResultSet rs, int columnIndex) throws SQLException {
		String key = rs.getString(columnIndex);
		return getOS(key);
	}

	@Override
	public OS getResult(CallableStatement cs, int columnIndex) throws SQLException {
		String key = cs.getString(columnIndex);
		return getOS(key);
	}

	private OS getOS(String type) {
		return switch (type) {
		case "Windows" -> Server.OS.WINDOWS;
		case "Linux" -> Server.OS.LINUX;
		default -> null;
		};
	}

}
