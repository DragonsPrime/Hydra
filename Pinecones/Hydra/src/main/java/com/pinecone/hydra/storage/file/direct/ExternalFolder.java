package com.pinecone.hydra.storage.file.direct;

import com.pinecone.hydra.storage.file.entity.ElementNode;

import java.io.File;
import java.net.URI;

public interface ExternalFolder extends ElementNode {
    File getNativeFile();

    URI toURI();

    String getName();

    String getParentPath();

    String getPath();

    String[] list();

    File[]   listFiles();
}
