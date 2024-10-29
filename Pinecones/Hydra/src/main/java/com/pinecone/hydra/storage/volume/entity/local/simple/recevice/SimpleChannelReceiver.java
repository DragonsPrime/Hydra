package com.pinecone.hydra.storage.volume.entity.local.simple.recevice;

import com.pinecone.hydra.storage.MiddleStorageObject;


import java.io.IOException;

public interface SimpleChannelReceiver extends SimpleReceiver {
    MiddleStorageObject receive( ) throws IOException;
    MiddleStorageObject receive( Number offset, Number endSize) throws IOException;
}
