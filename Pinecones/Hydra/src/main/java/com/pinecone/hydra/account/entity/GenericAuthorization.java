package com.pinecone.hydra.account.entity;

import com.pinecone.framework.util.id.GUID;

import java.time.LocalDateTime;

public class GenericAuthorization implements Authorization{
    private int enumId;

    private GUID guid;

    private String userName;

    private GUID userGuid;

    private GUID credentialGuid;

    private String privilegeToken;

    private GUID privilegeGuid;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

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
    public String getUserName() {
        return this.userName;
    }

    @Override
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public GUID getUserGuid() {
        return this.userGuid;
    }

    @Override
    public void setUserGuid(GUID userGuid) {
        this.userGuid = userGuid;
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
    public String getPrivilegeToken() {
        return this.privilegeToken;
    }

    @Override
    public void setPrivilegeToken(String privilegeToken) {
        this.privilegeToken = privilegeToken;
    }

    @Override
    public GUID getPrivilegeGuid() {
        return this.privilegeGuid;
    }

    @Override
    public void setPrivilegeGuid(GUID privilegeGuid) {
        this.privilegeGuid = privilegeGuid;
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
