package com.pinecone.hydra.storage.file.entity;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.file.KOMFileSystem;

import java.time.LocalDateTime;

public interface ElementNode extends FileTreeNode {
    long getEnumId();

    GUID getGuid();
    void setGuid(GUID guid);

    LocalDateTime getCreateTime();

    LocalDateTime getUpdateTime();

    String getName();
    void setName(String name);

    FileSystemAttributes getAttributes();
    void setAttributes( FileSystemAttributes attributes );

    KOMFileSystem parentFileSystem();

}
