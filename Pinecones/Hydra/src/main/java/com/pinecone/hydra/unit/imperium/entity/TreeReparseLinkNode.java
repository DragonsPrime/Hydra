package com.pinecone.hydra.unit.imperium.entity;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.unit.imperium.LinkedType;

public class TreeReparseLinkNode implements ReparseLinkNode {
    protected String     tagName;
    protected GUID       tagGuid;
    protected LinkedType linkedType;
    protected GUID       targetNodeGuid;
    protected GUID       parentNodeGuid;

    public TreeReparseLinkNode(){

    }

    @Override
    public String getTagName() {
        return this.tagName;
    }

    public void setTagName( String tagName ) {
        this.tagName = tagName;
    }

    @Override
    public GUID getTagGuid() {
        return this.tagGuid;
    }

    public void setTagGuid( GUID tagGuid ) {
        this.tagGuid = tagGuid;
    }

    @Override
    public LinkedType getLinkedType() {
        return this.linkedType;
    }

    public void setLinkedType( LinkedType linkedType ) {
        this.linkedType = linkedType;
    }

    @Override
    public GUID getTargetNodeGuid() {
        return this.targetNodeGuid;
    }

    public void setTargetNodeGuid( GUID targetNodeGuid ) {
        this.targetNodeGuid = targetNodeGuid;
    }

    @Override
    public GUID getParentNodeGuid() {
        return this.parentNodeGuid;
    }

    public void setParentNodeGuid( GUID parentNodeGuid ) {
        this.parentNodeGuid = parentNodeGuid;
    }
}
