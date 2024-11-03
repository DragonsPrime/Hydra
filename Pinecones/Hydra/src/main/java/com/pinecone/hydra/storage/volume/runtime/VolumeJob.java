package com.pinecone.hydra.storage.volume.runtime;

import com.pinecone.framework.system.functions.Executor;

public interface VolumeJob extends Executor {
    void execute();
}
