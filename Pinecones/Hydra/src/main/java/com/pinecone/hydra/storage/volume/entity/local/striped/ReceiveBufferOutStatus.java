package com.pinecone.hydra.storage.volume.entity.local.striped;

public enum ReceiveBufferOutStatus implements StripBufferStatus{
    Writing                  ,
    Suspended                ,
    Exiting                  ;
}
