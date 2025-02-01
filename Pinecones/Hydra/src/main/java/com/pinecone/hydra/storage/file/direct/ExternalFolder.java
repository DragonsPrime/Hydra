package com.pinecone.hydra.storage.file.direct;

import com.pinecone.hydra.storage.file.entity.ElementNode;
import com.pinecone.hydra.storage.file.entity.FileTreeNode;

import java.io.File;
import java.net.URI;
import java.util.List;

public interface ExternalFolder extends ElementNode {
    File getNativeFile();

    URI toURI();

    String getName();

    String getParentPath();

    String getPath();

    String[] list();

    File[]   listFiles();

    List<FileTreeNode> listItem();
}
