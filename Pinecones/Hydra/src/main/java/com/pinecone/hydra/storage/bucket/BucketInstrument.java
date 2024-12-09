package com.pinecone.hydra.storage.bucket;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.bucket.entity.Bucket;

import java.util.List;

public interface BucketInstrument extends Pinenut {
    GUID createBucket(Bucket bucket);

    void removeBucket( GUID bucketGuid );

    Bucket queryBucketByBucketGuid( GUID bucketGuid );

    List<Bucket> queryBucketsByUserGuid( GUID userGuid );
}
