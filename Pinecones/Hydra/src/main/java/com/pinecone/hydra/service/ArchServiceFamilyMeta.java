package com.pinecone.hydra.service;

import java.util.Map;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.id.Identification;
import com.pinecone.framework.util.json.homotype.BeanMapDecoder;
import com.pinecone.framework.util.json.homotype.BeanJSONEncoder;
import com.pinecone.ulf.util.id.GUIDs;

public abstract class ArchServiceFamilyMeta implements ServiceFamilyMeta {
    protected GUID   guid;

    protected String name;

    protected String scenario;

    protected String primaryImplLang;

    protected String extraInformation;

    protected String level;

    protected String description;

    public ArchServiceFamilyMeta() {
    }

    public ArchServiceFamilyMeta( Map<String, Object > joEntity ) {
        this.apply( joEntity );
    }

    protected ArchServiceFamilyMeta apply( Map<String, Object > joEntity ) {
        String szGuid = (String) joEntity.get( "guid" );
        if( szGuid != null ) {
            this.guid = GUIDs.GUID72( (String) joEntity.get( "guid" ) );
        }
        BeanMapDecoder.BasicDecoder.decode( this, joEntity );

        return this;
    }

    public GUID getGuid() {
        return this.guid;
    }

    @Override
    public Identification getId() {
        return this.getGuid();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getScenario() {
        return this.scenario;
    }

    @Override
    public String getPrimaryImplLang() {
        return this.primaryImplLang;
    }

    @Override
    public String getExtraInformation() {
        return this.extraInformation;
    }

    @Override
    public String getLevel() {
        return this.level;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String toString() {
        return this.toJSONString();
    }

    @Override
    public String toJSONString() {
        return BeanJSONEncoder.BasicEncoder.encode( this );
    }
}
