package com.pinecone.hydra.service.kom.entity;

import java.util.Set;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.json.JSONObject;
import com.pinecone.hydra.service.kom.GenericNamespaceRules;
import com.pinecone.hydra.unit.udtt.GUIDDistributedTrieNode;

public interface Namespace extends FolderElement {
    Set<String > UnbeanifiedKeys = Set.of( "distributedTreeNode", "classificationRules" );

    long getEnumId();

    void setEnumId( long id );

    GUID getGuid();

    void setGuid( GUID guid );

    GUID getMetaGuid();

    void setMetaGuid( GUID metaGuid );

    String getName();

    void setName( String name );

    GUID getRulesGUID();

    void setRulesGUID( GUID rulesGUID );

    GenericNamespaceRules getClassificationRules();

    void setClassificationRules( GenericNamespaceRules classificationRules );

    GUIDDistributedTrieNode getDistributedTreeNode();

    void setDistributedTreeNode( GUIDDistributedTrieNode distributedTreeNode );

    @Override
    default Namespace evinceNamespace() {
        return this;
    }

    JSONObject toJSONDetails();
}
