package com.pinecone.hydra.storage.volume.entity.local.striped;

import com.pinecone.framework.system.executum.Processum;
import com.pinecone.hydra.storage.volume.runtime.ArchStripedTaskThread;
import com.pinecone.hydra.storage.volume.runtime.VolumeJob;

public class LocalStripedTaskThread extends ArchStripedTaskThread {
    public LocalStripedTaskThread ( String szName, Processum parent, VolumeJob volumeJob ) {
        super( szName, parent, volumeJob );

        if( volumeJob instanceof TitanStripChannelBufferToFileJob ) {
            ( (TitanStripChannelBufferToFileJob)volumeJob ).applyTaskThread( this );
        }
        else if( volumeJob instanceof TitanStripChannelBufferWriteJob ) {
            ( (TitanStripChannelBufferWriteJob)volumeJob ).applyTaskThread( this );
        }
    }

}
