package com.pinecone.framework.system;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

import com.pinecone.framework.system.executum.EventedTaskManager;
import com.pinecone.framework.system.executum.ExclusiveProcessum;
import com.pinecone.framework.system.executum.Executum;
import com.pinecone.framework.system.executum.Processum;
import com.pinecone.framework.system.executum.VitalResource;
import com.pinecone.framework.unit.LinkedTreeMap;
import com.pinecone.framework.util.Debug;
import com.pinecone.framework.util.lock.ReentrantReadWriteSpinLock;

public class GenericMasterTaskManager implements EventedTaskManager {
    protected Processum                               mParentProcessum     ;
    protected ClassLoader                             mClassLoader         ;
    protected RuntimeSystem                           mSystem;
    protected Map<Integer, Executum>                  mExecutumPool        = new LinkedTreeMap<>();
    protected Map<String, ExclusiveProcessum >        mExclusiveTasks      = new LinkedTreeMap<>();
    protected Map<Integer, VitalResource>             mVitalResourcePool   = new LinkedTreeMap<>();
    protected long                                    mnVitalizeCount      = 0;
    protected long                                    mnFatalityCount      = 0;
    protected long                                    mnMaxWaitApoptosis   = 5000;
    protected final Object                            mTerminationLock     = new Object();
    protected BlockingDeque<Executum >                mSyncApoptosisQueue  = new LinkedBlockingDeque<>();
    protected Phaser                                  mFinishingPhaser     = new Phaser( 1 );
    protected final ReentrantReadWriteSpinLock        mPoolLock            = new ReentrantReadWriteSpinLock(); // Using optimistic lock.

    public GenericMasterTaskManager( Processum parent, ClassLoader classLoader ) {
        this.mParentProcessum = parent;
        if( parent instanceof RuntimeSystem ) {
            this.mSystem = (RuntimeSystem) parent;
        }
        else {
            this.mSystem = parent.getSystem();
        }

        this.mClassLoader = classLoader;
    }

    public GenericMasterTaskManager( Processum parent ) {
        this( parent, null );

        if( this.mSystem != null ) {
            this.mClassLoader = this.mSystem.getGlobalClassLoader();
        }
        else {
            this.mClassLoader = Thread.currentThread().getContextClassLoader();
        }
    }


    protected BlockingDeque<Executum  >    getSyncApoptosisQueue(){
        return this.mSyncApoptosisQueue;
    }

    public Map<Integer, Executum > getExecutumPool() {
        return this.mExecutumPool;
    }

    public Map<String, ExclusiveProcessum > getExclusiveTasks() {
        return this.mExclusiveTasks;
    }

    @Override
    public Processum getParentProcessum () {
        return this.mParentProcessum;
    }

    @Override
    public RuntimeSystem getSystem() {
        return this.mSystem;
    }

    @Override
    public ClassLoader getClassLoader() {
        return this.mClassLoader;
    }

    @Override
    public Map<Integer, VitalResource > getVitalResources() {
        return this.mVitalResourcePool;
    }

    @Override
    public void executeZionSequence() {
        Map<Integer, VitalResource   > map = this.getVitalResources();
        for ( Map.Entry<Integer, VitalResource > kv : map.entrySet() ) {
            VitalResource resource = kv.getValue();
            try{
                resource.store();
            }
            catch ( Throwable e ) {
                System.err.println(
                        String.format( "[FatesCriticalWarn] [VitalResource: %s, Id: %d] [StoreFailed]", resource.getName(), resource.getId() )
                );
            }
        }
    }

    @Override
    public void sendApoptosisSignal() {
        for ( Map.Entry<Integer, Executum > kv : this.getExecutumPool().entrySet() ) {
            kv.getValue().apoptosis();
        }
    }

    protected void killAll() {
        if( !this.isTerminated() ) {
            for ( Map.Entry<Integer, Executum > kv : this.getExecutumPool().entrySet() ) {
                kv.getValue().kill();
            }

            if( !this.mFinishingPhaser.isTerminated() ) {
                this.mFinishingPhaser.forceTermination();
            }
        }
    }

    @Override
    public void terminate() {
        this.executeZionSequence();
        this.killAll();
    }

    @Override
    public void  suspendAll() {
        for ( Map.Entry<Integer, Executum > kv : this.getExecutumPool().entrySet() ) {
            kv.getValue().suspend();
        }
    }

    @Override
    public void  resumeAll() {
        for ( Map.Entry<Integer, Executum > kv : this.getExecutumPool().entrySet() ) {
            kv.getValue().resume();
        }
    }

    @Override
    public int     size(){
        return this.getExecutumPool().size();
    }

    @Override
    public boolean isPooled(){
        return true;
    }

    @Override
    public long    getVitalizeCount() {
        return this.mnVitalizeCount;
    }

    @Override
    public long    getFatalityCount() {
        return this.mnFatalityCount;
    }

    @Override
    public Executum add( Executum that ){
        this.getExecutumPool().put( that.getId(), that );
        if( that instanceof ExclusiveProcessum ) {
            this.getExclusiveTasks().put( that.getName(), (ExclusiveProcessum) that );
        }
        return that;
    }

    @Override
    public Executum addSync( Executum that ) {
        this.mPoolLock.writeLock().lock();
        try{
            return this.add( that );
        }
        finally {
            this.mPoolLock.writeLock().unlock();
        }
    }

    @Override
    public void erase( Executum that ){
        if( this.autopsy( that ) ) {
            this.getExecutumPool().remove( that.getId() );
            this.getExclusiveTasks().remove( that.getName() );
            ++this.mnFatalityCount;
        }
        else {
            throw new IllegalStateException( "Executum is still alive." );
        }
    }

    @Override
    public void eraseSync( Executum that ) {
        this.mPoolLock.writeLock().lock();
        try{
            this.erase( that );
        }
        finally {
            this.mPoolLock.writeLock().unlock();
        }
    }

    @Override
    public void purge() {
        this.terminate();
        this.getExecutumPool().clear();
        this.getVitalResources().clear();
        this.getExclusiveTasks().clear();
    }

    @Override
    public boolean isTerminated(){
        boolean b = true;
        for ( Map.Entry<Integer, Executum > kv : this.getExecutumPool().entrySet() ) {
            Thread primaryAffiliateThread = kv.getValue().getAffiliateThread();
            if( primaryAffiliateThread != null ) { // null is uninitialized thread.
                if( !primaryAffiliateThread.isDaemon() ) {
                    b &= kv.getValue().isTerminated();
                }
            }
        }
        return b;
    }

    @Override
    public void syncWaitingTerminated() throws Exception {
        this.mFinishingPhaser.arriveAndAwaitAdvance();

        if( !this.isTerminated() ){
            while ( true ) {
                if( this.isTerminated() ) {
                    break;
                }

                synchronized ( this.mTerminationLock ) {
                    this.mTerminationLock.wait( 10 );
                }
            }
        }
    }

    protected Executum spawn     ( String szClassPath, Object... args ) {
        Executum obj = null;
        try {
            Class<?>[] paramTypes;
            if( args.length > 0 && args[0] instanceof Class<?>[] ) {
                paramTypes = (Class<?>[]) args[0];
                Object[] neoArgs = new Object[ args.length - 1 ];
                for ( int i = 0; i < neoArgs.length; i++ ) {
                    neoArgs[i] = args[i+1];
                }
                args = neoArgs;
            }
            else {
                paramTypes = new Class<?>[ args.length ];
                for ( int i = 0; i < args.length; i++ ) {
                    paramTypes[i] = args[i].getClass();
                }
            }

            Class<?> pVoid = this.getClassLoader().loadClass( szClassPath );
            try{
                Constructor<?> constructor = pVoid.getConstructor( paramTypes );
                obj = (Processum) constructor.newInstance( args );
            }
            catch ( NoSuchMethodException | InvocationTargetException e1 ){
                this.getSystem().handleLiveException( e1 );
            }
        }
        catch ( ClassNotFoundException | IllegalAccessException | InstantiationException e ){
            this.getSystem().handleLiveException( e );
        }
        return obj;
    }

    @Override
    public Executum summon       ( String szClassPath, Object... args ) throws Exception {
        String[] debris   = szClassPath.split( "." );
        String szTaskName = debris[ debris.length - 1 ];
        Executum obj = (Executum) this.getExclusiveTasks().get( szTaskName );
        if( obj != null ) {
            return obj;
        }

        obj = this.spawn( szClassPath, args );
        this.add( obj );

        ++this.mnVitalizeCount;
        return obj;
    }

    @Override
    public void     kill          ( Executum that ) {
        that.kill();
        this.eraseSync( that );
    }

    protected boolean isApproveLifeRenewal( ApoptosisRejectSignalException e ) {
        return true; // TODO
    }

    @Override
    public void     apoptosis     ( Executum that ) {
        try{
            that.apoptosis();
        }
        catch ( ApoptosisRejectSignalException e ) {
            if( this.isApproveLifeRenewal( e ) ) {
                return;
            }
            else {
                try {
                    that.apoptosis();
                }
                catch ( ApoptosisRejectSignalException e1 ) { // No more wait, just going to die.
                    System.err.println(
                            String.format( "[FatesCriticalWarn] [Executum: %d] [ForceApoptosis]", that.hashCode() )
                    );
                }
            }
        }

        try{
            Executum suspect = this.getSyncApoptosisQueue().poll( this.mnMaxWaitApoptosis, TimeUnit.MILLISECONDS );
            if( suspect == that ) {
                this.kill( that );
            }
        }
        catch ( InterruptedException e ) {
            this.kill( that );
        }
    }

    @Override
    public void     commitSuicide ( Executum that ){
        this.getSyncApoptosisQueue().add( that );
    }

    @Override
    public boolean  autopsy       ( Executum that ) {
        return true; //TODO
    }

    @Override
    public String   nomenclature    ( Thread that   ) {
        return String.format(
                "proc-%s-%s",this.getParentProcessum().getName(), that.getName()
        ).toLowerCase();
    }

    @Override
    public void notifyFinished    ( Executum that ){
        this.mFinishingPhaser.arriveAndDeregister();
    }

    @Override
    public void notifyExecuting   ( Executum that ){
        this.mFinishingPhaser.register();
    }

}
