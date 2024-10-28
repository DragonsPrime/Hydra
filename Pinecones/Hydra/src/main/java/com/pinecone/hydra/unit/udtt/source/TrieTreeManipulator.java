package com.pinecone.hydra.unit.udtt.source;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.uoi.UOI;
import com.pinecone.hydra.unit.udtt.GUIDDistributedTrieNode;
import com.pinecone.hydra.unit.udtt.LinkedType;
import com.pinecone.hydra.unit.udtt.entity.ReparseLinkNode;

import java.util.List;

public interface TrieTreeManipulator extends Pinenut {
    void insert( TireOwnerManipulator ownerManipulator, GUIDDistributedTrieNode node );

    /** With detail meta data node information. */
    GUIDDistributedTrieNode getNode( GUID guid );

    /** Only with tree node index information. */
    GUIDDistributedTrieNode getTreeNodeOnly( GUID guid, GUID parentGuid );

    long countNode( GUID guid, GUID parentGuid );

    // TODO
    void insertNode( GUID guid, GUIDDistributedTrieNode distributedTreeNode );

    // TODO
    void updateNode( GUID guid, GUIDDistributedTrieNode distributedTreeNode );



    /** Purge / Deletion */
    void purge         ( GUID guid );

    void removeTreeNode( GUID guid );

    void removeTreeNodeByParentGuid( GUID parentGuid );

    void removeTreeNodeYoke( GUID guid, GUID parentGuid );

    void removeTreeNodeWithLinkedType( GUID guid, LinkedType linkedType );

    void removeNodeMeta( GUID guid );

    default void removeOwnedTreeNode ( GUID guid ) {
        this.removeTreeNodeWithLinkedType( guid, LinkedType.Owned );
    }

    default void removeHardLinkedTreeNode ( GUID guid ) {
        this.removeTreeNodeWithLinkedType( guid, LinkedType.Hard );
    }



    /** Lineage / Affinity */
    List<GUIDDistributedTrieNode > getChildren( GUID guid );

    List<GUID > fetchChildrenGuids( GUID parentGuid );

    List<GUID > fetchParentGuids( GUID guid );

    void removeInheritance( GUID childNode, GUID parentGUID );

    void updateType       ( UOI type, GUID guid );

    List<GUID > fetchRoot();

    boolean isRoot( GUID guid );



    /** Link / Reference */
    /**
     * Querying link-count, that the node be linked by its owner. [Strong/Weak]
     * 获取节点引用计数。 [根据强弱引用条件]
     * @return the link-count, which its has been linked.
     */
    long queryLinkedCount( GUID guid, LinkedType linkedType );

    long queryAllLinkedCount( GUID guid );

    default long queryStrongLinkedCount( GUID guid ) {
        return this.queryLinkedCount( guid, LinkedType.Owned );
    }

    default long queryWeakLinkedCount( GUID guid ) {
        return this.queryLinkedCount( guid, LinkedType.Hard );
    }

    void newLinkTag( GUID originalGuid, GUID dirGuid, String tagName, GUID tagGuid, LinkedType linkedType );

    default void newLinkTag( GUID originalGuid, GUID dirGuid, String tagName, GUID tagGuid ) {
        this.newLinkTag( originalGuid, dirGuid, tagName, tagGuid, LinkedType.Hard );
    }

    void updateLinkTagName( GUID tagGuid, String tagName );

    GUID getOriginalGuid( String tagName,GUID parentDirGuid );

    GUID getOriginalGuidByNodeGuid( String tagName, GUID nodeGUID );

    ReparseLinkNode getReparseLinkNode( String tagName, GUID parentDirGuid );

    ReparseLinkNode getReparseLinkNodeByNodeGuid( String tagName, GUID nodeGUID );

    List<GUID > fetchOriginalGuid( String tagName );

    List<GUID > fetchOriginalGuidRoot( String tagName );

    boolean isTagGuid( GUID guid );

    GUID getOriginalGuidByTagGuid( GUID tagGuid );

    void removeReparseLink( GUID guid );
}
