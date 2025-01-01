package com.pinecone.hydra.storage.file.transmit.exporter;

import com.pinecone.framework.system.prototype.Pinenut;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public interface FileExport extends Pinenut {
    void export() throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException;
}
