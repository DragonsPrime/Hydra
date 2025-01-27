package com.pinecone.hydra.account.entity;

import com.pinecone.framework.util.id.GUID;

import java.time.LocalDateTime;

public class GenericCredential implements Credential {
    private int enumId;

    private GUID guid;

    private String name;

    private String credential;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String type;
    public GenericCredential() {
    }
    // 全参构造方法
    public GenericCredential( GUID guid, String name, String credential, LocalDateTime createTime, LocalDateTime updateTime, String type) {

        this.guid = guid;
        this.name = name;
        this.credential = credential;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.type = type;
    }
    @Override
    public int getEnumId() {
        return this.enumId;
    }

    @Override
    public GUID getGuid() {
        return this.guid;
    }

    @Override
    public void setGuid(GUID guid) {
        this.guid = guid;
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
    public String getCredential() {
        return this.credential;
    }

    @Override
    public void setCredential(String credential) {
        this.credential = credential;
    }

    @Override
    public LocalDateTime getCreateTime() {
        return this.createTime;
    }

    @Override
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @Override
    public LocalDateTime getUpdateTime() {
        return this.updateTime;
    }

    @Override
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }
}
