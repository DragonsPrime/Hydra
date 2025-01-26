package com.pinecone.hydra.storage.file.builder;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.hydra.storage.file.KOMFileSystem;

public interface UOFSBuilder extends Pinenut {
    KOMFileSystem build( Feature ...features );

    KOMFileSystem build( long featureValues );

    KOMFileSystem buildByRegistered();


    UOFSBuilder registerComponentor( UOFSComponentor componentor );

 }
