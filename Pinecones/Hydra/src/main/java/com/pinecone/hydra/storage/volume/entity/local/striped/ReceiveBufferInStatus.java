package com.pinecone.hydra.storage.volume.entity.local.striped;

public enum ReceiveBufferInStatus implements StripBufferStatus{
    Writing                  ,
    Suspended                ,
    Exiting                  ;

}
