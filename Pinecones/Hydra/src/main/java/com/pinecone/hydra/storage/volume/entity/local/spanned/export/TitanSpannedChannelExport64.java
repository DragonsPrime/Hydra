package com.pinecone.hydra.storage.volume.entity.local.spanned.export;

import com.pinecone.hydra.storage.MiddleStorageObject;
import com.pinecone.hydra.storage.volume.VolumeTree;
import com.pinecone.hydra.storage.volume.entity.ExportStorageObject;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.entity.SpannedVolume;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.List;

public class TitanSpannedChannelExport64 implements SpannedChannelExport64{
    private VolumeTree              volumeTree;
    private ExportStorageObject     exportStorageObject;
    private FileChannel             channel;
    private SpannedVolume           spannedVolume;

    public TitanSpannedChannelExport64(SpannedChannelExportEntity entity , SpannedVolume spannedVolume){
        this.volumeTree          =  entity.getVolumeTree();
        this.exportStorageObject =  entity.getExportStorageObject();
        this.channel             =  entity.getChannel();
        this.spannedVolume       =  spannedVolume;
    }

    @Override
    public MiddleStorageObject export() throws IOException, SQLException {
        List<LogicVolume> volumes = this.spannedVolume.getChildren();
        for( LogicVolume volume : volumes ){
            if( volume.existStorageObject( exportStorageObject.getStorageObjectGuid() ) ){
                return volume.channelExport( exportStorageObject, channel );
            }
        }
        return null;
    }
}
