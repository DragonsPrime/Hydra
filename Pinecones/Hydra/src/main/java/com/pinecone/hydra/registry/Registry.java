package com.pinecone.hydra.registry;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.pinecone.hydra.registry.entity.ElementNode;
import com.pinecone.hydra.registry.entity.Namespace;
import com.pinecone.hydra.registry.entity.Properties;
import com.pinecone.hydra.registry.entity.Property;
import com.pinecone.hydra.registry.entity.RegistryTreeNode;
import com.pinecone.hydra.registry.entity.TextFile;
import com.pinecone.hydra.registry.entity.TextValue;
import com.pinecone.hydra.registry.entity.TypeConverter;
import com.pinecone.hydra.system.ko.KernelObjectInstrument;
import com.pinecone.hydra.unit.imperium.entity.EntityNode;
import com.pinecone.hydra.unit.imperium.entity.ReparseLinkNode;
import com.pinecone.hydra.unit.imperium.entity.TreeNode;

public interface Registry extends KernelObjectInstrument {
    RegistryConfig KernelRegistryConfig = new KernelRegistryConfig();

    RegistryConfig getRegistryConfig();

    void setPropertyTypeConverter( TypeConverter propertyTypeConverter ) ;

    void setTextValueTypeConverter( TypeConverter textValueTypeConverter ) ;

    TypeConverter getTextValueTypeConverter() ;

    TypeConverter getPropertyTypeConverter() ;



    ElementNode queryElement( String path );

    Properties getProperties( String path );

    Namespace getNamespace(String path );

    void remove( String path );

    Collection<Property > fetchProperties( String path );

    TextValue getTextValue( String path );



    /** Normal Tree Node or ReparseLinkNode**/
    EntityNode queryNode( String path );

    ReparseLinkNode queryReparseLink( String path );

    List<TreeNode > selectByName( String name );


    /**  Move "game/terraria/npc" => "game/minecraft/" => "game/minecraft/npc"*/
    void moveTo( String sourcePath, String destinationPath );

    /** Affirm destination path existed.*/
    void move( String sourcePath, String destinationPath );

    /**  Copy "game/terraria/npc" => "game/minecraft/" => "game/minecraft/npc"*/
    void copyTo( String sourcePath, String destinationPath );

    void copy( String sourcePath, String destinationPath );

    List<RegistryTreeNode> fetchRoot();



    Namespace      affirmNamespace        ( String path );

    Properties     affirmProperties       ( String path );

    TextFile       affirmTextConfig       ( String path );

    Properties     putProperties          ( String path, Map<String, Object > properties );

    TextFile       putTextValue           ( String path, String format, String value );


    // Return with json.
    Object         querySelectorJ         ( String szSelector );

    Object         querySelector          ( String szSelector );

    List           querySelectorAll       ( String szSelector );
}
