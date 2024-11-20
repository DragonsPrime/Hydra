package com.pinecone.hydra.storage.file.transmit.exporter.channel;


import com.pinecone.framework.util.json.JSON;
import com.pinecone.hydra.storage.TitanStorageExportExportIORequest;
import com.pinecone.hydra.storage.file.entity.FileNode;
import com.pinecone.hydra.storage.file.entity.Frame;
import com.pinecone.hydra.storage.file.entity.LocalFrame;
import com.pinecone.hydra.storage.file.transmit.UniformSourceLocator;
import com.pinecone.hydra.storage.file.transmit.exporter.ArchExporter;
import com.pinecone.hydra.storage.file.transmit.exporter.ExporterEntity;
import com.pinecone.hydra.storage.volume.UniformVolumeManager;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.ulf.util.id.GUIDs;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.TreeMap;

public class GenericChannelExporter extends ArchExporter implements ChannelExporter{
    @Override
    public void export(ExporterEntity entity, UniformVolumeManager volumeManager) throws IOException, SQLException {
        ChannelExporterEntity exporter = entity.evinceChannelExporterEntity();
        FileChannel fileChannel = exporter.getChannel();
        FileNode file = exporter.getFile();

        // 获取文件所有的簇
        TreeMap<Long, Frame> framesMap = file.getFrames();
        for (long i = 0; i < framesMap.size(); i++) {
            LocalFrame frame = (LocalFrame) framesMap.get(i);
            TitanStorageExportExportIORequest titanExportStorageObject = new TitanStorageExportExportIORequest();
            titanExportStorageObject.setSize( frame.getSize() );
            titanExportStorageObject.setStorageObjectGuid( frame.getSegGuid() );
            String sourceName = frame.getSourceName();
            UniformSourceLocator uniformSourceLocator = JSON.unmarshal(sourceName, UniformSourceLocator.class);
            titanExportStorageObject.setSourceName( uniformSourceLocator.getSourceName() );
            LogicVolume volume = volumeManager.get(GUIDs.GUID72(uniformSourceLocator.getVolumeGuid()));
            volume.channelExport( titanExportStorageObject, fileChannel );
        }
    }

    @Override
    public void resumablExport(ExporterEntity entity) {

    }

}
