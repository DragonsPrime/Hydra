package com.pinecone.slime.chunk.marshaling;

import com.pinecone.slime.chunk.ContiguousPage;
import com.pinecone.slime.cluster.RangedCluster;

public interface PageCluster extends RangedCluster {
    boolean hasOwnPage( ContiguousPage that );
}
