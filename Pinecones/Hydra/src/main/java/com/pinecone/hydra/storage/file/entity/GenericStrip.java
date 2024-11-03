package com.pinecone.hydra.storage.file.entity;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.file.source.LocalFrameManipulator;
import com.pinecone.hydra.storage.file.source.StripManipulator;

import java.time.LocalDateTime;

public class GenericStrip implements Strip{
    private long                        enumId;
    private GUID                        stripGuid;
    private long                        stripId;
    private GUID                        segGuid;
    private long                        size;
    private LocalDateTime               createTime;
    private LocalDateTime               updateTime;
    private String                      sourceName;
    private long                        definitionSize;
    private long                        fileStartOffset;
    private StripManipulator            stripManipulator;

    public GenericStrip(){

    }

    public GenericStrip( StripManipulator stripManipulator ){
        this.stripManipulator = stripManipulator;
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
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
    public GUID getStripGuid() {
        return this.stripGuid;
    }

    @Override
    public void setStripGuid(GUID stripGuid) {
        this.stripGuid = stripGuid;
    }

    @Override
    public long getStripId() {
        return this.stripId;
    }

    @Override
    public void setStripId(long stripId) {
        this.stripId = stripId;
    }

    @Override
    public GUID getSegGuid() {
        return this.segGuid;
    }

    @Override
    public void setSegGuid(GUID segGuid) {
        this.segGuid = segGuid;
    }

    @Override
    public long getSize() {
        return this.size;
    }

    @Override
    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public LocalDateTime getCreateTime() {
        return this.createTime;
    }

    @Override
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @Override
    public LocalDateTime getUpdateTime() {
        return this.updateTime;
    }

    @Override
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
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
    public long getDefinitionSize() {
        return this.definitionSize;
    }

    @Override
    public void setDefinitionSize(long definitionSize) {
        this.definitionSize = definitionSize;
    }

    @Override
    public long getFileStartOffset() {
        return this.fileStartOffset;
    }

    @Override
    public void setFileStartOffset(long fileStartOffset) {
        this.fileStartOffset = fileStartOffset;
    }

    @Override
    public void save() {
        this.stripManipulator.insert( this );
    }
}
