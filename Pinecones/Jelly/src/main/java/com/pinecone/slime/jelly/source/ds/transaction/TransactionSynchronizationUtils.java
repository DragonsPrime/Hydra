package com.pinecone.slime.jelly.source.ds.transaction;

import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pinecone.framework.system.Nullable;
import com.pinecone.framework.util.Assert;
import com.pinecone.framework.util.ClassUtils;
import com.pinecone.framework.system.aop.InfrastructureProxy;
import com.pinecone.framework.system.aop.ScopedObject;

public final class TransactionSynchronizationUtils {
    private static final Log           logger = LogFactory.getLog( TransactionSynchronizationUtils.class );
    private static final boolean aopAvailable = ClassUtils.isPresent("com.pinecone.framework.system.aop.ScopedObject", TransactionSynchronizationUtils.class.getClassLoader());

    public TransactionSynchronizationUtils() {
    }

    public static boolean sameResourceFactory( ResourceTransactionManager tm, Object resourceFactory ) {
        return unwrapResourceIfNecessary( tm.getResourceFactory()).equals(unwrapResourceIfNecessary(resourceFactory) );
    }

    static Object unwrapResourceIfNecessary( Object resource ) {
        Assert.notNull( resource, "Resource must not be null" );
        Object resourceRef = resource;
        if ( resource instanceof InfrastructureProxy ) {
            resourceRef = ((InfrastructureProxy)resource).getWrappedObject();
        }

        if ( TransactionSynchronizationUtils.aopAvailable ) {
            resourceRef = TransactionSynchronizationUtils.ScopedProxyUnwrapper.unwrapIfNecessary( resourceRef );
        }

        return resourceRef;
    }

    public static void triggerFlush() {
        Iterator iter = TransactionSynchronizationManager.getSynchronizations().iterator();

        while( iter.hasNext() ) {
            TransactionSynchronization synchronization = (TransactionSynchronization)iter.next();
            synchronization.flush();
        }

    }

    public static void triggerBeforeCommit(boolean readOnly) {
        Iterator iter = TransactionSynchronizationManager.getSynchronizations().iterator();

        while( iter.hasNext() ) {
            TransactionSynchronization synchronization = (TransactionSynchronization)iter.next();
            synchronization.beforeCommit(readOnly);
        }

    }

    public static void triggerBeforeCompletion() {
        Iterator iter = TransactionSynchronizationManager.getSynchronizations().iterator();

        while( iter.hasNext() ) {
            TransactionSynchronization synchronization = (TransactionSynchronization)iter.next();

            try {
                synchronization.beforeCompletion();
            }
            catch ( Throwable e ) {
                logger.debug( "TransactionSynchronization.beforeCompletion threw exception", e );
            }
        }

    }

    public static void triggerAfterCommit() {
        invokeAfterCommit(TransactionSynchronizationManager.getSynchronizations());
    }

    public static void invokeAfterCommit( @Nullable List<TransactionSynchronization> synchronizations ) {
        if ( synchronizations != null ) {
            Iterator iter = synchronizations.iterator();

            while( iter.hasNext() ) {
                TransactionSynchronization synchronization = (TransactionSynchronization)iter.next();
                synchronization.afterCommit();
            }
        }

    }

    public static void triggerAfterCompletion( int completionStatus ) {
        List<TransactionSynchronization> synchronizations = TransactionSynchronizationManager.getSynchronizations();
        TransactionSynchronizationUtils.invokeAfterCompletion(synchronizations, completionStatus);
    }

    public static void invokeAfterCompletion( @Nullable List<TransactionSynchronization> synchronizations, int completionStatus ) {
        if ( synchronizations != null ) {
            Iterator iter = synchronizations.iterator();

            while( iter.hasNext() ) {
                TransactionSynchronization synchronization = (TransactionSynchronization)iter.next();

                try {
                    synchronization.afterCompletion(completionStatus);
                }
                catch ( Throwable e ) {
                    logger.debug("TransactionSynchronization.afterCompletion threw exception", e);
                }
            }
        }

    }

    private static class ScopedProxyUnwrapper {
        private ScopedProxyUnwrapper() {
        }

        public static Object unwrapIfNecessary(Object resource) {
            return resource instanceof ScopedObject ? ((ScopedObject)resource).getTargetObject() : resource;
        }
    }
}
