package com.pinecone.hydra.entity.ibatis;

import com.pinecone.ulf.util.guid.GUID72;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(GUID72.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class GUID72TypeHandler extends BaseTypeHandler<GUID72> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, GUID72 parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.toString());
    }

    @Override
    public GUID72 getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        if (value == null) {
            return null; // 如果值为 null，则直接返回 null
        }
        return new GUID72( value );
    }

    @Override
    public GUID72 getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        if (value == null) {
            return null; // 如果值为 null，则直接返回 null
        }
        return new GUID72( value );
    }

    @Override
    public GUID72 getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        if (value == null) {
            return null; // 如果值为 null，则直接返回 null
        }
        return new GUID72( value );
    }
}
