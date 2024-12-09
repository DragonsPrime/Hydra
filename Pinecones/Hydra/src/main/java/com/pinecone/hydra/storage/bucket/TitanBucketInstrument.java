package com.pinecone.hydra.storage.bucket;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.bucket.entity.Bucket;
import com.pinecone.hydra.storage.bucket.source.BucketManipulator;
import com.pinecone.hydra.storage.bucket.source.BucketMasterManipulator;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.system.Hydrarum;
import com.pinecone.hydra.system.ko.driver.KOIMappingDriver;
import com.pinecone.hydra.system.ko.driver.KOIMasterManipulator;
import com.pinecone.ulf.util.id.GuidAllocator;
import com.pinecone.ulf.util.id.impl.GenericGuidAllocator;

import java.util.List;

public class TitanBucketInstrument implements BucketInstrument {
    protected Hydrarum                  hydrarum;

    protected BucketMasterManipulator   masterManipulator;

    protected BucketManipulator         bucketManipulator;

    protected GuidAllocator             guidAllocator;

    public TitanBucketInstrument(Hydrarum hydrarum, KOIMasterManipulator masterManipulator, String name ){
        this.hydrarum = hydrarum;
        this.masterManipulator = (BucketMasterManipulator) masterManipulator;
        this.guidAllocator     = new GenericGuidAllocator();

        this.bucketManipulator = this.masterManipulator.getBucketManipulator();
    }

    public TitanBucketInstrument(Hydrarum hydrarum, KOIMasterManipulator masterManipulator ){
        this( hydrarum, masterManipulator, KOMFileSystem.class.getSimpleName() );
    }

    public TitanBucketInstrument(KOIMappingDriver driver ) {
        this(
                driver.getSystem(),
                driver.getMasterManipulator()
        );
    }

    @Override
    public GUID createBucket(Bucket bucket) {
        GUID guid = this.guidAllocator.nextGUID72();
        bucket.setBucketGuid( guid );
        this.bucketManipulator.insert( bucket );
        return guid;
    }

    @Override
    public void removeBucket(GUID bucketGuid) {
        this.bucketManipulator.remove( bucketGuid );
    }

    @Override
    public Bucket queryBucketByBucketGuid(GUID bucketGuid) {
        return this.bucketManipulator.queryBucketByBucketGuid( bucketGuid );
    }

    @Override
    public List<Bucket> queryBucketsByUserGuid(GUID userGuid) {
        return this.bucketManipulator.queryBucketsByUserGuid( userGuid );
    }
}
