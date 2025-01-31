package com.pinecone.hydra.storage.file.direct;

import com.pinecone.hydra.storage.UFile;
import com.pinecone.hydra.storage.file.entity.ElementNode;

import java.io.File;
import java.net.URI;
import java.net.URL;

public interface ExternalFile extends ElementNode, UFile {
    File getNativeFile();

    URI toURI();

    String getName();

    String getParentPath();

    String getPath();
}
