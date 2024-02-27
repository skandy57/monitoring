package sbt.qsecure.monitoring.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import sbt.qsecure.monitoring.constant.Server;
import sbt.qsecure.monitoring.constant.Server.Version;

@MappedTypes(Version.class)
public class VersionHandler implements TypeHandler<Version> {

	@Override
	public void setParameter(PreparedStatement ps, int i, Version parameter, JdbcType jdbcType) throws SQLException {
		ps.setString(i, parameter.getVersion());

	}

	@Override
	public Version getResult(ResultSet rs, String columnName) throws SQLException {
		String key = rs.getString(columnName);
		return getVersion(key);
	}

	@Override
	public Version getResult(ResultSet rs, int columnIndex) throws SQLException {
		String key = rs.getString(columnIndex);
		return getVersion(key);
	}

	@Override
	public Version getResult(CallableStatement cs, int columnIndex) throws SQLException {
		String key = cs.getString(columnIndex);
		return getVersion(key);
	}

	private Version getVersion(String type) {
		type = type.toLowerCase();
		return switch (type) {
		case "new" -> Version.NEW;
		case "old" -> Version.OLD;
		default -> null;
		};
	}
}
