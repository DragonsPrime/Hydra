package com.pinecone.hydra.storage.volume.entity.local.physical.export;

import com.pinecone.framework.system.prototype.Pinenut;

public interface ChannelBytesExportRecall extends Pinenut {
    void exportTo( Object toMe );
}
