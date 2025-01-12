package com.pinecone.slime.chunk.marshaling;

import com.pinecone.slime.cluster.SequentialChunkGroup;
import com.pinecone.slime.chunk.Chunk;

import java.util.List;

public class BuddyPrepPartitionDividerStrategy64 implements PartitionDividerStrategy {
    protected long mnMaxPerPage;
    protected int  mnBootstrapDivFactor;
    protected long mnMinThresholdPerPage;

    public BuddyPrepPartitionDividerStrategy64( long nMaxPerPage, int nBootstrapDivFactor, long nMinThresholdPerPage ) {
        this.mnMaxPerPage          = nMaxPerPage;
        this.mnBootstrapDivFactor  = nBootstrapDivFactor;
        this.mnMinThresholdPerPage = nMinThresholdPerPage;
    }

    public BuddyPrepPartitionDividerStrategy64( long nMaxPerPage, long nMinThresholdPerPage ) {
        this( nMaxPerPage, 2, nMinThresholdPerPage );
    }

    @Override
    public SequentialChunkGroup assignment(SequentialChunkGroup group ) {
        List<Chunk > chunks = (List<Chunk >) group.getSequentialChunks();
        long each = this.mnMaxPerPage;
        for ( int i = 0; i < chunks.size(); ++i ) {
            ( (PreparedPageDividerPartition64)chunks.get( i ) ).setEachPerPage( each );

            each = Math.max( this.mnMinThresholdPerPage , each / this.mnBootstrapDivFactor );
        }
        return group;
    }
}
