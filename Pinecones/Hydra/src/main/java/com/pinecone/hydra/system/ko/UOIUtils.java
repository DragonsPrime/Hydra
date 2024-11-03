package com.pinecone.hydra.system.ko;

import com.pinecone.framework.util.uoi.UOI;
import com.pinecone.framework.util.uoi.UniformObjectLoaderFactory;

public final class UOIUtils {
    public static UOI createJavaClass( String className, String resourceDetail ) {
        return UOI.create(
                String.format( "%s://%s/%s", UniformObjectLoaderFactory.DefaultJavaClassType, resourceDetail, className )
        );
    }

    public static UOI createLocalJavaClass( String className ) {
        return UOIUtils.createJavaClass( className, "" );
    }
}
