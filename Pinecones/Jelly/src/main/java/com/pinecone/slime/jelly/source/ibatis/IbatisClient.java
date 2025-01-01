package com.pinecone.slime.jelly.source.ibatis;

import com.pinecone.framework.util.json.JSONObject;
import com.pinecone.slime.source.DAOScanner;
import com.pinecone.slime.source.rdb.RDBClient;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.TransactionIsolationLevel;

import javax.sql.DataSource;
import java.sql.Connection;

public interface IbatisClient extends RDBClient {
    Configuration     getConfiguration();

    DataSource        getDataSource();

    Environment       getEnvironment();

    JSONObject        getIbatisConf();

    String            getJDBCDriverName();

    JSONObject        getClientConf();

    DAOScanner        getDAOScanner();

    <T> void addMapper( Class<T> type ) ;



    SqlSessionFactory getSqlSessionFactory();

    SqlSession        openSession();

    SqlSession        openSession( boolean autoCommit );

    SqlSession        openSession( Connection connection);

    SqlSession        openSession( TransactionIsolationLevel level );

    SqlSession        openSession( ExecutorType execType );

    SqlSession        openSession( ExecutorType execType, boolean autoCommit );

    SqlSession        openSession( ExecutorType execType, TransactionIsolationLevel level );

    SqlSession        openSession( ExecutorType execType, Connection connection );

    void              free( SqlSession sqlSession );

    int               sqlSessionSize();
}
