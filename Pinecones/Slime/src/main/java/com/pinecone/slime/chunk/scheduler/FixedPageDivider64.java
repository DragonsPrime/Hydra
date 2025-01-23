package com.pinecone.slime.chunk.scheduler;

import com.pinecone.slime.chunk.Chunk;
import com.pinecone.slime.chunk.Splitunk;
import com.pinecone.slime.chunk.marshaling.PageDividerPartition64;

public class FixedPageDivider64 extends FixedChunkDivider64 implements PageDivider {
    protected PagePool                  mPagePool;
    protected long                      mnPageIdOffset;

    public FixedPageDivider64( Splitunk masterChunk, PagePool pagePool, long each, long pageIdOffset ) {
        super( masterChunk, each );
        this.mPagePool        = pagePool;
        this.mnPageIdOffset   = pageIdOffset;
    }

    public FixedPageDivider64( Splitunk masterChunk, PagePool pagePool, long each ) {
        this( masterChunk, pagePool, each, 0 );
    }

    public FixedPageDivider64( PageDividerPartition64 partition, PagePool pagePool, long pageIdOffset ) {
        this( partition, pagePool, partition.eachPerPage(), pageIdOffset );
    }

    public FixedPageDivider64( PageDividerPartition64 partition, PagePool pagePool ) {
        this( partition, pagePool, 0 );
    }

    @Override
    protected Chunk newChunk( long start, long end, long epoch ) {
        return this.mPagePool.allocate( start, end, this.mnPageIdOffset + this.mnCurrentEpoch, this.mMasterChunk );
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
