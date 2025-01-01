package com.pinecone.hydra.unit.imperium;

import com.pinecone.framework.system.prototype.PineUnit;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.system.ko.KernelObjectInstrument;
import com.pinecone.hydra.unit.imperium.entity.ReparseLinkNode;

import java.util.List;

/**
 *  Pinecone Ursus For Java Imperial Tree
 *  Author: Harold.E (Dragon King), Ken
 *  Copyright © 2008 - 2028 Bean Nuts Foundation All rights reserved.
 *  *****************************************************************************************
 *  Imperium (Imperial Tree)
 *  It is a distributed uniformed orchestration system tree used for uniformed and systematic orchestration of controlled distributed objects.
 *  Similar to kernel object management in other OS, it ensures that kernel objects and target-controlled objects in the system are marshaled
 *  and accessed in a uniformed URL-style.
 *  This data structure is based on a prefix tree and a GUID system, which can also be utilized for other marshaling purposes.
 *
 *  Imperium (统治树)，
 *  是一种分布式统一编排体系树，用于对受控分布式对象进行统一系统性编排。
 *  与其他操作系统内核对象管理类似，使得系统中的内核对象和欲控对象，整整齐齐的被编组和统一URL式访问。
 *  该数据结构基于前缀树和GUID身份证体系，是一类通用数据结构，也可用于其他编组目的的实现。
 *
 *  e.g. \Device\HarddiskVolume3\Users\dragonking\AppData\Local\
 *  e.g. /proc/137/task
 *  *****************************************************************************************
 */
public interface ImperialTree extends PineUnit {

    void insert( ImperialTreeNode distributedConfTreeNode );

    void affirmOwnedNode( GUID nodeGUID, GUID parentGUID );

    GUIDImperialTrieNode getNode(GUID guid );

    void purge( GUID guid );

    void removeTreeNodeOnly( GUID guid );

    void put( GUID guid, GUIDImperialTrieNode distributedTreeNode );

    boolean contains( GUID key );

    boolean containsChild( GUID parentGuid, GUID childGuid );

    GUID queryGUIDByPath( String path );

    List<GUIDImperialTrieNode> getChildren(GUID guid );

    List<GUID > fetchChildrenGuids( GUID parentGuid );

    List<GUID > fetchParentGuids( GUID guid );

    void removeInheritance( GUID childGuid,GUID parentGuid );


    String getCachePath( GUID guid );

    void removeCachePath( GUID guid );

    GUID getOwner( GUID guid );

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
