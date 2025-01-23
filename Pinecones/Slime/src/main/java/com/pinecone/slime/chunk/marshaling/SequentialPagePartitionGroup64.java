package com.pinecone.slime.chunk.marshaling;

import com.pinecone.slime.cluster.ArchSequentialChunkGroup;
import com.pinecone.slime.chunk.ArchPatriarchalChunk;
import com.pinecone.slime.chunk.PatriarchalChunk;


public class SequentialPagePartitionGroup64 extends ArchSequentialChunkGroup implements PatriarchalChunk, SequentialPagePartitionGroup {
    protected ArchPatriarchalChunk mParent;

    public SequentialPagePartitionGroup64() {
        super();
    }

    @Override
    public PatriarchalChunk parent() {
        return this.mParent;
    }

    @Override
    public void setParent( PatriarchalChunk parent ){
        this.mParent = (ArchPatriarchalChunk) parent;
    }

    @Override
    public PagePartitionGroup getFirstChunkById( long id ){
        return (PagePartitionGroup) super.getFirstChunkById( id );
    }

    @Override
    public boolean hasOwnPartition( PagePartition that ) {
        return this.mChunkRegister.containsKey( that.getId() );
    }
}
