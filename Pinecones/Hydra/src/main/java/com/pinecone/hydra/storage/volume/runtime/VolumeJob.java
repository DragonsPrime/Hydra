package com.pinecone.hydra.storage.volume.runtime;

import com.pinecone.framework.system.functions.Executor;
import com.pinecone.hydra.storage.volume.entity.local.striped.LocalStripedTaskThread;
import com.pinecone.hydra.storage.volume.entity.local.striped.StripBufferStatus;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.Semaphore;

public interface VolumeJob extends Executor {
    void execute() throws VolumeJobCompromiseException;

    void applyThread( LocalStripedTaskThread thread );

    StripBufferStatus getStatus();

    Semaphore getPipelineLock();
    void setStatus( StripBufferStatus status );
}
