package com.pinecone.hydra.registry.entity;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.json.homotype.BeanJSONEncoder;
import com.pinecone.hydra.registry.KOMRegistry;
import com.pinecone.framework.util.id.GuidAllocator;

import java.time.LocalDateTime;

public abstract class ArchConfigNode extends ArchElementNode implements ConfigNode {
    protected GUID                    dataAffinityGuid;

    protected ConfigNodeMeta          configNodeMeta;

    protected KOMRegistry             registry;

    protected ArchConfigNode() {

    }

    public ArchConfigNode( KOMRegistry registry ) {
        this.registry = registry;

        GuidAllocator guidAllocator = this.registry.getGuidAllocator();
        this.setGuid( guidAllocator.nextGUID() );
        this.setCreateTime( LocalDateTime.now() );
    }



    public void apply( KOMRegistry registry ) {
        this.registry = registry;
    }

    @Override
    public GUID getDataAffinityGuid() {
        return this.dataAffinityGuid;
    }

    @Override
    public void setDataAffinityGuid( GUID parentGuid ) {
        this.dataAffinityGuid = parentGuid;
    }



    @Override
    public void copyMetaTo( GUID guid ) {
        this.registry.setDataAffinityGuid( guid, this.getDataAffinityGuid() );
    }

    @Override
    public void moveTo( String path ) {
        this.moveTo( this.registry.affirmNamespace( path ).getGuid() );
    }

    @Override
    public void moveTo( GUID destinationGuid ) {
        this.registry.getMasterTrieTree().moveTo( this.guid, destinationGuid );
    }



    @Override
    public ConfigNodeMeta getConfigNodeMeta() {
        return this.configNodeMeta;
    }

    @Override
    public void setConfigNodeMeta( ConfigNodeMeta configNodeMeta ) {
        this.configNodeMeta = configNodeMeta;
    }



    @Override
    public void setAttributes( Attributes attributes) {
        this.attributes = attributes;
    }

    @Override
    public KOMRegistry parentRegistry() {
        return this.registry;
    }

    @Override
    public String toJSONString() {
        return BeanJSONEncoder.BasicEncoder.encode( this );
    }

    @Override
    public String toString() {
        return this.toJSONString();
    }


    protected void putNewCopy(ConfigNode thisCopy, GUID destinationGuid ) {
        thisCopy.setName( this.getName() );
        thisCopy.setConfigNodeMeta( this.getConfigNodeMeta() );

        this.registry.put( thisCopy );
        this.registry.getMasterTrieTree().setGuidLineage( thisCopy.getGuid(), destinationGuid );
    }
}
