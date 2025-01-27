package com.pinecone.hydra.account.entity;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.json.homotype.BeanJSONEncoder;
import com.pinecone.hydra.account.AccountManager;

import java.time.LocalDateTime;

public class GenericAccount extends ArchElementNode implements Account {
    protected long enumId;

    protected String name;

    protected GUID guid;

    protected String nickName;

    protected String kernelCredential;

    protected GUID credentialGuid;

    protected String kernelGroupType;
    protected String role;

    protected LocalDateTime createTime;

    protected LocalDateTime updateTime;

    public GenericAccount(
            String name,
            GUID guid,
            String nickName,
            String kernelCredential,
            GUID credentialGuid,
            String kernelGroupType,
            String role,
            LocalDateTime createTime,
            LocalDateTime updateTime
    ) {
        this.name = name;
        this.guid = guid;
        this.nickName = nickName;
        this.kernelCredential = kernelCredential;
        this.credentialGuid = credentialGuid;
        this.kernelGroupType = kernelGroupType;
        this.role = role;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }


    public GenericAccount(){
        super();
    }

    public GenericAccount(AccountManager accountManager){
        super(accountManager);
    }


    @Override
    public String getRole() {
       return this.role;
    }

    @Override
    public void setRole(String role) {
                this.role = role;
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
    @Override
    public String toJSONString() {
        return BeanJSONEncoder.BasicEncoder.encode( this );
    }

    @Override
    public String toString() {
        return this.toJSONString();
    }
}
