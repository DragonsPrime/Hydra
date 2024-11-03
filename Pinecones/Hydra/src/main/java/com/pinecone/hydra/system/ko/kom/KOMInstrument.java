package com.pinecone.hydra.system.ko.kom;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.system.ko.CascadeInstrument;
import com.pinecone.hydra.unit.udtt.entity.EntityNode;
import com.pinecone.hydra.unit.udtt.entity.TreeNode;

import java.util.List;

public interface KOMInstrument extends CascadeInstrument {
    @Override
    KOMInstrument parent();

    @Override
    default void setName( String name ) {
        CascadeInstrument.super.setName( name );
    }

    String getPath( GUID guid );

    String getFullName( GUID guid );

    GUID queryGUIDByPath( String path );

    GUID queryGUIDByFN  ( String fullName );

    default GUID assertPath( String path, String pathType ) throws IllegalArgumentException {
        GUID guid      = this.queryGUIDByPath( path );
        if( guid == null ) {
            throw new IllegalArgumentException( "Undefined " + pathType + " '" + path + "'" );
        }

        return guid;
    }

    default GUID assertPath( String path ) throws IllegalArgumentException {
        return this.assertPath( path, "path" );
    }

    boolean contains( GUID nodeGuid );

    GUID put( TreeNode treeNode );

    TreeNode get( GUID guid );

    GUID queryGUIDByNS( String path, String szBadSep, String szTargetSep );

    TreeNode get( GUID guid, int depth );

    TreeNode getSelf( GUID guid );

    void remove( GUID guid );

    void remove( String path );

    List<TreeNode > getChildren( GUID guid );

    List<GUID > fetchChildrenGuids( GUID guid );

    Object queryEntityHandleByNS( String path, String szBadSep, String szTargetSep );

    EntityNode queryNode( String path );

    List<? extends TreeNode > fetchRoot();

    void rename( GUID guid, String name );

}
