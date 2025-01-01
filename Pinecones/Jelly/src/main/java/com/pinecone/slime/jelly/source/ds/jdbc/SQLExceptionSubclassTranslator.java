package com.pinecone.slime.jelly.source.ds.jdbc;

import java.sql.SQLDataException;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLInvalidAuthorizationSpecException;
import java.sql.SQLNonTransientConnectionException;
import java.sql.SQLNonTransientException;
import java.sql.SQLRecoverableException;
import java.sql.SQLSyntaxErrorException;
import java.sql.SQLTimeoutException;
import java.sql.SQLTransactionRollbackException;
import java.sql.SQLTransientConnectionException;
import java.sql.SQLTransientException;

import com.pinecone.slime.jelly.source.ds.dao.ConcurrencyFailureException;
import com.pinecone.slime.jelly.source.ds.dao.DataAccessException;
import com.pinecone.slime.jelly.source.ds.dao.DataAccessResourceFailureException;
import com.pinecone.slime.jelly.source.ds.dao.DataIntegrityViolationException;
import com.pinecone.slime.jelly.source.ds.dao.InvalidDataAccessApiUsageException;
import com.pinecone.slime.jelly.source.ds.dao.PermissionDeniedDataAccessException;
import com.pinecone.slime.jelly.source.ds.dao.QueryTimeoutException;
import com.pinecone.slime.jelly.source.ds.dao.RecoverableDataAccessException;
import com.pinecone.slime.jelly.source.ds.dao.TransientDataAccessResourceException;
import com.pinecone.framework.system.Nullable;


public class SQLExceptionSubclassTranslator extends AbstractFallbackSQLExceptionTranslator {
    public SQLExceptionSubclassTranslator() {
        this.setFallbackTranslator( new SQLStateSQLExceptionTranslator() );
    }

    @Nullable
    @Override
    protected DataAccessException doTranslate(String task, @Nullable String sql, SQLException ex) {
        if (ex instanceof SQLTransientException) {
            if (ex instanceof SQLTransientConnectionException) {
                return new TransientDataAccessResourceException(this.buildMessage(task, sql, ex), ex);
            }

            if (ex instanceof SQLTransactionRollbackException) {
                return new ConcurrencyFailureException(this.buildMessage(task, sql, ex), ex);
            }

            if (ex instanceof SQLTimeoutException) {
                return new QueryTimeoutException(this.buildMessage(task, sql, ex), ex);
            }
        }
        else if (ex instanceof SQLNonTransientException) {
            if (ex instanceof SQLNonTransientConnectionException) {
                return new DataAccessResourceFailureException(this.buildMessage(task, sql, ex), ex);
            }

            if (ex instanceof SQLDataException) {
                return new DataIntegrityViolationException(this.buildMessage(task, sql, ex), ex);
            }

            if (ex instanceof SQLIntegrityConstraintViolationException) {
                return new DataIntegrityViolationException(this.buildMessage(task, sql, ex), ex);
            }

            if (ex instanceof SQLInvalidAuthorizationSpecException) {
                return new PermissionDeniedDataAccessException(this.buildMessage(task, sql, ex), ex);
            }

            if (ex instanceof SQLSyntaxErrorException) {
                return new BadSqlGrammarException(task, sql != null ? sql : "", ex);
            }

            if (ex instanceof SQLFeatureNotSupportedException) {
                return new InvalidDataAccessApiUsageException(this.buildMessage(task, sql, ex), ex);
            }
        }
        else if (ex instanceof SQLRecoverableException) {
            return new RecoverableDataAccessException(this.buildMessage(task, sql, ex), ex);
        }

        return null;
    }
}
