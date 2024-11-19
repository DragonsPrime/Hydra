package com.pinecone.hydra.storage;

import com.pinecone.framework.util.id.GUID;

public class TitanStorageExportExportIORequest implements StorageExportIORequest {
    private String sourceName;
    private String crc32;
    private Number size;
    private GUID   storageGuid;

    @Override
    public String getSourceName() {
        return this.sourceName;
    }

    @Override
    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    @Override
    public String getCrc32() {
        return this.crc32;
    }

    @Override
    public void setCrc32(String crc32) {
        this.crc32 = crc32;
    }

    @Override
    public Number getSize() {
        return this.size;
    }

    @Override
    public void setSize(Number size) {
        this.size = size;
    }

    @Override
    public GUID getStorageObjectGuid() {
        return this.storageGuid;
    }

    @Override
    public void setStorageObjectGuid(GUID storageObjectGuid) {
        this.storageGuid = storageObjectGuid;
    }
}
