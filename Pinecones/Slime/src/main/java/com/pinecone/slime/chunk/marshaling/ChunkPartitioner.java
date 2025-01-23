package com.pinecone.slime.chunk.marshaling;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.slime.chunk.DivisibleChunk;

public interface ChunkPartitioner extends Pinenut {
    DivisibleChunk getMasterChunk();
}
