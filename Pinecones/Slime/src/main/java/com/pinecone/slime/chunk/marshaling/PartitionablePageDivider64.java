package com.pinecone.slime.chunk.marshaling;

import com.pinecone.slime.chunk.scheduler.FixedPageDivider64;
import com.pinecone.slime.chunk.scheduler.PageDivider;
import com.pinecone.slime.cluster.SequentialChunkGroup;
import com.pinecone.slime.chunk.Chunk;
import com.pinecone.slime.chunk.Splitunk;
import com.pinecone.slime.chunk.scheduler.FixedChunkDivider64;
import com.pinecone.slime.chunk.scheduler.PagePool;

public class PartitionablePageDivider64 extends PartitionableChunkDivider64 implements PageDivider {
    protected PagePool                  mPagePool;
    protected long                      mnPageIdOffset;

    public PartitionablePageDivider64(Splitunk masterChunk, PagePool pagePool, SequentialChunkGroup chunkGroup, long pageIdOffset ) {
        super( masterChunk, chunkGroup );
        this.mPagePool        = pagePool;
        this.mnPageIdOffset   = pageIdOffset;

        this.preparePartitionsOwnedDivider();
    }

    public PartitionablePageDivider64( Splitunk masterChunk, PagePool pagePool, SequentialChunkGroup chunkGroup ) {
        this( masterChunk, pagePool, chunkGroup, 0 );
    }

    @Override
    protected Chunk newChunk( long start, long end, long epoch ) {
        return this.mPagePool.allocate( start, end, this.mnPageIdOffset + this.mnCurrentEpoch, this.mMasterChunk );
    }

    @Override
    protected FixedChunkDivider64 newDivider( Splitunk masterChunk, long each ) {
        return new FixedPageDivider64( masterChunk, this.getPagePool(), each );
    }

    @Override
    public PagePool getPagePool() {
        return this.mPagePool;
    }

    @Override
    public long getPageIdOffset() {
        return this.mnPageIdOffset;
    }

    @Override
    public void setPageIdOffset( long offset ) {
        this.mnPageIdOffset = offset;
    }
}
