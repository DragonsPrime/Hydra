package com.pinecone.hydra.storage.file.entity;

import com.pinecone.hydra.storage.file.source.LocalFrameManipulator;

import java.time.LocalDateTime;

public class GenericLocalFrame extends ArchFrame implements LocalFrame {
    private LocalDateTime               createTime;
    private LocalDateTime               updateTime;
    private String                      sourceName;
    private LocalFrameManipulator       localFrameManipulator;
    private long                        definitionSize;
    private long                        fileStartOffset;

    @Override
    public long getDefinitionSize() {
        return this.definitionSize;
    }

    @Override
    public void setDefinitionSize(long definitionSize) {
        this.definitionSize = definitionSize;
    }

    public GenericLocalFrame() {
    }

    public GenericLocalFrame(LocalDateTime createTime, LocalDateTime updateTime, String sourceName) {
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.sourceName = sourceName;
    }

    public GenericLocalFrame( LocalFrameManipulator localFrameManipulator ) {
        this.localFrameManipulator = localFrameManipulator;
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }


    public LocalDateTime getCreateTime() {
        return createTime;
    }


    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @Override
    public long getFileStartOffset() {
        return this.fileStartOffset;
    }

    @Override
    public void setFileStartOffset(long fileStartOffset) {
        this.fileStartOffset = fileStartOffset;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }


    @Override
    public void setLocalFrameManipulator(LocalFrameManipulator localFrameManipulator) {
        this.localFrameManipulator = localFrameManipulator;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }


    public String getSourceName() {
        return sourceName;
    }


    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    @Override
    public void save() {
        this.localFrameManipulator.insert(this);
    }

    @Override
    public void remove() {
        this.localFrameManipulator.remove( this.getSegGuid() );
    }
    public String toString() {
        return "GenericLocalCluster{createTime = " + createTime + ", updateTime = " + updateTime + ", sourceName = " + sourceName + "}";
    }


}
