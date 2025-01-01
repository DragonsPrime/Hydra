package com.pinecone.hydra.registry.entity;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.unit.imperium.entity.TreeNode;

public interface RegistryTreeNode extends TreeNode {

    default ConfigNode evinceConfigNode(){
        return null;
    }

    default Namespace evinceNamespace(){
        return null;
    }

    default Properties evinceProperties() {
        return null;
    }

    default TextFile evinceTextFile() {
        return null;
    }

    void copyTo( String path );

    void copyTo( GUID guid );

    void moveTo( String path );

    void moveTo( GUID destinationGuid );

}
