package com.pinecone.hydra.unit.imperium;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.uoi.UOI;

import java.util.List;

/**
 *  Pinecone Ursus For Java
 *  Author: Harold.E / JH.W (DragonKing)
 *  Copyright © 2008 - 2028 Bean Nuts Foundation All rights reserved.
 *  *****************************************************************************************
 *  Uniform Distributed Trie Tree (UDTT)
 *  *****************************************************************************************
 */
public interface ImperialTreeNode extends Pinenut {
    long getEnumId();
    void setEnumId(long enumId);

    GUID getGuid();
    void setGuid(GUID guid);

    List<GUID> getParentGUIDs();
    void setParentGUID(List<GUID> parentGUID);

    GUID getAttributesGUID();
    void setBaseDataGUID(GUID baseDataGUID);

    GUID getNodeMetadataGUID();
    void setNodeMetadataGUID(GUID nodeMetadataGUID);

    UOI getType();
    void setType( UOI type );
}
