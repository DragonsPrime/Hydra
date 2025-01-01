package com.pinecone.framework.util.rdb;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public interface ResultSession {
    RDBHost getHost();

    default Connection getConnection()  {
        return this.getHost().getConnection();
    }

    Statement getStatement();

    ResultSet getResultSet();

    void close() throws SQLException;
}
