package com.pinecone.slime.jelly.source.ds.jdbc;

import java.sql.SQLException;

import com.pinecone.framework.system.Nullable;
import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.slime.jelly.source.ds.dao.DataAccessException;

@FunctionalInterface
public interface SQLExceptionTranslator extends Pinenut {
    @Nullable
    DataAccessException translate(String task, @Nullable String sql, SQLException e );
}
