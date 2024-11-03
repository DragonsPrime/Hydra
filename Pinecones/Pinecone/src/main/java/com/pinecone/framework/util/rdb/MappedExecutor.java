package com.pinecone.framework.util.rdb;

import com.pinecone.framework.util.json.JSONArray;

import java.sql.SQLException;
import java.util.Map;
import java.util.List;

public interface MappedExecutor {
    RDBHost getRDBSQLHost();

    long execute( String szSQL, boolean bIgnoreNoAffected ) throws SQLException;

    ResultSession query( String szSQL ) throws SQLException;


    JSONArray fetch     ( String szSQL ) throws SQLException ;


    long insertWithArray ( String szSimpleTable, Map dataMap ) throws SQLException;

    long updateWithArray ( String szSimpleTable, Map dataMap, List<Map.Entry> conditionMap, String szConditionGlue ) throws SQLException;

    long updateWithArray ( String szSimpleTable, Map dataMap, Map conditionMap, String szConditionGlue ) throws SQLException;



    long deleteWithArray (String szSimpleTable, List<Map.Entry> conditionMap, String szConditionGlue ) throws SQLException;

    long deleteWithArray ( String szSimpleTable, Map conditionMap,  String szConditionGlue ) throws SQLException;
}
