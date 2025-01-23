package com.pinecone.slime.chunk.marshaling;

import com.pinecone.slime.chunk.scheduler.ArchMasterSplitunkDivider64;
import com.pinecone.slime.chunk.scheduler.BadAllocateException;
import com.pinecone.slime.chunk.Chunk;
import com.pinecone.slime.chunk.Splitunk;
import com.pinecone.slime.chunk.scheduler.ChunkDivider;
import com.pinecone.slime.chunk.scheduler.FixedChunkDivider64;
import com.pinecone.slime.cluster.SequentialChunkGroup;

import java.util.ArrayList;
import java.util.List;

public abstract class PartitionableChunkDivider64 extends ArchMasterSplitunkDivider64 implements ChunkDivider {
    protected SequentialChunkGroup         mChunkGroup;
    protected List<FixedChunkDivider64>   mPartitionsOwnedDivider;
    protected int                          mnCurrentPartDivider;

    protected long                         mnCurrentEpoch;
    protected long                         mnMaxAllocations;

    public PartitionableChunkDivider64( Splitunk masterChunk, SequentialChunkGroup chunkGroup ) {
        super( masterChunk );

        this.mnCurrentEpoch          = 0;
        this.mChunkGroup             = chunkGroup;
        this.mnCurrentPartDivider    = 0;
    }

    protected abstract FixedChunkDivider64 newDivider( Splitunk masterChunk, long each );

    protected void preparePartitionsOwnedDivider() {
        this.mPartitionsOwnedDivider = new ArrayList<>();
        List<Chunk> chunks = (List<Chunk >) this.mChunkGroup.getSequentialChunks();
        for ( int i = 0; i < chunks.size(); ++i ) {
            PreparedPageDividerPartition64 partition = (PreparedPageDividerPartition64)chunks.get( i );
            FixedChunkDivider64 divider = this.newDivider(
                    partition, partition.eachPerPage()
            );

            this.mPartitionsOwnedDivider.add( divider );
            this.mnMaxAllocations += divider.getMaxAllocations();
        }
    }

    @Override
    public long getMaxAllocations() {
        return this.mnMaxAllocations;
    }

    @Override
    public long remainAllocatable(){
        return this.mnMaxAllocations - this.mnCurrentEpoch;
    }

    @Override
    public Chunk allocate() throws BadAllocateException {
        if( this.mnCurrentEpoch < this.getMaxAllocations() ) {
            FixedChunkDivider64 divider = this.mPartitionsOwnedDivider.get( this.mnCurrentPartDivider );
            if( divider.remainAllocatable() == 0 ){
                ++this.mnCurrentPartDivider;
                divider = this.mPartitionsOwnedDivider.get( this.mnCurrentPartDivider );
            }

            Chunk chunk = divider.allocate();
            chunk.setId( this.mnCurrentEpoch );

            ++this.mnCurrentEpoch;

            return chunk;
        }

        throw new BadAllocateException();
    }
}
