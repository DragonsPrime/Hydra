package com.walnuts.sparta.uofs.service.domain.vo;

import com.pinecone.hydra.storage.file.entity.FileTreeNode;

import java.util.List;

public class FolderContentVo {
    private List< FileTreeNode > fileTreeNodes;


    public FolderContentVo() {
    }

    public FolderContentVo(List<FileTreeNode> fileTreeNodes) {
        this.fileTreeNodes = fileTreeNodes;
    }

    public List<FileTreeNode> getFileTreeNodes() {
        return fileTreeNodes;
    }


    public void setFileTreeNodes(List<FileTreeNode> fileTreeNodes) {
        this.fileTreeNodes = fileTreeNodes;
    }

    public String toString() {
        return "FolderContentVo{fileTreeNodes = " + fileTreeNodes + "}";
    }
}
