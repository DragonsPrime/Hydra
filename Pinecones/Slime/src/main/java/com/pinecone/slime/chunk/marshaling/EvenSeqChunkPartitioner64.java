package com.pinecone.slime.chunk.marshaling;

import com.pinecone.slime.chunk.RangedPage64;
import com.pinecone.slime.chunk.scheduler.DirectPagePool;
import com.pinecone.slime.chunk.scheduler.FixedPageDivider64;
import com.pinecone.slime.cluster.Cluster;
import com.pinecone.slime.cluster.SequentialChunkGroup;
import com.pinecone.slime.unitization.NumPrecision;
import com.pinecone.slime.chunk.Chunk;
import com.pinecone.slime.chunk.Splitunk;

public abstract class EvenSeqChunkPartitioner64 extends ArchMasterSplitunkPartitioner64 {
    protected long                   mnGroups;
    protected long                   mnEach;
    protected FixedPageDivider64 mDivider;

    protected EvenSeqChunkPartitioner64( Splitunk masterChunk, long nGroups ) {
        super( masterChunk );

        this.mnGroups = nGroups;
        this.mnEach   = ((NumPrecision)this.getMasterChunk().size()).longValue() / this.mnGroups;
        this.mDivider = new FixedPageDivider64( this.getMasterChunk(), new DirectPagePool( RangedPage64.class ), this.mnEach );
    }

    protected abstract Cluster newCluster(long nMin, long nMax, long id );

    protected abstract SequentialChunkGroup newGroup();

    public SequentialChunkGroup partition() {
        SequentialChunkGroup group = this.newGroup();

        for ( long i = 0; i < this.mnGroups; i++ ) {
            Chunk c         = this.mDivider.allocate();
            RangedPage64 tp = (RangedPage64) c;

            group.add( this.newCluster( tp.getRange().getMin(), tp.getRange().getMax(), i ) );
        }

        return group;
    }
}
