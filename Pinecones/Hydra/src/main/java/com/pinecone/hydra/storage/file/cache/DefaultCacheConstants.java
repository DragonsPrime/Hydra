package com.pinecone.hydra.storage.file.cache;

import java.util.concurrent.TimeUnit;

public final class DefaultCacheConstants {

    public static final String FilePathCacheNS = "FILE_PATH_CACHE_NS_";

    public static final long PathQueryExpiryTimeHotMil = TimeUnit.HOURS.toMillis( 4 );

}
