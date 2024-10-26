package com.pinecone.hydra.storage.volume.entity.local;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.entity.FileNode;
import com.pinecone.hydra.storage.volume.entity.SimpleVolume;

import java.io.IOException;
import java.nio.channels.FileChannel;

public interface LocalSimpleVolume extends SimpleVolume {
    void channelReceive(KOMFileSystem fileSystem, FileNode file, GUID frameGuid, int threadNum, int threadId) throws IOException;
}
