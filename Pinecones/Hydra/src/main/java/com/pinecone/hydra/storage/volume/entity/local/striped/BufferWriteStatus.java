package com.pinecone.hydra.storage.volume.entity.local.striped;

public enum BufferWriteStatus {
    Writing                  ,
    Suspended                ,
    Mastering                ,
    Exiting                  ;
}
