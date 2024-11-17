package com.pinecone.hydra.storage.volume.entity.local.striped;

public enum BufferOutStatus implements StripBufferStatus{
    Writing                  ,
    Suspended                ,
    Exiting                  ;
}
