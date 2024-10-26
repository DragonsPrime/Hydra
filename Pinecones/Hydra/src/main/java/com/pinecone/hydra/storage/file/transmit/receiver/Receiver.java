package com.pinecone.hydra.storage.file.transmit.receiver;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;

import java.io.IOException;

public interface Receiver extends Pinenut {
    void receive( ReceiveEntity entity ) throws IOException;
    void receive( ReceiveEntity entity, long offset,long endSize ) throws IOException;
    void receive(ReceiveEntity entity, GUID frameGuid, int threadId , int threadNum) throws IOException;
    void resumableReceive(ReceiveEntity entity ) throws IOException;
}
