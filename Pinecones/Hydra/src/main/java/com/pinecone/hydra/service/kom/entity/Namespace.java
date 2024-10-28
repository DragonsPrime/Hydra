package com.pinecone.hydra.service.kom.entity;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.service.kom.GenericNamespaceRules;
import com.pinecone.hydra.unit.udtt.GUIDDistributedTrieNode;

public interface Namespace extends FolderElement {
    long getEnumId();

    void setEnumId( long id );

    GUID getGuid();

    void setGuid( GUID guid );

    String getName();

    void setName( String name );

    GUID getRulesGUID();

    void setRulesGUID( GUID rulesGUID );

    GenericNamespaceRules getClassificationRules();

    void setClassificationRules( GenericNamespaceRules classificationRules );

    GUIDDistributedTrieNode getDistributedTreeNode();

    void setDistributedTreeNode( GUIDDistributedTrieNode distributedTreeNode );

    ArchElementNode getAttributes();

    void setNodeCommonData( ArchElementNode nodeAttributes );

    @Override
    default Namespace evinceNamespace() {
        return this;
    }

}
