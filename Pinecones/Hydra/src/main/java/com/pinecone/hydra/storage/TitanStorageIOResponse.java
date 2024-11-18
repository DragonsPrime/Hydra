package com.pinecone.hydra.storage;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.file.entity.FileNode;
import com.pinecone.hydra.storage.file.entity.Frame;

public class TitanStorageIOResponse implements StorageIOResponse {
    private GUID objectGuid;
    private long checksum;
    private long parityCheck;
    private String crc32;
    private String sourceName;
    private GUID bottomGuid;

    @Override
    public GUID getObjectGuid() {
        return this.objectGuid;
    }

    @Override
    public void setObjectGuid(GUID objectGuid) {
        this.objectGuid = objectGuid;
    }

    @Override
    public long getChecksum() {
        return this.checksum;
    }

    @Override
    public void setChecksum(long checksum) {
        this.checksum = checksum;
    }

    @Override
    public long getParityCheck() {
        return this.parityCheck;
    }

    @Override
    public void setParityCheck(long parityCheck) {
        this.parityCheck = parityCheck;
    }

    @Override
    public String getCre32() {
        return this.crc32;
    }

    @Override
    public void setCrc32(String crc32) {
        this.crc32 = crc32;
    }

    @Override
    public String getSourceName() {
        return this.sourceName;
    }

    @Override
    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    @Override
    public GUID getBottomGuid() {
        return this.bottomGuid;
    }

    @Override
    public void setBottomGuid(GUID bottomGuid) {
        this.bottomGuid = bottomGuid;
    }

    @Override
    public Frame toFrame() {
        return null;
    }

    @Override
    public FileNode toFileNode() {
        return null;
    }

}
