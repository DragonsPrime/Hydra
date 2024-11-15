package com.pinecone.hydra.storage.volume.entity.local.striped;

public enum BufferToFileStatus implements StripBufferStatus{
    Writing                  ,
    Suspended                ,
    Exiting                  ;
}
