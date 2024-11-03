package com.pinecone.framework.util.rdb;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.pinecone.framework.util.json.JSONArray;
import com.pinecone.framework.util.json.JSONArraytron;
import com.pinecone.framework.util.json.JSONMaptron;

public abstract class ArchRDBExecutor implements MappedExecutor {
    private RDBHost          mRDBSQLHost;

    private MappedSQLSplicer mSimpleSQLSpawner = null ;

    private void init() {
        this.mSimpleSQLSpawner = new MappedSQLSplicer();
    }


    @Override
    public RDBHost getRDBSQLHost() {
        this.init();
        return this.mRDBSQLHost;
    }

    public ArchRDBExecutor( RDBHost rdbHost ) {
        this.init();
        this.mRDBSQLHost = rdbHost;
    }

    protected Statement createStatement() throws SQLException {
        return this.mRDBSQLHost.createStatement();
    }

    @Override
    public ResultSession query( String szSQL ) throws SQLException {
        //this.affirmCurrentStatement();
        Statement statement = this.createStatement();
        ResultSet resultSet = statement.executeQuery( szSQL );
        return new DirectResultSession( this.mRDBSQLHost, statement, resultSet );
    }

    @Override
    public long execute( String szSQL, boolean bIgnoreNoAffected ) throws SQLException {
        //this.affirmCurrentStatement();
        Statement statement = this.createStatement();
        statement.execute( szSQL );
        if( bIgnoreNoAffected ){
            return 1;
        }
        long n = statement.getUpdateCount();
        statement.close();
        return n;
    }

    public long execute( String szSQL ) throws SQLException {
        return this.execute( szSQL, false );
    }

    public int countFromTable( String szSQL ){
        try{
            ResultSession session = this.query(szSQL);
            ResultSet resultSet   = session.getResultSet();

            resultSet.next();
            int n = resultSet.getInt("COUNT(*)");
            session.close();
            return n;
        }
        catch ( Exception E ){
            return 0;
        }
    }

    public int getSumFromTable( String szTableName ){
        return this.countFromTable( "SELECT COUNT(*) FROM `" + szTableName + "`" );
    }



    /** Fetch Function **/
    public String[] fetchAllColumn ( String szTable ) throws SQLException {
        String szSQL = "SHOW COLUMNS FROM `" + szTable + "`";
        ResultSession session = this.query( szSQL );
        ResultSet resultSet   = session.getResultSet();

        resultSet.last();
        int nRow = resultSet.getRow();
        resultSet.beforeFirst();
        String[] columns = new String[ nRow ];

        int j = 0;
        while ( resultSet.next() ){
            columns[ j++ ] = resultSet.getString( 1 );
        }
        session.close();
        return columns;
    }

    public static String[] column2Array( ResultSet resultSet )throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int nColumnCount = metaData.getColumnCount();
        String[] columns = new String[nColumnCount];

        for ( int i = 1, j = 0; i <= nColumnCount; i++ ) {
            columns[j++] = metaData.getColumnLabel(i);
        }
        return columns;
    }

    public List<Map<String, Object >> fetchAssoc(String szSQL ) throws SQLException {
        ResultSession session = this.query( szSQL );
        ResultSet resultSet   = session.getResultSet();

        ResultSetMetaData metaData = resultSet.getMetaData();
        int sizeofRowSet = metaData.getColumnCount();
        ArrayList<Map<String,Object > > queryResult = new ArrayList<>();

        int jc = 0;
        while ( resultSet.next() ){
            queryResult.add( new LinkedHashMap<>() );
            for ( int i = 1; i <= sizeofRowSet; i++ ) {
                queryResult.get(jc).put(
                        metaData.getColumnLabel( i ), resultSet.getObject( i )
                );
            }
            jc++;
        }

        session.close();
        return queryResult;
    }

    @Override
    public JSONArray fetch     (String szSQL ) throws SQLException {
        ResultSession session = this.query( szSQL );
        ResultSet resultSet   = session.getResultSet();

        ResultSetMetaData metaData = resultSet.getMetaData();
        int sizeofRowSet = metaData.getColumnCount();
        JSONArray queryResult = new JSONArraytron();

        int jc = 0;
        while ( resultSet.next() ){
            queryResult.put( new JSONMaptron() );
            for ( int i = 1; i <= sizeofRowSet; i++ ) {
                queryResult.getJSONObject( jc ).put(
                        metaData.getColumnLabel( i ), resultSet.getObject( i )
                );
            }
            jc++;
        }

        session.close();
        return queryResult;
    }

    /**
     *  Using java class to store query result if these data operated particular frequently (> 1e6)
     *  According to trail result, if calculation scale beyond (1e6) there is a significant
     *  performance gap between java native object and the HashMap based com.pinecone::JSONObject.
     *  *****************************************************************************************
     *  Experiment At [Intel(R) Core(TM) i7-9750H CPU @ 2.60GHz (Single Thread)]:
     *  Trail At 1e6 : HashMap [11ms], Java Native Object [<10ms] the difference is tiny.
     *  Trail At 1e7 : HashMap [~100ms], Java Native Object [10ms ~ 20ms] the difference is huge but still acceptable.
     *  Trail At 1e8 : HashMap [>1000ms], Java Native Object [80ms ~ 150ms] the difference is huge but unacceptable.
     *  *****************************************************************************************
     *  JSONObject Mode was recommended to be used in temporary query object or normal condition.
     *  NativeObject Mode was recommended to be used in the query result will be manipulated frequently.
     */
//    public Object selectJavaify ( String szSQL, Class<?> antetype ) throws SQLException {
//
//    }


    /** Insert Function **/
    public long insertWithArray ( String szSimpleTable, Map dataMap, boolean bReplace ) throws SQLException {
        if ( dataMap != null ) {
            return this.execute( this.mSimpleSQLSpawner.spliceInsertSQL( szSimpleTable, dataMap, bReplace ) );
        }
        return -1;
    }

    @Override
    public long insertWithArray ( String szSimpleTable, Map dataMap ) throws SQLException {
        return insertWithArray(szSimpleTable,dataMap,false);
    }



    /** Update Function **/
    @Override
    public long updateWithArray ( String szSimpleTable, Map dataMap, List<Map.Entry> conditionMap, String szConditionGlue ) throws SQLException {
        if ( dataMap != null ) {
            return this.execute(
                    this.mSimpleSQLSpawner.spliceUpdateSQL (
                            szSimpleTable,
                            dataMap,
                            conditionMap,
                            szConditionGlue
                    ),
                    true
            );
        }
        return -1;
    }

    public long updateWithArray ( String szSimpleTable, Map dataMap, List<Map.Entry> conditionMap ) throws SQLException {
        return this.updateWithArray( szSimpleTable, dataMap, conditionMap, "AND" );
    }

    @Override
    public long updateWithArray ( String szSimpleTable, Map dataMap, Map conditionMap, String szConditionGlue ) throws SQLException {
        if ( dataMap != null ) {
            return this.execute(
                    this.mSimpleSQLSpawner.spliceUpdateSQL (
                            szSimpleTable,
                            dataMap,
                            conditionMap,
                            szConditionGlue
                    ),
                    true
            );
        }
        return -1;
    }

    public long updateWithArray ( String szSimpleTable, Map dataMap, Map conditionMap ) throws SQLException {
        return this.updateWithArray( szSimpleTable, dataMap, conditionMap, "AND" );
    }

    public long updateWithArray ( String szSimpleTable, Map dataMap, String szConditionSQL ) throws SQLException {
        if ( dataMap != null ) {
            StringBuilder sqlStream = new StringBuilder();
            sqlStream.append( this.mSimpleSQLSpawner.spliceNoConditionUpdateSQL( szSimpleTable,dataMap ) );

            if ( szConditionSQL!= null ) {
                if( !szConditionSQL.toLowerCase().contains("where")){
                    sqlStream.append(" WHERE ");
                }
                sqlStream.append( szConditionSQL );
            }
            return this.execute( sqlStream.toString(), true );
        }
        return -1;
    }

    public long updateWithArray ( String szSimpleTable, Map dataMap ) throws SQLException {
        return updateWithArray( szSimpleTable, dataMap, (Map) null, "AND" );
    }



    /** Delete Function **/
    @Override
    public long deleteWithArray ( String szSimpleTable, List<Map.Entry> conditionMap,  String szConditionGlue ) throws SQLException {
        if ( conditionMap != null ) {
            return this.execute( this.mSimpleSQLSpawner.spliceDeleteSQL( szSimpleTable, conditionMap, szConditionGlue ) );
        }
        return this.execute("TRUNCATE `" + szSimpleTable + '`');
    }

    @Override
    public long deleteWithArray ( String szSimpleTable, Map conditionMap,  String szConditionGlue ) throws SQLException {
        if ( conditionMap != null ) {
            return this.execute( this.mSimpleSQLSpawner.spliceDeleteSQL( szSimpleTable, conditionMap, szConditionGlue ) );
        }
        return this.execute("TRUNCATE  `" + szSimpleTable + '`');
    }

    public long deleteWithArray ( String szSimpleTable, List<Map.Entry> conditionMap ) throws SQLException {
        return this.deleteWithArray( szSimpleTable,conditionMap,"AND" );
    }

    public long deleteWithArray ( String szSimpleTable, Map conditionMap ) throws SQLException {
        return this.deleteWithArray( szSimpleTable,conditionMap,"AND" );
    }

    public long deleteWithSQL   ( String szSimpleTable, String szConditionSQL ) throws SQLException {
        StringBuilder sqlStream = new StringBuilder();
        sqlStream .append( "DELETE FROM `" ).append( szSimpleTable ).append( "`" );
        if ( szConditionSQL!= null ) {
            if( !szConditionSQL.toLowerCase().contains("where")){
                sqlStream.append(" WHERE ");
            }
            sqlStream.append( szConditionSQL );
        }
        return this.execute( sqlStream.toString() );
    }
}