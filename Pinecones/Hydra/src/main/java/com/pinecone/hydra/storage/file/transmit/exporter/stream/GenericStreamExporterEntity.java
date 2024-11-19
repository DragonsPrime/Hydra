package com.pinecone.hydra.storage.file.transmit.exporter.stream;

import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.entity.FileNode;
import com.pinecone.hydra.storage.file.transmit.exporter.ArchExporterEntity;
import com.pinecone.hydra.storage.volume.UniformVolumeManager;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;

public class GenericStreamExporterEntity extends ArchExporterEntity implements StreamExporterEntity {
    private OutputStream    outputStream;
    private StreamExporter  streamExporter;
    public GenericStreamExporterEntity(KOMFileSystem fileSystem, FileNode file, OutputStream outputStream) {
        super(fileSystem, file);
        this.outputStream = outputStream;
        this.streamExporter = new GenericStreamExporter();
    }

    @Override
    public OutputStream getOutputStream() {
        return this.outputStream;
    }

    @Override
    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void export(UniformVolumeManager volumeManager) throws IOException, SQLException {
        this.streamExporter.export(this, volumeManager);
    }
}
