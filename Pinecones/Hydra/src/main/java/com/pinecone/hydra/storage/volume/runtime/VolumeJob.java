package com.pinecone.hydra.storage.volume.runtime;

import com.pinecone.framework.system.functions.Executor;

import java.io.IOException;
import java.sql.SQLException;

public interface VolumeJob extends Executor {
    void execute() throws VolumeJobCompromiseException;
}
