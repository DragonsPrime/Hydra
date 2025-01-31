package com.pinecone.hydra.storage.file.transmit.exporter;

import com.pinecone.hydra.storage.io.Chanface;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.entity.FileNode;
import com.pinecone.hydra.storage.volume.VolumeManager;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public class TitanFileExportEntity64 extends ArchFileExporterEntity  implements FileExportEntity64{
    protected FileExport64      fileExport;
    public TitanFileExportEntity64(KOMFileSystem fileSystem, VolumeManager volumeManager, FileNode file, Chanface channel) {
        super(fileSystem, file, channel, volumeManager);
        this.fileExport  = new TitanFileExport64( this );
    }


    @Override
    public void export() throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        this.fileExport.export();
    }


}
