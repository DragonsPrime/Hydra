package com.pinecone.hydra.storage.file.entity;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.file.source.SymbolicMetaManipulator;

public class GenericSymbolicMeta implements SymbolicMeta{
    private long enumId;
    private GUID guid;
    private SymbolicMetaManipulator symbolicMetaManipulator;


    public GenericSymbolicMeta() {
    }

    public GenericSymbolicMeta( SymbolicMetaManipulator symbolicMetaManipulator ) {
        this.symbolicMetaManipulator = symbolicMetaManipulator;
    }

    public GenericSymbolicMeta(long enumId, GUID guid) {
        this.enumId = enumId;
        this.guid = guid;
    }


    public long getEnumId() {
        return enumId;
    }


    public void setEnumId(long enumId) {
        this.enumId = enumId;
    }


    public GUID getGuid() {
        return guid;
    }


    public void setGuid(GUID guid) {
        this.guid = guid;
    }

    @Override
    public void save() {
        this.symbolicMetaManipulator.insert(this);
    }

    @Override
    public void remove() {
        this.symbolicMetaManipulator.remove(this.guid);
    }

    public String toString() {
        return "GenericSymbolicMeta{enumId = " + enumId + ", guid = " + guid + "}";
    }
}
