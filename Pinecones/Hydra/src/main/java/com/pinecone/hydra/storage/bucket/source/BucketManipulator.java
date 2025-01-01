package com.pinecone.hydra.storage.bucket.source;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.bucket.entity.Bucket;

import java.util.List;

public interface BucketManipulator extends Pinenut {
    void insert( Bucket bucket );
    void remove( GUID bucketGuid );

    void removeByAccountAndBucketName( GUID accountGuid, String bucketName );
    Bucket queryBucketByBucketGuid( GUID bucketGuid );
    List<Bucket> queryBucketsByUserGuid( GUID userGuid );
}
