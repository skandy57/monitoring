package sbt.qsecure.monitoring.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import sbt.qsecure.monitoring.constant.Auth.*;

@MappedTypes(AuthGrade.class)
public class AuthTypeHandler implements TypeHandler<AuthGrade>{

	@Override
	public void setParameter(PreparedStatement ps, int i, AuthGrade parameter, JdbcType jdbcType) throws SQLException {
		ps.setString(i, parameter.toString());
		
	}

	@Override
	public AuthGrade getResult(ResultSet rs, String columnName) throws SQLException {
		String key = rs.getString(columnName);
		return getAuth(key);
	}

	@Override
	public AuthGrade getResult(ResultSet rs, int columnIndex) throws SQLException {
		String key = rs.getString(columnIndex);
		return getAuth(key);
	}

	@Override
	public AuthGrade getResult(CallableStatement cs, int columnIndex) throws SQLException {
		String key = cs.getString(columnIndex);
		return getAuth(key);
	}

	private AuthGrade getAuth(String type) {
		return switch(type) {
		case "ADMIN" -> AuthGrade.ADMIN;
		case "MEMBER" -> AuthGrade.MEMBER;
		default -> AuthGrade.NOT_LOGIN;
		};
	}



}
