package com.pinecone.hydra.storage.file.entity;

import java.time.LocalDateTime;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;

public interface ReparseSemanticNode extends Pinenut {
    long getEnumId();
    void setEnumId(long enumId);

    GUID getGuid();
    void setGuid(GUID guid);

    LocalDateTime getCreateTime();
    void setCreateTime( LocalDateTime createTime );

    LocalDateTime getUpdateTime();
    void setUpdateTime( LocalDateTime updateTime );

    String getName();
    void setName( String name );

    String getReparsedPoint();
    void setReparsedPoint( String reparsedPoint );
}
