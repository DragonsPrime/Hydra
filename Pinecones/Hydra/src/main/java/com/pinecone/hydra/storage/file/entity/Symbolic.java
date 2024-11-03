package com.pinecone.hydra.storage.file.entity;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;

import java.time.LocalDateTime;

public interface Symbolic extends Pinenut {
    long getEnumId();
    void setEnumId(long enumId);

    GUID getGuid();
    void setGuid(GUID guid);

    LocalDateTime getCreateTime();
    void setCreateTime(LocalDateTime createTime);

    LocalDateTime getUpdateTime();
    void setUpdateTime(LocalDateTime updateTime);

    String getName();
    void setName(String name);

    int getReparsedPoint();
    void setReparsedPoint( int ReparsedPoint );

    SymbolicMeta  getSymbolicMeta();
    void setSymbolicMeta(SymbolicMeta symbolicMeta);
    void create();
    void remove();
}
