package com.pinecone.hydra.registry.entity;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.json.homotype.BeanJSONEncoder;

public class GenericConfigNodeMeta implements ConfigNodeMeta {
    private long enumId;

    private GUID guid;


    public GenericConfigNodeMeta() {
    }

    public GenericConfigNodeMeta(long enumId, GUID guid) {
        this.enumId = enumId;
        this.guid = guid;
    }

    @Override
    public long getEnumId() {
        return enumId;
    }

    @Override
    public void setEnumId( long enumId ) {
        this.enumId = enumId;
    }

    @Override
    public GUID getGuid() {
        return guid;
    }

    @Override
    public void setGuid( GUID guid ) {
        this.guid = guid;
    }

    @Override
    public String toJSONString() {
        return BeanJSONEncoder.BasicEncoder.encode( this );
    }

    @Override
    public String toString() {
        return this.toJSONString();
    }
}
