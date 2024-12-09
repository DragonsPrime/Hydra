package com.pinecone.hydra.account.entity;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;

import java.time.LocalDateTime;

public interface credential extends Pinenut {
    int getEnumId();

    GUID getGuid();
    void setGuid( GUID guid );

    String getName();
    void setName( String name );

    String getCredential();
    void setCredential( String credential );

    LocalDateTime getCreateTime();
    void setCreateTime( LocalDateTime createTime );

    LocalDateTime getUpdateTime();
    void setUpdateTime( LocalDateTime updateTime );

    String getType();
    void setType( String type );
}
