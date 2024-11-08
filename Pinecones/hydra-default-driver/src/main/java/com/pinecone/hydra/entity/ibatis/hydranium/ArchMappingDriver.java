package com.pinecone.hydra.entity.ibatis.hydranium;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.LocalCacheScope;
import org.apache.ibatis.session.SqlSession;

import com.pinecone.framework.system.ProxyProvokeHandleException;
import com.pinecone.framework.system.construction.UnifyStructureInjector;
import com.pinecone.framework.system.homotype.StereotypicInjector;
import com.pinecone.hydra.entity.ibatis.GUID72TypeHandler;
import com.pinecone.hydra.entity.ibatis.GUIDTypeHandler;
import com.pinecone.hydra.entity.ibatis.UOITypeHandler;
import com.pinecone.hydra.system.Hydrarum;
import com.pinecone.hydra.system.component.ResourceDispenserCenter;
import com.pinecone.hydra.system.ko.driver.KOIMappingDriver;
import com.pinecone.slime.jelly.source.ibatis.AppliedMapperPool;
import com.pinecone.slime.jelly.source.ibatis.IbatisClient;

public abstract class ArchMappingDriver implements KOIMappingDriver {
    protected Hydrarum             mSystem;

    protected IbatisClient         mIbatisClient;

    protected SqlSession           mSqlSession;

    protected List<Class<? > >     mMapperCandidates;

    protected ResourceDispenserCenter mResourceDispenserCenter;

    public ArchMappingDriver( Hydrarum system ) {
        this.mSystem = system;
    }

    // Temp , TODO
    public ArchMappingDriver( Hydrarum system, IbatisClient ibatisClient, ResourceDispenserCenter dispenserCenter, String szPackageName ) {
        this.mSystem       = system;
        this.mIbatisClient = ibatisClient;
        this.mSqlSession   = ibatisClient.openSession( true );

        ibatisClient.getConfiguration().getTypeHandlerRegistry().register( GUID72TypeHandler.class );
        ibatisClient.getConfiguration().getTypeHandlerRegistry().register( GUIDTypeHandler.class );
        ibatisClient.getConfiguration().getTypeHandlerRegistry().register( UOITypeHandler.class );

        this.mMapperCandidates = ibatisClient.addDataAccessObjectScope( szPackageName );

        for( Class<? > mapperClass : this.mMapperCandidates ) {
            dispenserCenter.getInstanceDispenser().register(
                    mapperClass,
                    new AppliedMapperPool( this.mSqlSession, mapperClass )
            );
        }

        this.mResourceDispenserCenter = dispenserCenter;
    }

    @Override
    public StereotypicInjector autoConstruct( Class<?> stereotype, Map config, Object instance ) {
        UnifyStructureInjector injector = new UnifyStructureInjector( stereotype, this.mResourceDispenserCenter.getInstanceDispenser() );
        try{
            injector.inject( config, instance );
        }
        catch ( Exception e ){
            throw new ProxyProvokeHandleException( e );
        }
        return injector;
    }

    @Override
    public String getVersionSignature() {
        return "HydraniumV2.1";
    }

    @Override
    public Hydrarum getSystem() {
        return this.mSystem;
    }
}
