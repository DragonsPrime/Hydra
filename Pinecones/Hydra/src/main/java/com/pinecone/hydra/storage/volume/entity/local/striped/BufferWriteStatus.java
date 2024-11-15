package com.pinecone.hydra.storage.volume.entity.local.striped;

public enum BufferWriteStatus implements StripBufferStatus{
    Writing                  ,
    Suspended                ,
    Synchronization          ,
    Exiting                  ;
}
