package com.pinecone.hydra.account.entity;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;

import java.time.LocalDateTime;

public interface authorization extends Pinenut {
    int getEnumId();

    GUID getGuid();
    void setGuid( GUID guid );

    String getUserName();
    void setUserName( String userName );

    GUID getUserGuid();
    void setUserGuid( GUID userGuid );

    GUID getCredentialGuid();
    void setCredentialGuid( GUID credentialGuid );

    String getPrivilegeToken();
    void setPrivilegeToken(String privilegeToken);

    GUID getPrivilegeGuid();
    void setPrivilegeGuid( GUID privilegeGuid );

    LocalDateTime getCreateTime();
    void setCreateTime( LocalDateTime createTime );

    LocalDateTime getUpdateTime();
    void setUpdateTime( LocalDateTime updateTime );
}
