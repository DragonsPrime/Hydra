package com.pinecone.hydra.storage.file.entity;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.id.GuidAllocator;
import com.pinecone.hydra.storage.file.KOMFileSystem;

import java.time.LocalDateTime;

public abstract class ArcReparseSemanticNode extends ArchElementNode implements ReparseSemanticNode {
    protected String                  reparsedPoint;

    protected KOMFileSystem           fileSystem;

    public ArcReparseSemanticNode(){
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    public ArcReparseSemanticNode( KOMFileSystem fileSystem ) {
        this();
        this.fileSystem = fileSystem;
        GuidAllocator guidAllocator = this.fileSystem.getGuidAllocator();
        this.setGuid( guidAllocator.nextGUID() );
    }

    @Override
    public String getReparsedPoint() {
        return this.reparsedPoint;
    }

    @Override
    public void setReparsedPoint(String reparsedPoint) {
        this.reparsedPoint = reparsedPoint;
    }

    @Override
    public KOMFileSystem parentFileSystem() {
        return this.fileSystem;
    }
}
