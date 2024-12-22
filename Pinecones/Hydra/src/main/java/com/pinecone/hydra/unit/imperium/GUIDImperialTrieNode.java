package com.pinecone.hydra.unit.imperium;

import com.pinecone.framework.unit.KeyValue;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.json.JSONEncoder;
import com.pinecone.framework.util.uoi.UOI;

import java.util.List;

/**
 *  Pinecone Ursus For Java GUIDDistributedTrieNode
 *  Author:
 *  Copyright © 2008 - 2028 Bean Nuts Foundation All rights reserved.
 *  **********************************************************
 *  Thanks for genius`s contribution.
 *  **********************************************************
 */
public class GUIDImperialTrieNode implements ImperialTreeNode {
    //节点id
    protected long enumId;
    //节点uuid
    protected GUID guid;
    //父节点uuid
    protected List<GUID > parentGUID;
    //基础信息uuid
    protected GUID attributesGUID;
    //元信息uuid
    protected GUID nodeMetadataGUID;
    //节点的类型方便获取数据
    protected UOI type;


    public GUIDImperialTrieNode() {
    }

    public GUIDImperialTrieNode(
            long enumId, GUID guid, List<GUID> parentGUID, GUID baseDataGUID, GUID nodeMetadataGUID, UOI type
    ) {
        this.enumId = enumId;
        this.guid = guid;
        this.parentGUID = parentGUID;
        this.attributesGUID = baseDataGUID;
        this.nodeMetadataGUID = nodeMetadataGUID;
        this.type = type;
    }

    @Override
    public long getEnumId() {
        return this.enumId;
    }

    @Override
    public void setEnumId( long enumId ) {
        this.enumId = enumId;
    }

    @Override
    public GUID getGuid() {
        return this.guid;
    }

    @Override
    public void setGuid( GUID guid ) {
        this.guid = guid;
    }

    @Override
    public List<GUID > getParentGUIDs() {
        return this.parentGUID;
    }

    @Override
    public void setParentGUID( List<GUID> parentGUID ) {
        this.parentGUID = parentGUID;
    }

    @Override
    public GUID getAttributesGUID() {
        return this.attributesGUID;
    }

    @Override
    public void setBaseDataGUID( GUID baseDataGUID ) {
        this.attributesGUID = baseDataGUID;
    }

    @Override
    public GUID getNodeMetadataGUID() {
        return this.nodeMetadataGUID;
    }

    @Override
    public void setNodeMetadataGUID( GUID nodeMetadataGUID ) {
        this.nodeMetadataGUID = nodeMetadataGUID;
    }

    @Override
    public UOI getType() {
        return this.type;
    }

    @Override
    public void setType(UOI type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return JSONEncoder.stringifyMapFormat( new KeyValue[]{
                new KeyValue<>( "class", this.className() ),
                new KeyValue<>( "guid", this.getGuid() ),
                new KeyValue<>( "type", this.getType() )
        } );
    }
}
