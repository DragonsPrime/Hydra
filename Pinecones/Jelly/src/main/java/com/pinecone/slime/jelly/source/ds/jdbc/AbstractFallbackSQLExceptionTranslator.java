package com.pinecone.slime.jelly.source.ds.jdbc;

import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pinecone.framework.system.Nullable;
import com.pinecone.framework.util.Assert;
import com.pinecone.slime.jelly.source.ds.dao.DataAccessException;


public abstract class AbstractFallbackSQLExceptionTranslator implements SQLExceptionTranslator {
    protected final Log logger = LogFactory.getLog(this.getClass());

    @Nullable
    private SQLExceptionTranslator fallbackTranslator;

    public AbstractFallbackSQLExceptionTranslator() {
    }

    public void setFallbackTranslator(@Nullable SQLExceptionTranslator fallback) {
        this.fallbackTranslator = fallback;
    }

    @Nullable
    public SQLExceptionTranslator getFallbackTranslator() {
        return this.fallbackTranslator;
    }

    @Nullable
    @Override
    public DataAccessException translate(String task, @Nullable String sql, SQLException ex) {
        Assert.notNull(ex, "Cannot translate a null SQLException");
        DataAccessException dae = this.doTranslate(task, sql, ex);
        if (dae != null) {
            return dae;
        }
        else {
            SQLExceptionTranslator fallback = this.getFallbackTranslator();
            return fallback != null ? fallback.translate(task, sql, ex) : null;
        }
    }

    @Nullable
    protected abstract DataAccessException doTranslate(String var1, @Nullable String var2, SQLException var3);

    protected String buildMessage(String task, @Nullable String sql, SQLException ex) {
        return task + "; " + (sql != null ? "SQL [" + sql + "]; " : "") + ex.getMessage();
    }
}
