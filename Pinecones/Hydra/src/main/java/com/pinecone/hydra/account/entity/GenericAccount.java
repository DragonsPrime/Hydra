package com.pinecone.hydra.account.entity;

import com.pinecone.framework.util.id.GUID;

import java.time.LocalDateTime;

public class GenericAccount implements Account {
    protected long enumId;

    protected String userName;

    protected GUID guid;

    protected String nickName;

    protected String kernelCredential;

    protected GUID credentialGuid;

    protected String kernelGroupType;

    protected LocalDateTime createTime;

    protected LocalDateTime updateTime;


    @Override
    public long getEnumId() {
        return this.enumId;
    }

    @Override
    public void setEnumId(long enumId) {
        this.enumId = enumId;
    }

    @Override
    public String getUserName() {
        return this.userName;
    }

    @Override
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String getName() {
        return this.userName;
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
    public String getNickName() {
        return this.nickName;
    }

    @Override
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @Override
    public String getKernelCredential() {
        return this.kernelCredential;
    }

    @Override
    public void setKernelCredential(String kernelCredential) {
        this.kernelCredential = kernelCredential;
    }

    @Override
    public GUID getCredentialGuid() {
        return this.credentialGuid;
    }

    @Override
    public void setCredentialGuid(GUID credentialGuid) {
        this.credentialGuid = credentialGuid;
    }

    @Override
    public String getKernelGroupType() {
        return this.kernelGroupType;
    }

    @Override
    public void setKernelGroupType(String kernelGroupType) {
        this.kernelGroupType = kernelGroupType;
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
}
