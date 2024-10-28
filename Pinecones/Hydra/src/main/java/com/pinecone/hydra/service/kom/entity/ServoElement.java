package com.pinecone.hydra.service.kom.entity;

import java.time.LocalDateTime;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.service.kom.ServiceFamilyNode;

public interface ServoElement extends ElementNode, ServiceFamilyNode {
    long getEnumId();
    void setEnumId( long id );

    GUID getGuid();
    void setGuid( GUID guid );

    GUID getMetaGuid();
    void setMetaGuid( GUID metaGuid );

    String getName();
    void setName( String name );

    String getPath();
    void setPath( String path );

    String getType();
    void setType( String type );

    String getAlias();
    void setAlias( String alias );

    String getResourceType();
    void setResourceType( String resourceType );

    LocalDateTime getCreateTime();
    void setCreateTime( LocalDateTime createTime );

    LocalDateTime getUpdateTime();
    void setUpdateTime( LocalDateTime updateTime );
}
