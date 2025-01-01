package com.pinecone.slime.jelly.source.ds.jdbc;

import java.sql.SQLException;

import com.pinecone.framework.system.Nullable;
import com.pinecone.slime.jelly.source.ds.dao.InvalidDataAccessResourceUsageException;

public class InvalidResultSetAccessException extends InvalidDataAccessResourceUsageException {
    @Nullable
    private final String sql;

    public InvalidResultSetAccessException(String task, String sql, SQLException ex) {
        super( task + "; invalid ResultSet access for SQL [" + sql + "]", ex );
        this.sql = sql;
    }

    public InvalidResultSetAccessException(SQLException ex) {
        super( ex.getMessage(), ex );
        this.sql = null;
    }

    public SQLException getSQLException() {
        return (SQLException)this.getCause();
    }

    @Nullable
    public String getSql() {
        return this.sql;
    }
}
