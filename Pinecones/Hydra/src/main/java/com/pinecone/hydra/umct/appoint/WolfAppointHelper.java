package com.pinecone.hydra.umct.appoint;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.pinecone.hydra.umct.IlleagalResponseException;

public final class WolfAppointHelper {
    public static Object evalCompletableFuture( CompletableFuture<Object> future, long nWaitTimeMil ) throws IlleagalResponseException, TimeoutException, ExecutionException, InterruptedException {
        Object ret;

        if ( nWaitTimeMil != -1 ) {
            ret = future.get( nWaitTimeMil, TimeUnit.MILLISECONDS );
        }
        else {
            ret = future.get();
        }

        if ( ret instanceof Exception ) {
            throw new IlleagalResponseException( (Exception)ret );
        }

        return ret;
    }
}
