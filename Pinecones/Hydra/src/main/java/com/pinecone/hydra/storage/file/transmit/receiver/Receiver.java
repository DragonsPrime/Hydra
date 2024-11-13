package com.pinecone.hydra.storage.file.transmit.receiver;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;

import java.io.IOException;
import java.sql.SQLException;

public interface Receiver extends Pinenut {
    void receive(ReceiveEntity entity, LogicVolume volume) throws IOException, SQLException;
    void receive( ReceiveEntity entity, Number offset,Number endSize ) throws IOException;
    void resumableReceive(ReceiveEntity entity ) throws IOException;
}
