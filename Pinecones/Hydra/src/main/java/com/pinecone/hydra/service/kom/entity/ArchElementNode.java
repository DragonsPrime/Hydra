package com.pinecone.hydra.service.kom.entity;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.json.hometype.BeanJSONEncoder;
import com.pinecone.hydra.service.ArchServiceFamilyMeta;
import com.pinecone.hydra.service.kom.ServiceFamilyNode;

public abstract class ArchElementNode extends ArchServiceFamilyMeta implements ServiceFamilyNode {
    protected long enumId;

    public ArchElementNode() {
        super();
    }

    @Override
    public long getEnumId() {
        return this.enumId;
    }

    @Override
    public void setEnumId( long enumId ) {
        this.enumId = enumId;
    }

    @Override
    public GUID getGuid() {
        return this.guid;
    }

    @Override
    public void setGuid( GUID guid ) {
        this.guid = guid;
    }

    @Override
    public void setName( String name ) {
        this.name = name;
    }

    @Override
    public void setScenario( String scenario ) {
        this.scenario = scenario;
    }

    @Override
    public void setPrimaryImplLang( String primaryImplLang ) {
        this.primaryImplLang = primaryImplLang;
    }

    @Override
    public void setExtraInformation( String extraInformation ) {
        this.extraInformation = extraInformation;
    }

    @Override
    public void setLevel( String level ) {
        this.level = level;
    }

    @Override
    public void setDescription( String description ) {
        this.description = description;
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