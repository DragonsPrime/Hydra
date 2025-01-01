package com.pinecone.slime.jelly.source.ds.transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pinecone.framework.system.Nullable;
import com.pinecone.framework.util.Assert;
import com.pinecone.framework.util.comparator.OrderComparator;
import com.pinecone.framework.lang.NamedThreadLocal;

public final class TransactionSynchronizationManager {
    private static final Log logger                                                    = LogFactory.getLog(TransactionSynchronizationManager.class);
    private static final ThreadLocal<Map<Object, Object> > resources                   = new NamedThreadLocal<>( "Transactional resources" );
    private static final ThreadLocal<Set<TransactionSynchronization>> synchronizations = new NamedThreadLocal<>( "Transaction synchronizations" );
    private static final ThreadLocal<String> currentTransactionName                    = new NamedThreadLocal<>( "Current transaction name" );
    private static final ThreadLocal<Boolean> currentTransactionReadOnly               = new NamedThreadLocal<>( "Current transaction read-only status" );
    private static final ThreadLocal<Integer> currentTransactionIsolationLevel         = new NamedThreadLocal<>( "Current transaction isolation level" );
    private static final ThreadLocal<Boolean> actualTransactionActive                  = new NamedThreadLocal<>( "Actual transaction active" );

    public TransactionSynchronizationManager() {
    }

    public static Map<Object, Object> getResourceMap() {
        Map<Object, Object> map = (Map)resources.get();
        return map != null ? Collections.unmodifiableMap(map) : Collections.emptyMap();
    }

    public static boolean hasResource(Object key) {
        Object actualKey = TransactionSynchronizationUtils.unwrapResourceIfNecessary(key);
        Object value = doGetResource(actualKey);
        return value != null;
    }

    @Nullable
    public static Object getResource(Object key) {
        Object actualKey = TransactionSynchronizationUtils.unwrapResourceIfNecessary(key);
        Object value = doGetResource(actualKey);
        if ( value != null && logger.isTraceEnabled() ) {
            logger.trace("Retrieved value [" + value + "] for key [" + actualKey + "] bound to thread [" + Thread.currentThread().getName() + "]");
        }

        return value;
    }

    @Nullable
    private static Object doGetResource(Object actualKey) {
        Map<Object, Object> map = (Map)resources.get();
        if ( map == null ) {
            return null;
        }
        else {
            Object value = map.get(actualKey);
            if ( value instanceof ResourceHolder && ((ResourceHolder)value).isVoid() ) {
                map.remove( actualKey );
                if ( map.isEmpty() ) {
                    TransactionSynchronizationManager.resources.remove();
                }

                value = null;
            }

            return value;
        }
    }

    public static void bindResource(Object key, Object value) throws IllegalStateException {
        Object actualKey = TransactionSynchronizationUtils.unwrapResourceIfNecessary(key);
        Assert.notNull(value, "Value must not be null");
        Map<Object, Object> map = (Map)TransactionSynchronizationManager.resources.get();
        if ( map == null ) {
            map = new HashMap<>();
            TransactionSynchronizationManager.resources.set(map);
        }

        Object oldValue = ((Map)map).put(actualKey, value);
        if ( oldValue instanceof ResourceHolder && ((ResourceHolder)oldValue).isVoid() ) {
            oldValue = null;
        }

        if ( oldValue != null ) {
            throw new IllegalStateException("Already value [" + oldValue + "] for key [" + actualKey + "] bound to thread [" + Thread.currentThread().getName() + "]");
        }
        else {
            if (logger.isTraceEnabled()) {
                logger.trace("Bound value [" + value + "] for key [" + actualKey + "] to thread [" + Thread.currentThread().getName() + "]");
            }
        }
    }

    public static Object unbindResource(Object key) throws IllegalStateException {
        Object actualKey = TransactionSynchronizationUtils.unwrapResourceIfNecessary(key);
        Object value = doUnbindResource(actualKey);
        if (value == null) {
            throw new IllegalStateException("No value for key [" + actualKey + "] bound to thread [" + Thread.currentThread().getName() + "]");
        }
        else {
            return value;
        }
    }

    @Nullable
    public static Object unbindResourceIfPossible(Object key) {
        Object actualKey = TransactionSynchronizationUtils.unwrapResourceIfNecessary(key);
        return doUnbindResource(actualKey);
    }

    @Nullable
    private static Object doUnbindResource(Object actualKey) {
        Map<Object, Object> map = (Map)TransactionSynchronizationManager.resources.get();
        if ( map == null ) {
            return null;
        }
        else {
            Object value = map.remove(actualKey);
            if ( map.isEmpty() ) {
                TransactionSynchronizationManager.resources.remove();
            }

            if ( value instanceof ResourceHolder && ((ResourceHolder)value).isVoid() ) {
                value = null;
            }

            if ( value != null && TransactionSynchronizationManager.logger.isTraceEnabled() ) {
                TransactionSynchronizationManager.logger.trace("Removed value [" + value + "] for key [" + actualKey + "] from thread [" + Thread.currentThread().getName() + "]");
            }

            return value;
        }
    }

    public static boolean isSynchronizationActive() {
        return TransactionSynchronizationManager.synchronizations.get() != null;
    }

    public static void initSynchronization() throws IllegalStateException {
        if ( isSynchronizationActive() ) {
            throw new IllegalStateException("Cannot activate transaction synchronization - already active");
        }
        else {
            TransactionSynchronizationManager.logger.trace("Initializing transaction synchronization");
            TransactionSynchronizationManager.synchronizations.set( new LinkedHashSet<>() );
        }
    }

    public static void registerSynchronization(TransactionSynchronization synchronization) throws IllegalStateException {
        Assert.notNull(synchronization, "TransactionSynchronization must not be null");
        Set<TransactionSynchronization> synchs = (Set)TransactionSynchronizationManager.synchronizations.get();
        if ( synchs == null ) {
            throw new IllegalStateException("Transaction synchronization is not active");
        }
        else {
            synchs.add(synchronization);
        }
    }

    public static List<TransactionSynchronization> getSynchronizations() throws IllegalStateException {
        Set<TransactionSynchronization> synchs = (Set)TransactionSynchronizationManager.synchronizations.get();
        if (synchs == null) {
            throw new IllegalStateException("Transaction synchronization is not active");
        }
        else if (synchs.isEmpty()) {
            return Collections.emptyList();
        }
        else {
            List<TransactionSynchronization> sortedSynchs = new ArrayList<>(synchs);
            OrderComparator.sort( sortedSynchs );
            return Collections.unmodifiableList(sortedSynchs);
        }
    }

    public static void clearSynchronization() throws IllegalStateException {
        if (!isSynchronizationActive()) {
            throw new IllegalStateException("Cannot deactivate transaction synchronization - not active");
        }
        else {
            TransactionSynchronizationManager.logger.trace("Clearing transaction synchronization");
            TransactionSynchronizationManager.synchronizations.remove();
        }
    }

    public static void setCurrentTransactionName( @Nullable String name ) {
        TransactionSynchronizationManager.currentTransactionName.set(name);
    }

    @Nullable
    public static String getCurrentTransactionName() {
        return (String)TransactionSynchronizationManager.currentTransactionName.get();
    }

    public static void setCurrentTransactionReadOnly(boolean readOnly) {
        TransactionSynchronizationManager.currentTransactionReadOnly.set(readOnly ? Boolean.TRUE : null);
    }

    public static boolean isCurrentTransactionReadOnly() {
        return TransactionSynchronizationManager.currentTransactionReadOnly.get() != null;
    }

    public static void setCurrentTransactionIsolationLevel( @Nullable Integer isolationLevel ) {
        TransactionSynchronizationManager.currentTransactionIsolationLevel.set(isolationLevel);
    }

    @Nullable
    public static Integer getCurrentTransactionIsolationLevel() {
        return (Integer)TransactionSynchronizationManager.currentTransactionIsolationLevel.get();
    }

    public static void setActualTransactionActive(boolean active) {
        TransactionSynchronizationManager.actualTransactionActive.set(active ? Boolean.TRUE : null);
    }

    public static boolean isActualTransactionActive() {
        return TransactionSynchronizationManager.actualTransactionActive.get() != null;
    }

    public static void clear() {
        TransactionSynchronizationManager.synchronizations.remove();
        TransactionSynchronizationManager.currentTransactionName.remove();
        TransactionSynchronizationManager.currentTransactionReadOnly.remove();
        TransactionSynchronizationManager.currentTransactionIsolationLevel.remove();
        TransactionSynchronizationManager.actualTransactionActive.remove();
    }
}
