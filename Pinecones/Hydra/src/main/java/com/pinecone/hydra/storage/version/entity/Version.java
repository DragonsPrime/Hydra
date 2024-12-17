package com.pinecone.hydra.storage.version.entity;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;

public class Version implements Pinenut{
    private long enumId;

    private String version;

    private GUID targetStorageObjectGuid;

    private GUID fileGuid;


    public Version() {
    }

    public Version(long enumId, String version, GUID targetStorageObjectGuid, GUID fileGuid) {
        this.enumId = enumId;
        this.version = version;
        this.targetStorageObjectGuid = targetStorageObjectGuid;
        this.fileGuid = fileGuid;
    }

    public long getEnumId() {
        return enumId;
    }

    public void setEnumId(long enumId) {
        this.enumId = enumId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public GUID getTargetStorageObjectGuid() {
        return targetStorageObjectGuid;
    }

    public void setTargetStorageObjectGuid(GUID targetStorageObjectGuid) {
        this.targetStorageObjectGuid = targetStorageObjectGuid;
    }

    public GUID getFileGuid() {
        return fileGuid;
    }

    public void setFileGuid(GUID fileGuid) {
        this.fileGuid = fileGuid;
    }


}
