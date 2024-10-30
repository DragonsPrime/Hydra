package com.pinecone.hydra.storage.volume.entity.local.spanned.receive;

import com.pinecone.hydra.storage.MiddleStorageObject;
import com.pinecone.hydra.storage.volume.entity.local.simple.recevice.SimpleChannelReceiverEntity;

import java.io.IOException;

public interface SpannedChannelReceive extends SpannedReceive{
    MiddleStorageObject receive(SimpleChannelReceiverEntity entity ) throws IOException;
    MiddleStorageObject receive( SimpleChannelReceiverEntity entity, Number offset, Number endSize) throws IOException;
}
