package com.pinecone.slime.jelly.source.ibatis;

import org.apache.ibatis.session.ExecutorType;

import com.pinecone.framework.system.construction.InstancePool;
import com.pinecone.slime.jelly.source.ibatis.proxy.SqlSessionTemplate;

public class ProxySessionMapperPool implements InstancePool {
    protected IbatisClient         mIbatisClient;
    protected Class<? >            mType;
    protected SqlSessionTemplate   mSqlSessionTemplate;

    protected ProxySessionMapperPool( IbatisClient ibatisClient, Class<? > type, Void dummy ) {
        this.mIbatisClient       = ibatisClient;
        this.mType               = type;
    }

    public ProxySessionMapperPool( IbatisClient ibatisClient, Class<? > type, ExecutorType executorType ) {
        this( ibatisClient, type, (Void) null );
        this.mSqlSessionTemplate = new SqlSessionTemplate( ibatisClient.getSqlSessionFactory(), executorType );
    }

    public ProxySessionMapperPool( IbatisClient ibatisClient, Class<? > type ) {
        this( ibatisClient, type, (Void) null );
        this.mSqlSessionTemplate = new SqlSessionTemplate( ibatisClient.getSqlSessionFactory() );
    }

    @Override
    public Object allocate() {
        return this.mSqlSessionTemplate.getMapper ( this.mType );
    }

    @Override
    public void free( Object obj ) {

    }

    @Override
    public int freeSize() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int pooledSize() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void preAllocate( int count ) {
    }

    @Override
    public void setCapacity( int capacity ) {
    }

    @Override
    public int getCapacity() {
        return 0;
    }
}
