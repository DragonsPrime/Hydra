package com.pinecone.slime.source.rdb;

import com.pinecone.framework.util.json.homotype.MapStructure;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ArchRelationalDatabase implements RelationalDatabase {
    @MapStructure( "host" )
    protected String  mHost;

    @MapStructure( "username" )
    protected String  mUsername;

    @MapStructure( "password" )
    protected String  mPassword;

    @MapStructure( "database" )
    protected String  mDatabase;

    @MapStructure( "port" )
    protected int     mPort;

    @MapStructure( "charset" )
    protected String  mCharset = "utf8";

    @MapStructure( "tablePrefix" )
    protected String  mTablePrefix;

    @MapStructure( "dbType" )
    protected String  mDBType;

    @MapStructure( "Enable" )
    protected boolean mEnable = true;


    @Override
    public String getHost() {
        return this.mHost;
    }

    @Override
    public void setHost( String host ) {
        this.mHost = host;
    }

    @Override
    public String getUsername() {
        return this.mUsername;
    }

    @Override
    public void setUsername( String username ) {
        this.mUsername = username;
    }

    @Override
    public String getPassword() {
        return this.mPassword;
    }

    @Override
    public void setPassword( String password ) {
        this.mPassword = password;
    }

    @Override
    public String getDatabase() {
        return this.mDatabase;
    }

    @Override
    public void setDatabase( String database ) {
        this.mDatabase = database;
    }

    @Override
    public int getPort() {
        return this.mPort;
    }

    @Override
    public void setPort( int port ) {
        this.mPort = port;
    }

    @Override
    public String getCharset() {
        return this.mCharset;
    }

    @Override
    public void setCharset( String charset ) {
        this.mCharset = charset;
    }

    @Override
    public String getTablePrefix() {
        return this.mTablePrefix;
    }

    @Override
    public void setTablePrefix( String tablePrefix ) {
        this.mTablePrefix = tablePrefix;
    }

    @Override
    public boolean isEnabled() {
        return this.mEnable;
    }

    @Override
    public void setEnabled( boolean enabled ) {
        this.mEnable = enabled;
    }

    @Override
    public String getDBType() {
        return this.mDBType;
    }

    @Override
    public void setDBType( String dbType ) {
        this.mDBType = dbType;
    }


    @Override
    public String toJDBCURL() {
        String url = "jdbc:" + this.mDBType + "://" + this.mHost + ":" + this.mPort + "/" + this.mDatabase;
        if( this.mCharset.toLowerCase().startsWith( "utf" ) )  { // utf-8, utf8, etc...
            url = url +"?useUnicode=true&characterEncoding=" + this.mCharset;
        }
        else {
            url = url +"?characterEncoding=" + this.mCharset;
        }

        return url;
    }

    @Override
    public void fromJDBCURL( String jdbcUrl ) {
        Pattern pattern = Pattern.compile( "jdbc:(\\w+):\\/\\/(.+):(\\d+)\\/(.+)\\?useUnicode=true&characterEncoding=(\\w+)" );
        Matcher matcher = pattern.matcher( jdbcUrl );
        boolean bMatched = false;
        if ( matcher.matches() ) {
            bMatched = true;
        }
        else {
            pattern = Pattern.compile( "jdbc:(\\w+):\\/\\/(.+):(\\d+)\\/(.+)\\?characterEncoding=(\\w+)" );
            matcher = pattern.matcher( jdbcUrl );
            bMatched = matcher.matches();
        }

        if ( bMatched ) {
            this.mDBType   = matcher.group(1);
            this.mHost     = matcher.group(2);
            this.mPort     = Integer.parseInt(matcher.group(3));
            this.mDatabase = matcher.group(4);
            this.mCharset  = matcher.group(5);
        }
        else {
            throw new IllegalArgumentException( "Invalid JDBC URL format: " + jdbcUrl );
        }
    }
}