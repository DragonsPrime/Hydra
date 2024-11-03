package com.pinecone.hydra.registry;

import com.pinecone.framework.system.Nullable;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.registry.entity.ConfigNode;
import com.pinecone.hydra.registry.entity.Namespace;
import com.pinecone.hydra.registry.entity.Properties;
import com.pinecone.hydra.registry.entity.Property;
import com.pinecone.hydra.registry.entity.RegistryTreeNode;
import com.pinecone.hydra.registry.entity.TextValue;
import com.pinecone.hydra.system.ko.kom.ReparseKOMTree;
import com.pinecone.hydra.unit.udtt.entity.TreeNode;

import java.util.List;

public interface KOMRegistry extends Registry, ReparseKOMTree {

    @Override
    String getPath( GUID guid );

    @Override
    String getFullName( GUID guid );

    @Override
    GUID put( TreeNode treeNode );

    @Override
    RegistryTreeNode get( GUID guid );

    @Override
    RegistryTreeNode get( GUID guid, int depth );

    @Override
    RegistryTreeNode getSelf( GUID guid );

    Properties getProperties( GUID guid );

    Namespace getNamespace( GUID guid );

    @Override
    GUID queryGUIDByPath( String path );

    @Override
    GUID queryGUIDByFN  ( String fullName );

    List<Property > fetchProperties( GUID guid );

    TextValue getTextValue( GUID guid );

    ConfigNode getConfigNode( GUID guid );

    @Override
    RegistryConfig getConfig();



    void putProperty( Property property, GUID configNodeGuid );

    void putTextValue( GUID guid, String text, String format );

    void updateProperty( @Nullable GUID configNodeGuid, Property property );

    default void updateProperty( Property property ) {
        this.updateProperty( null, property );
    }

    void updateTextValue( TextValue textValue, GUID configNodeGuid );

    void updateTextValue( GUID guid, String text, String format );


    @Override
    void remove( GUID guid );

    @Override
    void removeReparseLink( GUID guid );

    void removeProperty( GUID guid, String key );

    void removeTextValue( GUID guid );


    @Override
    List<TreeNode > getChildren( GUID guid );

    @Override
    void rename( GUID guid, String name );

    default void rename( String path, String name ) {
        this.rename( this.assertPath( path ), name );
    }

    @Override
    default GUID assertPath( String path, String pathType ) throws IllegalArgumentException {
        GUID guid      = this.queryGUIDByPath( path );
        if( guid == null ) {
            throw new IllegalArgumentException( "Undefined " + pathType + " '" + path + "'" );
        }

        return guid;
    }

    @Override
    default GUID assertPath( String path ) throws IllegalArgumentException {
        return this.assertPath( path, "path" );
    }

    List<TreeNode > getAllTreeNode();



    /** 断言，确保节点唯一拥有关系*/
    @Override
    void affirmOwnedNode( GUID parentGuid, GUID childGuid  );

    @Override
    void newHardLink    ( GUID sourceGuid, GUID targetGuid );

    /** set affinityParentGuid for child.*/
    void setDataAffinityGuid ( GUID childGuid, GUID affinityParentGuid  );

    default void setDataAffinity ( String childPath, String parentPath ) {
        GUID childGuid      = this.assertPath( childPath );
        GUID parentGuid     = this.assertPath( parentPath );
        if( childGuid == parentGuid ) {
            throw new IllegalArgumentException( "Cyclic path detected '" + childPath + "'" );
        }

        this.setDataAffinityGuid( childGuid, parentGuid );
    }

    @Override
    void newLinkTag( GUID originalGuid,GUID dirGuid,String tagName );

    @Override
    void newLinkTag( String originalPath ,String dirPath,String tagName );

    @Override
    void updateLinkTag( GUID tagGuid,String tagName);


    void copyPropertiesTo( GUID sourceGuid, GUID destinationGuid );

    void copyTextValueTo( GUID sourceGuid, GUID destinationGuid );
}
