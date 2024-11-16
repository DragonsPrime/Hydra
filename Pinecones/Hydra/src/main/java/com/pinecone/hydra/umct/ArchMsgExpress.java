package com.pinecone.hydra.umct;

import com.pinecone.hydra.system.component.Slf4jTraceable;
import com.pinecone.hydra.express.Deliver;
import com.pinecone.hydra.system.Hydrarum;
import org.slf4j.Logger;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 *  Pinecone Ursus For Java MessageExpress [Archetype]
 *  Author: Harold.E / JH.W (DragonKing)
 *  Copyright © 2008 - 2028 Bean Nuts Foundation All rights reserved.
 *  *****************************************************************************************
 *  MessageExpress vs Messenger
 *  Messenger is the abstract producer of domestic.
 *  Messenger is the message reproducer, who import commodities(Messages) from abroad (inbound, the real producer).
 *
 *  MessageExpress is the deliver center, that deliver the package(Message) through deliver man to the consumer(e.g. Messagelet).
 *  [Server -> send message -> (out)] -> [(in) -> Messenger -> Express -> Deliver -> Messagelet ]
 *  *****************************************************************************************
 */
public abstract class ArchMsgExpress implements MessageExpress, Slf4jTraceable {
    protected String        mszName      ;
    protected Hydrarum      mSystem      ;
    protected ArchMessagram mMessagram   ;
    protected Logger        mLogger      ;
    protected Map<String, MessageDeliver > mDeliverPool = new LinkedHashMap<>();
    protected ReadWriteLock                mPoolLock    = new ReentrantReadWriteLock();

    public ArchMsgExpress( String name, ArchMessagram messagram ) {
        this.mszName      = name;
        this.mMessagram   = messagram;
        this.mSystem      = messagram.getSystem();
        this.mLogger      = this.getSystem().getTracerScope().newLogger( this.className() );

        if( this.mszName == null ){
            this.mszName = this.className();
        }
    }

    public ArchMsgExpress( ArchMessagram messagram ) {
        this( null, messagram );
    }

    @Override
    public String getName() {
        return this.mszName;
    }

    @Override
    public Hydrarum getSystem() {
        return this.mSystem;
    }

    @Override
    public ArchMessagram getMessagram() {
        return this.mMessagram;
    }

    @Override
    public Logger getLogger() {
        return this.mLogger;
    }

    protected ReadWriteLock        getPoolLock() {
        return this.mPoolLock;
    }

    protected Map<String, MessageDeliver > getDeliverPool() {
        return this.mDeliverPool;
    }

    protected MessageDeliver wrap( Deliver deliver ) {
        return (MessageDeliver) deliver;
    }

    protected abstract MessageDeliver spawn( String szName );

    @Override
    public MessageDeliver   recruit ( String szName ) {
        if( this.getDeliverPool().containsKey( szName ) ) {
            return this.getDeliverPool().get( szName );
        }
        MessageDeliver deliver = this.spawn( szName );
        this.register( deliver );
        return deliver;
    }

    @Override
    public ArchMsgExpress   register    ( Deliver deliver ) {
        this.getDeliverPool().put( deliver.getName(), this.wrap( deliver ) );
        return this;
    }

    @Override
    public ArchMsgExpress   fired       ( Deliver deliver ) {
        this.getDeliverPool().remove( deliver.getName(), this.wrap( deliver ) );
        return this;
    }

    public ArchMsgExpress   syncRegister( Deliver deliver ) {
        this.getPoolLock().writeLock().lock();
        try{
            this.register( deliver );
        }
        finally {
            this.getPoolLock().writeLock().unlock();
        }
        return this;
    }

    public ArchMsgExpress   syncFired   ( Deliver deliver ) {
        this.getPoolLock().writeLock().lock();
        try{
            this.fired( deliver );
        }
        finally {
            this.getPoolLock().writeLock().unlock();
        }
        return this;
    }

    @Override
    public boolean hasOwnDeliver( Deliver deliver ) {
        return this.getDeliverPool().containsKey( deliver.getName() );
    }

    @Override
    public int size() {
        return this.getDeliverPool().size();
    }

}
