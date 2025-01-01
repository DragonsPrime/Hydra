package com.pinecone.slime.jelly.source.ds.jdbc;

import java.sql.SQLException;

import com.pinecone.slime.jelly.source.ds.dao.InvalidDataAccessResourceUsageException;

public class BadSqlGrammarException extends InvalidDataAccessResourceUsageException {
    private final String sql;

    public BadSqlGrammarException( String task, String sql, SQLException ex ) {
        super( task + "; bad SQL grammar [" + sql + "]", ex );
        this.sql = sql;
    }

    public SQLException getSQLException() {
        return (SQLException)this.getCause();
    }

    public String getSql() {
        return this.sql;
    }
}
