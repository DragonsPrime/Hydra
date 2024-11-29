package com.pinecone.hydra.storage.file.transmit.receiver;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public interface FileReceive extends Pinenut {
    void receive(LogicVolume volume) throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException;
    void receive(LogicVolume volume, Number offset, Number endSize ) throws IOException;
}
