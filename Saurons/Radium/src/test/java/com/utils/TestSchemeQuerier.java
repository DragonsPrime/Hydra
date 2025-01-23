package com.utils;

import com.pinecone.Pinecone;
import com.pinecone.framework.unit.ListDictium;
import com.pinecone.framework.unit.MapDictium;
import com.pinecone.framework.util.Debug;
import com.pinecone.framework.util.Randomium;
import com.pinecone.framework.util.json.*;
import com.pinecone.slime.cache.query.LocalDictCachePage;
import com.pinecone.slime.cache.query.LocalFixedLRUDictCachePage;
import com.pinecone.slime.cache.query.pool.CountSelfPooledPageDictCache;
import com.pinecone.slime.cache.query.pool.LocalHotspotPooledDictCache;
import com.pinecone.slime.cache.query.pool.LocalLRUPrimaryPooledDictCache;
import com.pinecone.slime.jelly.source.ibatis.GenericMybatisQuerierDataManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisManipulatorProxyMapperFactory;
import com.pinecone.slime.jelly.source.memcached.GenericMemcachedManipulator;
import com.pinecone.slime.jelly.source.redis.GenericRedisHashManipulator;
import com.pinecone.slime.jelly.source.redis.GenericRedisMasterManipulator;
import com.pinecone.slime.map.LocalMapQuerier;

import java.net.InetSocketAddress;
import java.util.*;

import com.pinecone.slime.map.indexable.IndexableMapQuerier;
import com.pinecone.slime.map.rdb.RDBMapQuerier;
import com.pinecone.slime.source.indexable.*;
import com.pinecone.slime.source.rdb.*;
import net.spy.memcached.MemcachedClient;
import org.apache.ibatis.session.*;


import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.ibatis.mapping.Environment;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

class DataEntity {
    private Object id;
    private Object value;

    public Object getId() {
        return this.id;
    }

    public void setId( Object key ) {
        this.id = key;
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue( Object value ) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "{" + this.id + ":" + this.value + "}";
    }
}

class MyBatisUtil {
    private static SqlSessionFactory sqlSessionFactory;

    static {
        try {
            // Define database connection information
            String driver = "com.mysql.cj.jdbc.Driver";
            String url = "jdbc:mysql://localhost:3306/pinecone";
            String username = "root";
            String password = "test";

            DataSource dataSource       = new PooledDataSource(driver, url, username, password);
            Environment environment     = new Environment("development", new JdbcTransactionFactory(), dataSource);
            Configuration configuration = new Configuration(environment);


            // Add mappers directly in the configuration
            configuration.addMapper(GenericMybatisQuerierDataManipulator.class);

            sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public static SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }
}










public class TestSchemeQuerier {
    public static void testListDict() throws Exception {
        JSONArray ja = new JSONArraytron( "[1,2,sss,null,false]" );

        ListDictium<Object > listDictium = new ListDictium<>( ja.toList() );

        for( Map.Entry kv : listDictium.entrySet() ) {
            Debug.trace( kv );
        }

        Debug.trace( listDictium.entrySet() );
    }

    public static void testMapDict() throws Exception {
        JSONObject jo = new JSONMaptron( "{ k1:v1, k2:v2, k3:3 }" );

        MapDictium<Object > mapDictium = new MapDictium<>( jo.toMap(), true );

        for( Map.Entry kv : mapDictium.entrySet() ) {
            Debug.trace( kv.getKey(), kv.getValue() );
        }

        Debug.trace( mapDictium.entrySet() );
    }

    public static void testLocalDict() throws Exception {
        JSONObject jo = new JSONMaptron( "{ k1:v1, k2:v2, k3:3 }" );

        LocalMapQuerier<Object > querier = new LocalMapQuerier<>( jo );

        for( Object kv : querier.entrySet() ) {
            Debug.trace( kv );
        }

        Debug.trace( querier );

        querier = new LocalMapQuerier<>( true );
        querier.insert( 0, 111 );
        querier.insert( 1, 211 );
        querier.insert( 2, 311 );
        querier.insert( 3, 311 );

        Debug.trace( querier );

        querier.insert( 8, 811 );
        Debug.trace( querier );

    }

    public static void testLRUDictCache() throws Exception {
        SqlSessionFactory sqlSessionFactory = MyBatisUtil.getSqlSessionFactory();
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            //sqlSession.getConnection().setAutoCommit(true);

            GenericMybatisQuerierDataManipulator manipulator = IbatisManipulatorProxyMapperFactory.getMapper( sqlSession, GenericMybatisQuerierDataManipulator.class );
            RDBTargetTableMeta meta = (
                    new GenericRDBTargetTableMeta("test_table", "id", String.class, manipulator )
            ).addValueMetaKey( "value" );/*.addValueMetaKey( "id" ).addValueMetaKey( "value" )*/

            ContiguousNumIndexBatchPageSourceRetriever<Integer, String > retriever = new ContiguousNumIndexBatchPageSourceRetriever<>( meta, 100, "id" );

            //Debug.trace( retriever.retrieve( 56 ) );

            Debug.trace( ( (LocalDictCachePage) retriever.retrieves( 8561 ) ).getDictium() );

            LocalLRUPrimaryPooledDictCache<Integer, String > cache = new LocalLRUPrimaryPooledDictCache<>( 100, 3, retriever );

            Debug.trace( cache.get( 123 ) );
            Debug.trace( cache.get( 126 ) );
            Debug.trace( cache.get( 128 ) );


//            {username:undefined, role:admin, expired:20250117-12:30:00, xxxx}
            Debug.trace( cache.get( 1995 ) );
            Debug.trace( cache.get( 1915 ) );
            cache.erase( 1915 );

            Debug.trace( cache.get( 1915 ) );
            Debug.trace( cache.get( 2915 ) );

            Debug.trace( cache.get( 3615 ) );
            Debug.trace( cache.get( 3415 ) );


            LocalFixedLRUDictCachePage<String > cachePage = new LocalFixedLRUDictCachePage<>( 3, retriever );
            Debug.trace( cachePage.get( 1995 ) );
            Debug.trace( cachePage.get( 1915 ) );
            cachePage.erase( 1915 );

            Debug.trace( cachePage.get( 1915 ) );
//            for ( int i = 0; i < (int)1e4; ++i ) {
//                Debug.trace( cachePage.get( i ) );
//            }
        }
    }

    public static void testHotspotDictCache() throws Exception {
        SqlSessionFactory sqlSessionFactory = MyBatisUtil.getSqlSessionFactory();
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            //sqlSession.getConnection().setAutoCommit(true);

            GenericMybatisQuerierDataManipulator manipulator = IbatisManipulatorProxyMapperFactory.getMapper( sqlSession, GenericMybatisQuerierDataManipulator.class );
            RDBTargetTableMeta meta = (
                    new GenericRDBTargetTableMeta("test_table", "id", String.class, manipulator )
            ).addValueMetaKey( "value" );/*.addValueMetaKey( "id" ).addValueMetaKey( "value" )*/

            ContiguousNumIndexBatchPageSourceRetriever<Integer, String > retriever = new ContiguousNumIndexBatchPageSourceRetriever<>( meta, 100, "id" );

            //Debug.trace( retriever.retrieve( 56 ) );

            Debug.trace( ( (LocalDictCachePage) retriever.retrieves( 8561 ) ).getDictium() );

            LocalHotspotPooledDictCache<Integer, String > cache = new LocalHotspotPooledDictCache<>( 100, 6, retriever );

            Debug.trace( cache.get( 123 ) );
            Debug.trace( cache.get( 126 ) );
            Debug.trace( cache.get( 128 ) );

            Debug.trace( cache.get( 1995 ) );
            Debug.trace( cache.get( 1915 ) );

            Debug.trace( cache.get( 2915 ) );

            Debug.trace( cache.get( 3414 ) );
            Debug.trace( cache.get( 3415 ) );
            //cache.erase( 3415 );
            //Debug.trace( cache.get( 3415 ) );
            Debug.trace( cache.get( 3416 ) );
            Debug.trace( cache.get( 3417 ) );

            Debug.trace( cache.get( 4915 ) );
            Debug.trace( cache.get( 4916 ) );
            Debug.trace( cache.get( 4917 ) );

            Debug.trace( cache.get( 1917 ) );
            Debug.trace( cache.get( 1918 ) );

            Debug.trace( cache.get( 5917 ) );

            Debug.trace( cache.get( 6917 ) );

            Randomium randomium = Randomium.newInstance();
            int scale = (int)1e4;
            for ( int i = 0; i < scale; ++i ) {
                //Debug.trace( cache.get( i ) );
                Debug.trace( cache.get( (int)randomium.nextBias(0, (int)1e3, 0.4 ) ) );
            }

            Debug.trace( cache.getMisses() );
            Debug.trace( cache.getAccesses() );

        }
    }

    public static void testRDBDict() throws Exception {
        SqlSessionFactory sqlSessionFactory = MyBatisUtil.getSqlSessionFactory();
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            //sqlSession.getConnection().setAutoCommit(true);

            RangedRDBQuerierDataManipulator manipulator = sqlSession.getMapper( GenericMybatisQuerierDataManipulator.class );

            RDBTargetTableMeta meta = (
                    new GenericRDBTargetTableMeta("test_table", "id", String.class, manipulator )
            ).addValueMetaKey( "value" );/*.addValueMetaKey( "id" ).addValueMetaKey( "value" )*/

            CountSelfPooledPageDictCache<String > cache = new LocalLRUPrimaryPooledDictCache<>( 100, 3,
                    new ContiguousNumIndexBatchPageSourceRetriever<>( meta, 100, "id" )
            );
            RDBMapQuerier<Integer, String > querier = new RDBMapQuerier<>( meta, cache );
//            querier.insert(1, "value1");
//            querier.insert(2, "value2");
//            querier.insert(3, "value3");
//            querier.insert(4, "value4");

//            for ( int i = 0; i < (int)1e4; ++i ) {
//                querier.insert(i, "value"+i);
//            }

//            Debug.trace( querier );
//
//            //querier.clear();
//
            Debug.trace(querier.get(1));
            Debug.trace(querier.get(2));
            Debug.trace(querier.get(3));
            sqlSession.commit();

            Debug.trace( querier.values() );

            Debug.trace( querier.isEmpty() );

            Debug.trace( querier.queryVal( "SELECT * FROM test_table WHERE id > 100 AND id < 120" ) );
        }
    }

    public static void testRedisDict() throws Exception {
        //IndexableMapQuerier<String, String > querier = new IndexableMapQuerier<>( "b-serverkingpin", 6397, "", "wolf19310918" );

        //Debug.trace( querier.get( "name" ) );



        JedisPoolConfig poolConfig = new JedisPoolConfig();
        JedisPool jedisPool = new JedisPool( poolConfig, "b-serverkingpin", 6379, 2000, "wolf19310918", 0 );
        Jedis jedis = jedisPool.getResource();
        jedis.auth( "wolf19310918" );
        //IndexableIteratableManipulator<String, String > manipulator = new GenericRedisHashManipulator<>( jedis );
        IndexableIterableManipulator<String, String > manipulator = new GenericRedisMasterManipulator<>( jedis );
        IndexableTargetScopeMeta meta = new GenericIndexableTargetScopeMeta( "1", "test", Object.class, manipulator );

        //manipulator.insert( meta, "hah", "hhhh" );
        //Debug.trace( manipulator.selectByKey( meta, "name" ) );

        //Debug.trace( manipulator.selectByKey( meta, "li" ) );

        //manipulator.insertByNS( meta, "shit", "more", "fuck" );
        //manipulator.insertByNS( meta, "shit", "more", "fuck" );

        jedis.select( 0 );
//        manipulator.insert( meta, "shit1", "vshit1" );
//        manipulator.insert( meta, "crap:shit1", "crap:vshit1" );
//        manipulator.insert( meta, "crap:shit2", "crap:vshit2" );

//        Debug.trace( manipulator.selectAllByNS( meta, null, null ) );






        manipulator = new GenericRedisMasterManipulator<>( jedis );
        meta = new GenericIndexableTargetScopeMeta( "0", "", Object.class, manipulator );
        //IndexableMapQuerier<String, String > querier = new IndexableMapQuerier<>( meta );
        //IndexableMapQuerier<String, String > querier = new IndexableMapQuerier<>( meta, false );
        IndexableMapQuerier<String, String > querier = new IndexableMapQuerier<>( meta, true );

        Debug.trace( querier.get( "test" ) );
        Debug.trace( querier.get( "test" ) );
        Debug.trace( querier.get( "test" ) );

        Debug.trace( querier.containsKey( "li" ) );
        Debug.trace( querier.containsValue( "ssss" ) );
        Map map = querier.toMap();
        Debug.trace( map.entrySet() );




        manipulator = new GenericRedisHashManipulator<>( jedis );
        meta = new GenericIndexableTargetScopeMeta( "0", "student", Object.class, manipulator );
        Iterator iter = manipulator.iterator( meta );
        while ( iter.hasNext() ) {
            Debug.trace( iter.next() );
        }

        querier = new IndexableMapQuerier<>( meta );
        map = querier.toMap();
        Debug.trace( map.entrySet() );
    }

    public static void testMemCachedDict() throws Exception {
        MemcachedClient client = new MemcachedClient( new InetSocketAddress( "b-serverkingpin", 11211 ) );

        // 设置一个键值对
        Future<Boolean> setFuture = client.set("key1", 900, "value1");
        Debug.trace("Set key1: " + setFuture.get(5, TimeUnit.SECONDS));
        Debug.trace(client.get("key1"));


        Debug.trace(client.get("key2"));

        Set<String> allKeys = new HashSet<>();

        IndexableIterableManipulator<String, String > manipulator = new GenericMemcachedManipulator<> ( client );
        IndexableTargetScopeMeta meta = new GenericIndexableTargetScopeMeta( "", "", Object.class, manipulator );
        manipulator.insert( meta, "key2", "val2" );

        Debug.trace( ((GenericMemcachedManipulator<String>) manipulator).keys(), manipulator.counts( meta, "key1" ) );


        IndexableMapQuerier<String, String > querier = new IndexableMapQuerier<>( meta );

        Map map = querier.toMap();
        Debug.trace( map );

        Debug.trace( querier.get( "key2" ) );

        client.shutdown();
    }

    public static void main( String[] args ) throws Exception {
        Pinecone.init( (Object...cfg )->{


            //TestSchemeQuerier.testListDict();
            //TestSchemeQuerier.testMapDict();
            //TestSchemeQuerier.testLocalDict();
            //TestSchemeQuerier.testLRUDictCache();
            //TestSchemeQuerier.testHotspotDictCache();
            //TestSchemeQuerier.testRDBDict();
            TestSchemeQuerier.testRedisDict();
            //TestSchemeQuerier.testMemCachedDict();


            return 0;
        }, (Object[]) args );
    }
}
