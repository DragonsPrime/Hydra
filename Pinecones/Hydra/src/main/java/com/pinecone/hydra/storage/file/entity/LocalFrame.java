package com.pinecone.hydra.storage.file.entity;

import com.pinecone.hydra.storage.file.source.LocalFrameManipulator;

import java.time.LocalDateTime;

public interface LocalFrame extends Frame {
    LocalDateTime getCreateTime();
    void setCreateTime(LocalDateTime createTime);

    LocalDateTime getUpdateTime();
    void setUpdateTime(LocalDateTime updateTime);

    String getSourceName();
    void setSourceName(String sourceName);

    @Override
    default LocalFrame evinceLocalFrame() {
        return this;
    }

    void setLocalFrameManipulator(LocalFrameManipulator localFrameManipulator);

    long getDefinitionSize();
    void setDefinitionSize( long definitionSize );

    long getFileStartOffset();
    void setFileStartOffset( long fileStartOffset );
}
