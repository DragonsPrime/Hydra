package com.pinecone.slime.chunk.marshaling;

import com.pinecone.slime.chunk.RangedChunk64;
import com.pinecone.slime.chunk.Splitunk;

public class PreparedEvenSeqPagePartitioner64 extends EvenSeqChunkPartitioner64 {
    public PreparedEvenSeqPagePartitioner64( Splitunk masterChunk, long nGroups ) {
        super( masterChunk, nGroups );
    }

    @Override
    protected PreparedPageDividerPartition64 newCluster( long nMin, long nMax, long id ) {
        return new PreparedPageDividerPartition64( nMin, nMax, id, 1, (RangedChunk64) this.getMasterChunk() );
    }

    @Override
    protected SequentialPagePartitionGroup64 newGroup() {
        return new SequentialPagePartitionGroup64();
    }

    @Override
    public SequentialPagePartitionGroup64 partition() {
        return (SequentialPagePartitionGroup64)super.partition();
    }
}
