package com.pinecone.hydra.storage.file.entity;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;

import java.time.LocalDateTime;

public interface Strip extends Pinenut {
    long getEnumId();

    void setEnumId(long enumId);

    GUID getStripGuid();
    void setStripGuid(GUID stripGuid);

    long getStripId();
    void setStripId( long stripId );

    GUID getSegGuid();
    void setSegGuid(GUID segGuid);

    long getSize();
    void setSize(long size);
    LocalDateTime getCreateTime();
    void setCreateTime(LocalDateTime createTime);

    LocalDateTime getUpdateTime();
    void setUpdateTime(LocalDateTime updateTime);

    String getSourceName();
    void setSourceName(String sourceName);
    long getDefinitionSize();
    void setDefinitionSize( long definitionSize );

    long getFileStartOffset();
    void setFileStartOffset( long fileStartOffset );
    void save();
}
