package com.pinecone.hydra.storage.file.entity;

import com.pinecone.hydra.unit.udtt.entity.TreeNode;

public interface FileTreeNode extends TreeNode {
    default FileNode evinceFileNode(){
        return null;
    }

    default Folder evinceFolder(){
        return null;
    }
}
