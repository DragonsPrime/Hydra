package com.pinecone.hydra.storage.file.entity;

import com.pinecone.framework.util.id.GUID;

import java.time.LocalDateTime;

public abstract class ArchElementNode implements ElementNode {
    protected long                    enumId;
    protected GUID                    guid;
    protected LocalDateTime           createTime;
    protected LocalDateTime           updateTime;
    protected String                  name;

    protected FileSystemAttributes fileSystemAttributes;

    @Override
    public long getEnumId() {
        return enumId;
    }


    public void setEnumId(long enumId) {
        this.enumId = enumId;
    }


    @Override
    public GUID getGuid() {
        return guid;
    }


    @Override
    public void setGuid(GUID guid) {
        this.guid = guid;
    }


    @Override
    public LocalDateTime getCreateTime() {
        return createTime;
    }


    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }


    @Override
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }


    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }


    @Override
    public String getName() {
        return this.name;
    }


    @Override
    public void setName(String name) {
        this.name = name;
    }


    @Override
    public FileSystemAttributes getAttributes() {
        return fileSystemAttributes;
    }


    @Override
    public void setAttributes( FileSystemAttributes fileSystemAttributes ) {
        this.fileSystemAttributes = fileSystemAttributes;
    }
}
