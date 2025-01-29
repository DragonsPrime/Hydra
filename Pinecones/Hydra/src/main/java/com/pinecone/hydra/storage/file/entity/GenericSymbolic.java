package com.pinecone.hydra.storage.file.entity;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.file.source.SymbolicManipulator;

import java.time.LocalDateTime;

public class GenericSymbolic implements Symbolic{
    private long                    enumId;
    private GUID                    guid;
    private LocalDateTime           createTime;
    private LocalDateTime           updateTime;
    private String                  name;
    private String                  reparsedPoint;
    private SymbolicMeta            symbolicMeta;
    private SymbolicManipulator     symbolicManipulator;

    public GenericSymbolic() {
    }

    public GenericSymbolic( SymbolicManipulator symbolicManipulator ) {
        this.symbolicManipulator = symbolicManipulator;
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    public GenericSymbolic(long enumId, GUID guid, LocalDateTime createTime, LocalDateTime updateTime, String name, String reparsedPoint, SymbolicMeta symbolicMeta) {
        this.enumId = enumId;
        this.guid = guid;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.name = name;
        this.reparsedPoint = reparsedPoint;
        this.symbolicMeta = symbolicMeta;
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


    public LocalDateTime getCreateTime() {
        return createTime;
    }


    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }


    public LocalDateTime getUpdateTime() {
        return updateTime;
    }


    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getReparsedPoint() {
        return reparsedPoint;
    }


    public void setReparsedPoint( String reparsedPoint ) {
        this.reparsedPoint = reparsedPoint;
    }


    public SymbolicMeta getSymbolicMeta() {
        return symbolicMeta;
    }


    public void setSymbolicMeta(SymbolicMeta symbolicMeta) {
        this.symbolicMeta = symbolicMeta;
    }

    @Override
    public void create() {
        this.symbolicManipulator.insert(this);
    }

    @Override
    public void remove() {
        this.symbolicManipulator.remove(this.guid);
        this.symbolicMeta.remove();
    }

    public String toString() {
        return "GenericSymbolic{enumId = " + enumId + ", guid = " + guid + ", createTime = " + createTime + ", updateTime = " + updateTime + ", name = " + name + ", reparsedPoint = " + reparsedPoint + ", symbolicMeta = " + symbolicMeta + "}";
    }
}
