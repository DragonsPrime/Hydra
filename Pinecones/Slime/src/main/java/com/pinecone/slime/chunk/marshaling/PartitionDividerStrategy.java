package com.pinecone.slime.chunk.marshaling;

import com.pinecone.slime.cluster.SequentialChunkGroup;
import com.pinecone.framework.system.prototype.Pinenut;

public interface PartitionDividerStrategy extends Pinenut {
    SequentialChunkGroup assignment(SequentialChunkGroup group );
}
