package com.pinecone.hydra.storage.volume.operator;

import com.pinecone.hydra.storage.file.entity.GenericFileNode;
import com.pinecone.hydra.storage.file.entity.GenericFolder;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.source.VolumeMasterManipulator;
import com.pinecone.hydra.unit.imperium.operator.TreeNodeOperator;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class TitanVolumeOperatorFactory implements VolumeOperatorFactory{
    protected  VolumeMasterManipulator          volumeMasterManipulator;
    protected VolumeManager volumeManager;
    protected Map<String, TreeNodeOperator>     registerer = new HashMap<>();
    protected Map<String, String >              metaTypeMap = new TreeMap<>();

    protected void registerDefaultMetaType( Class<?> genericType ) {
        this.metaTypeMap.put( genericType.getName(), genericType.getSimpleName().replace(
                this.volumeManager.getConfig().getVersionSignature(), ""
        ));
    }

    protected void registerDefaultMetaTypes() {
        this.registerDefaultMetaType( GenericFolder.class );
        this.registerDefaultMetaType( GenericFileNode.class );
    }

    public TitanVolumeOperatorFactory(VolumeManager volumeManager, VolumeMasterManipulator volumeMasterManipulator ){
        this.volumeManager = volumeManager;
        this.volumeMasterManipulator = volumeMasterManipulator;

        this.registerer.put(
                DefaultSimpleVolume,
                new SimpleVolumeOperator( this )
        );

        this.registerer.put(
                DefaultStripedVolume,
                new StripedVolumeOperator( this )
        );

        this.registerer.put(
                DefaultSpannedVolume,
                new SpannedVolumeOperator( this )
        );
        this.registerDefaultMetaTypes();
    }


    @Override
    public void register( String typeName, TreeNodeOperator functionalNodeOperation ) {
        this.registerer.put( typeName, functionalNodeOperation );
    }

    @Override
    public void registerMetaType( Class<?> clazz, String metaType ){
        this.registerMetaType( clazz.getName(), metaType );
    }

    @Override
    public void registerMetaType( String classFullName, String metaType ){
        this.metaTypeMap.put( classFullName, metaType );
    }

    @Override
    public String getMetaType( String classFullName ) {
        return this.metaTypeMap.get( classFullName );
    }

    @Override
    public VolumeOperator getOperator(String typeName ) {
        //Debug.trace( this.registerer.toString() );
        //Debug.trace( typeName );
        return (VolumeOperator) this.registerer.get( typeName );
    }

    @Override
    public VolumeManager getVolumeManager() {
        return this.volumeManager;
    }

    @Override
    public VolumeMasterManipulator getMasterManipulator() {
        return this.volumeMasterManipulator;
    }
}
