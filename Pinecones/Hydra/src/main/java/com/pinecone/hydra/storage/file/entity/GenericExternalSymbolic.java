package com.pinecone.hydra.storage.file.entity;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.id.GuidAllocator;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.source.ExternalSymbolicManipulator;

import java.time.LocalDateTime;

public class GenericExternalSymbolic extends ArcReparseSemanticNode implements ExternalSymbolic {
    private SymbolicMeta                    symbolicMeta;

    private ExternalSymbolicManipulator     externalSymbolicManipulator;


    public GenericExternalSymbolic() {
        super();
    }

    public GenericExternalSymbolic( KOMFileSystem fileSystem ) {
        super( fileSystem );
    }

    @Override
    public SymbolicMeta getSymbolicMeta() {
        return this.symbolicMeta;
    }

    @Override
    public void setSymbolicMeta(SymbolicMeta symbolicMeta) {
        this.symbolicMeta = symbolicMeta;
    }

    @Override
    public void create() {
        this.externalSymbolicManipulator.insert( this );
    }

    @Override
    public void remove() {
        this.externalSymbolicManipulator.remove( this.guid );
        this.symbolicMeta.remove();
    }

    @Override
    public void apply(ExternalSymbolicManipulator externalSymbolicManipulator) {
        this.externalSymbolicManipulator = externalSymbolicManipulator;
    }
}
