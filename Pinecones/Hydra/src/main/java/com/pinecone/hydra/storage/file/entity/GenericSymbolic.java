package com.pinecone.hydra.storage.file.entity;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.file.source.SymbolicManipulator;

import java.time.LocalDateTime;

public class GenericSymbolic extends ArcReparseSemanticNode implements Symbolic{
    private SymbolicMeta            symbolicMeta;
    private SymbolicManipulator     symbolicManipulator;

    public GenericSymbolic( SymbolicManipulator symbolicManipulator ) {
        this.symbolicManipulator = symbolicManipulator;
    }



    public SymbolicMeta getSymbolicMeta() {
        return symbolicMeta;
    }


    public void setSymbolicMeta(SymbolicMeta symbolicMeta) {
        this.symbolicMeta = symbolicMeta;
    }

    @Override
    public void create() {
        this.symbolicManipulator.insert(this);
    }

    @Override
    public void remove() {
        this.symbolicManipulator.remove(this.guid);
        this.symbolicMeta.remove();
    }

}
