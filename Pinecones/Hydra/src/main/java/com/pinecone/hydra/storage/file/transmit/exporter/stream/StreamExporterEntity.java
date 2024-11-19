package com.pinecone.hydra.storage.file.transmit.exporter.stream;

import com.pinecone.hydra.storage.file.transmit.exporter.ExporterEntity;
import com.pinecone.hydra.storage.volume.UniformVolumeManager;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;

public interface StreamExporterEntity extends ExporterEntity {
    OutputStream getOutputStream();
    void setOutputStream( OutputStream outputStream);
    void export(UniformVolumeManager volumeManager) throws IOException, SQLException;

    @Override
    default StreamExporterEntity evinceStreamExporterEntity() {
        return this;
    }
}
