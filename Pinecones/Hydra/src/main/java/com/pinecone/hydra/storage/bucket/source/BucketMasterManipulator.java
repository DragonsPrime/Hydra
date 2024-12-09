package com.pinecone.hydra.storage.bucket.source;

import com.pinecone.hydra.system.ko.driver.KOIMasterManipulator;

public interface BucketMasterManipulator extends KOIMasterManipulator {
    BucketManipulator getBucketManipulator();
}
