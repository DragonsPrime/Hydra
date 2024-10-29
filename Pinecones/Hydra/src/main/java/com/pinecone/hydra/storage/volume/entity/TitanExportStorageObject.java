package com.pinecone.hydra.storage.volume.entity;

public class TitanExportStorageObject implements ExportStorageObject{
    private String sourceName;
    private String crc32;
    private Number size;
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
}
