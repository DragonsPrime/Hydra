package com.pinecone.framework.util.rdb.sqlite;

import com.pinecone.framework.util.Debug;
import com.pinecone.framework.util.rdb.RDBHost;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SqliteHost implements RDBHost {
    private String      mszLocation;

    private String      mszUsername;

    private String      mszPassword;

    private String      mszCharset;

    private Connection mGlobalConnection;

    public SqliteHost( String dbLocation, String dbUsername, String dbPassword ) throws SQLException {
        this.mszLocation = dbLocation;
        this.mszUsername = dbUsername;
        this.mszPassword = dbPassword;
        this.mszCharset = "UTF-8";
        this.connect();
    }

    public SqliteHost( String dbLocation, String dbUsername, String dbPassword, String dbCharset ) throws SQLException {
        this.mszLocation = dbLocation;
        this.mszUsername = dbUsername;
        this.mszPassword = dbPassword;
        this.mszCharset = dbCharset;
        this.connect();
    }

    public SqliteHost( String dbLocation ) throws SQLException {
        this.mszLocation = dbLocation;
        this.connect();
    }

    @Override
    public boolean isClosed() {
        if( this.mGlobalConnection == null ) {
            return true;
        }

        try {
            return this.mGlobalConnection.isClosed();
        }
        catch ( SQLException e ) {
            Debug.cerr( e );
            return false;
        }
    }

    @Override
    public void connect() throws SQLException {
        try{
            Class.forName("org.sqlite.JDBC");
        }
        catch ( ClassNotFoundException e ){
            throw new SQLException( "JDBC Driver is not found.", "CLASS_NOT_FOUND", e );
        }
        String url = "jdbc:sqlite:" + this.mszLocation;
        this.mGlobalConnection = DriverManager.getConnection(url);
    }

    @Override
    public void close() throws SQLException {
        if( this.mGlobalConnection != null ) {
            this.mGlobalConnection.close();
        }
    }

    @Override
    public Connection getConnection() {
        return this.mGlobalConnection;
    }

    @Override
    public Statement createStatement() throws SQLException {
        if( this.isClosed() ){
            this.connect();
        }

        return this.mGlobalConnection.createStatement();
    }
}
