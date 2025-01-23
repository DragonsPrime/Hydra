package com.pinecone.hydra.entity.ibatis.hydranium;

import java.util.List;
import java.util.Map;

import com.pinecone.framework.system.ProxyProvokeHandleException;
import com.pinecone.framework.system.construction.UnifyStructureInjector;
import com.pinecone.framework.system.executum.Processum;
import com.pinecone.framework.system.homotype.StereotypicInjector;
import com.pinecone.hydra.entity.ibatis.GUID72TypeHandler;
import com.pinecone.hydra.entity.ibatis.GUIDTypeHandler;
import com.pinecone.hydra.entity.ibatis.UOITypeHandler;
import com.pinecone.hydra.system.Hydrarum;
import com.pinecone.hydra.system.component.ResourceDispenserCenter;
import com.pinecone.hydra.system.ko.driver.KOIMappingDriver;
import com.pinecone.slime.jelly.source.ibatis.ProxySessionMapperPool;
import com.pinecone.slime.jelly.source.ibatis.IbatisClient;

public abstract class ArchMappingDriver implements KOIMappingDriver {
    protected Hydrarum             mSystem;

    protected Processum            mSuperiorProcess;

    protected IbatisClient         mIbatisClient;

    //protected SqlSession           mSqlSession;

    protected List<Class<? > >     mMapperCandidates;

    protected ResourceDispenserCenter mResourceDispenserCenter;

    public ArchMappingDriver( Processum superiorProcess ) {
        this.mSuperiorProcess                 = superiorProcess;
        if ( this.mSuperiorProcess instanceof Hydrarum ) {
            this.mSystem                      = (Hydrarum) this.mSuperiorProcess;
        }
        else {
            this.mSystem                      = (Hydrarum) superiorProcess.getSystem();
        }
    }

    // Temp , TODO
    public ArchMappingDriver( Processum superiorProcess, IbatisClient ibatisClient, ResourceDispenserCenter dispenserCenter, String szPackageName ) {
        this( superiorProcess );

        this.mIbatisClient = ibatisClient;
        //this.mSqlSession   = ibatisClient.openSession( true );

        //SqlSessionTemplate

        ibatisClient.getConfiguration().getTypeHandlerRegistry().register( GUID72TypeHandler.class );
        ibatisClient.getConfiguration().getTypeHandlerRegistry().register( GUIDTypeHandler.class );
        ibatisClient.getConfiguration().getTypeHandlerRegistry().register( UOITypeHandler.class );

        this.mMapperCandidates = ibatisClient.addDataAccessObjectScope( szPackageName );

        for( Class<? > mapperClass : this.mMapperCandidates ) {
            dispenserCenter.getInstanceDispenser().register(
                    mapperClass,
                    //new SoloSessionMapperPool( this.mSqlSession, mapperClass )
                    new ProxySessionMapperPool( ibatisClient, mapperClass )
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

    @Override
    public Processum getSuperiorProcess() {
        return this.mSuperiorProcess;
    }
}
