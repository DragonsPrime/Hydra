package com.pinecone.hydra.storage.file.entity;

import com.pinecone.framework.util.id.GUID;

public class ArchFrame implements Frame {
    private long enumId;
    private GUID fileGuid;
    private GUID segGuid;
    private long segId;
    private String crc32;
    private long size;

    public ArchFrame() {
    }

    public ArchFrame(long enumId, GUID fileGuid, GUID segGuid, long segId, String crc32, long size) {
        this.enumId = enumId;
        this.fileGuid = fileGuid;
        this.segGuid = segGuid;
        this.segId = segId;
        this.crc32 = crc32;
        this.size = size;
    }


    public long getEnumId() {
        return enumId;
    }


    public void setEnumId(long enumId) {
        this.enumId = enumId;
    }


    public GUID getFileGuid() {
        return fileGuid;
    }


    public void setFileGuid(GUID fileGuid) {
        this.fileGuid = fileGuid;
    }


    public GUID getSegGuid() {
        return segGuid;
    }


    public void setSegGuid(GUID segGuid) {
        this.segGuid = segGuid;
    }


    public long getSegId() {
        return segId;
    }


    public void setSegId(long segId) {
        this.segId = segId;
    }


    public String getCrc32() {
        return crc32;
    }


    public void setCrc32(String crc32) {
        this.crc32 = crc32;
    }


    public long getSize() {
        return size;
    }


    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public void remove() {

    }

    @Override
    public void save() {

    }

    public String toString() {
        return "ArchCluster{enumId = " + enumId + ", fileGuid = " + fileGuid + ", segGuid = " + segGuid + ", segId = " + segId + ", crc32 = " + crc32 + ", size = " + size + "}";
    }
}
