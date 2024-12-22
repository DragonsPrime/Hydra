package com.pinecone.hydra.storage.bucket;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.bucket.entity.Bucket;
import com.pinecone.hydra.storage.bucket.entity.Site;
import com.pinecone.hydra.storage.bucket.source.SiteManipulator;

import java.util.List;

public interface BucketInstrument extends Pinenut {
    GUID createBucket(Bucket bucket);

    void removeBucket( GUID bucketGuid );

    void removeBucketByAccountAndBucketName( GUID accountGuid, String bucketName );

    Bucket queryBucketByBucketGuid( GUID bucketGuid );

    List<Bucket> queryBucketsByUserGuid( GUID userGuid );

    SiteManipulator getSiteManipulator();

    GUID createSite(Site site);

    void removeSite( GUID siteGuid );

    void removeSite( String siteName );

    Site querySite( GUID siteGuid );

    List<Site> listSite();
}
