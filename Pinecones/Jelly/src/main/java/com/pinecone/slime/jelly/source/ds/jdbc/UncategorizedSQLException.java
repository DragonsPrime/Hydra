package com.pinecone.slime.jelly.source.ds.jdbc;

import java.sql.SQLException;

import com.pinecone.framework.system.Nullable;

public class UncategorizedSQLException extends UncategorizedDataAccessException {
    @Nullable
    private final String sql;

    public UncategorizedSQLException( String task, @Nullable String sql, SQLException ex ) {
        super(task + "; uncategorized SQLException" + (sql != null ? " for SQL [" + sql + "]" : "") + "; SQL state [" + ex.getSQLState() + "]; error code [" + ex.getErrorCode() + "]; " + ex.getMessage(), ex);
        this.sql = sql;
    }

    public SQLException getSQLException() {
        return (SQLException)this.getCause();
    }

    @Nullable
    public String getSql() {
        return this.sql;
    }
}
