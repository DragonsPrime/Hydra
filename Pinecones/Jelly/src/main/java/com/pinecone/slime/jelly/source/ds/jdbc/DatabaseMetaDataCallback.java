package com.pinecone.slime.jelly.source.ds.jdbc;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import com.pinecone.framework.system.prototype.Pinenut;

@FunctionalInterface
public interface DatabaseMetaDataCallback<T> extends Pinenut {
    T processMetaData( DatabaseMetaData metaData ) throws SQLException, MetaDataAccessException;
}
