package com.pinecone.hydra.storage;

import com.pinecone.framework.system.prototype.Pinenut;

import java.io.IOException;

public interface InputChannel extends Pinenut {
    void read( ReadChannelRecalled recaller ) throws IOException;
}
