package com.pinecone.hydra.storage.file.transmit.exporter;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.hydra.storage.volume.UniformVolumeManager;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;

import java.io.IOException;
import java.sql.SQLException;

public interface Exporter extends Pinenut {
    void export(ExporterEntity entity, UniformVolumeManager volumeManager) throws IOException, SQLException;
    void resumablExport( ExporterEntity entity );
}
