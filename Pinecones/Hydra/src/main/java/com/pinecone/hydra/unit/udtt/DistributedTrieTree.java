package com.pinecone.hydra.unit.udtt;

import com.pinecone.framework.system.prototype.PineUnit;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.system.ko.KernelObjectInstrument;
import com.pinecone.hydra.unit.udtt.entity.ReparseLinkNode;

import java.util.List;

public interface DistributedTrieTree extends PineUnit {

    void insert( DistributedTreeNode distributedConfTreeNode );

    void affirmOwnedNode( GUID nodeGUID, GUID parentGUID );

    GUIDDistributedTrieNode getNode(GUID guid );

    void purge( GUID guid );

    void removeTreeNodeOnly( GUID guid );

    void put( GUID guid, GUIDDistributedTrieNode distributedTreeNode );

    boolean containsKey( GUID key );

    GUID queryGUIDByPath( String path );

    List<GUIDDistributedTrieNode> getChildren( GUID guid );

    List<GUID > fetchChildrenGuids( GUID parentGuid );

    List<GUID > fetchParentGuids( GUID guid );

    void removeInheritance( GUID childGuid,GUID parentGuid );


    String getCachePath( GUID guid );

    void removeCachePath( GUID guid );

    GUID getOwner( GUID guid);

    void setOwner( GUID sourceGuid, GUID targetGuid );

    void setGuidLineage( GUID sourceGuid, GUID targetGuid );

    List<GUID > getSubordinates( GUID guid );

    void insertCachePath( GUID guid,String path );


    List<GUID > fetchRoot();

    boolean isRoot( GUID guid );




    /** Link / Reference */
    long queryLinkedCount( GUID guid, LinkedType linkedType );

    long queryAllLinkedCount( GUID guid );

    default long queryStrongLinkedCount( GUID guid ) {
        return this.queryLinkedCount( guid, LinkedType.Owned );
    }

    default long queryWeakLinkedCount( GUID guid ) {
        return this.queryLinkedCount( guid, LinkedType.Hard );
    }

    void newHardLink( GUID sourceGuid, GUID targetGuid );

    void moveTo( GUID sourceGuid, GUID destinationGuid );

    void newLinkTag( GUID originalGuid, GUID dirGuid, String tagName, KernelObjectInstrument instrument );

    void updateLinkTagName( GUID tagGuid, String tagName );


    /** Link Tag */
    GUID getOriginalGuid( String tagName, GUID parentDirGUID );

    GUID getOriginalGuidByNodeGuid( String tagName, GUID nodeGUID );

    List<GUID > fetchOriginalGuid( String tagName );

    List<GUID > fetchOriginalGuidRoot( String tagName );

    ReparseLinkNode getReparseLinkNode( String tagName, GUID parentDirGuid );

    ReparseLinkNode getReparseLinkNodeByNodeGuid( String tagName, GUID nodeGUID );

    GUID getOriginalGuid( GUID tagGuid );

    void removeReparseLink( GUID guid );

    boolean isTagGuid( GUID guid );

}
