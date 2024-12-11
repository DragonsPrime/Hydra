package com.pinecone.hydra.account.entity;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.unit.udtt.entity.TreeNode;

import java.time.LocalDateTime;

public interface Account extends FileElement {
    String getNickName();
    void setNickName( String nickName );

    String getKernelCredential();
    void setKernelCredential( String kernelCredential );

    GUID getCredentialGuid();
    void setCredentialGuid( GUID credentialGuid );

    String getKernelGroupType();
    void setKernelGroupType( String kernelGroupType );

    LocalDateTime getCreateTime();
    void setCreateTime( LocalDateTime createTime );

    LocalDateTime getUpdateTime();
    void setUpdateTime( LocalDateTime updateTime );
}
