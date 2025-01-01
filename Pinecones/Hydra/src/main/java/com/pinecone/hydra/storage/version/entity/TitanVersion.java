package com.pinecone.hydra.storage.version.entity;

import com.pinecone.framework.util.id.GUID;

public class TitanVersion implements Version{
    private long enumId;

    private String version;

    private GUID targetStorageObjectGuid;

    private GUID fileGuid;

    private boolean enableCrc32;

    private String crc32;


    public TitanVersion() {
    }

    @Override
    public long getEnumId() {
        return this.enumId;
    }

    @Override
    public void setEnumId(long enumId) {
        this.enumId = enumId;
    }

    @Override
    public String getVersion() {
        return this.version;
    }

    @Override
    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public GUID getTargetStorageObjectGuid() {
        return this.targetStorageObjectGuid;
    }

    @Override
    public void setTargetStorageObjectGuid(GUID targetStorageObjectGuid) {
        this.targetStorageObjectGuid = targetStorageObjectGuid;
    }

    @Override
    public GUID getFileGuid() {
        return this.fileGuid;
    }

    @Override
    public void setFileGuid(GUID fileGuid) {
        this.fileGuid = fileGuid;
    }

    @Override
    public boolean getEnableCrc32() {
        return this.enableCrc32;
    }

    @Override
    public void setEnableCrc32(boolean enableCrc32) {
        this.enableCrc32 = enableCrc32;
    }

    @Override
    public String getCrc32() {
        return this.crc32;
    }

    @Override
    public void setCrc32(String crc32) {
        this.crc32 = crc32;
    }
}
