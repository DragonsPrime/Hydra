package com.pinecone.framework.util.sqlite;

import com.pinecone.framework.util.Debug;
import com.pinecone.framework.util.rdb.RDBHost;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteHost implements RDBHost {
    protected String      mszLocation;

    protected String      mszUsername;

    protected String      mszPassword;

    protected String      mszCharset;

    protected String      mszDriver;

    protected Connection  mGlobalConnection;


    public SQLiteHost( String dbLocation, String dbUsername, String dbPassword ) throws SQLException {
        this( dbLocation, dbUsername, dbPassword, "UTF-8" );
    }

    public SQLiteHost( String dbLocation, String dbUsername, String dbPassword, String dbCharset ) throws SQLException {
        this( dbLocation, dbUsername, dbPassword, dbCharset, "org.sqlite.JDBC" );
    }

    public SQLiteHost( String dbLocation, String dbUsername, String dbPassword, String dbCharset, String driver ) throws SQLException {
        this.mszLocation = dbLocation ;
        this.mszUsername = dbUsername ;
        this.mszPassword = dbPassword ;
        this.mszCharset  = dbCharset  ;
        this.mszDriver   = driver     ;
        this.connect();
    }

    public SQLiteHost( String dbLocation ) throws SQLException {
        this.mszLocation = dbLocation;
        this.mszDriver   = "org.sqlite.JDBC";
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
            Class.forName( this.mszDriver );
        }
        catch ( ClassNotFoundException e ){
            throw new SQLException( "JDBC Driver is not found.", "CLASS_NOT_FOUND", e );
        }
        String url = "jdbc:sqlite:" + this.mszLocation;
        this.mGlobalConnection = DriverManager.getConnection( url );
    }

    @Override
    public void close() throws SQLException {
        Debug.trace("关闭");
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
