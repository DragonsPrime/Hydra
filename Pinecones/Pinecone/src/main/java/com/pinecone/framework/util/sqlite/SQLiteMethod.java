package com.pinecone.framework.util.sqlite;

import com.pinecone.framework.system.prototype.Pinenut;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class SQLiteMethod implements Pinenut {
    private SQLiteHost sqliteHost;
    private   Statement    statement;

    public SQLiteMethod(SQLiteHost sqliteHost ) throws SQLException {
        this.sqliteHost = sqliteHost;
        this.statement  = sqliteHost.createStatement();
    }

    public Map< String, Object > executeQuery(String sql ) throws SQLException {
        HashMap<String, Object> map = new HashMap<>();
        ResultSet resultSet = this.statement.executeQuery(sql);

        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            map.put( metaData.getColumnName( i ), resultSet.getString( i ) );
        }

        return map;
    }

    public int executeUpdate( String sql ) throws SQLException {
        return this.statement.executeUpdate( sql );
    }
}
