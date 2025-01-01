package com.pinecone.hydra.umct.appoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pinecone.hydra.express.Deliver;
import com.pinecone.hydra.umc.msg.Medium;
import com.pinecone.hydra.umc.msg.UMCMessage;
import com.pinecone.hydra.umc.msg.UMCReceiver;
import com.pinecone.hydra.umc.msg.UMCTransmit;
import com.pinecone.hydra.umct.MessageDeliver;
import com.pinecone.hydra.umct.MessageJunction;
import com.pinecone.hydra.umct.UMCConnection;
import com.pinecone.hydra.umct.WolfMCExpress;


public class HuskyDuplexExpress extends ArchDuplexExpress {
    protected WolfMCExpress                     mFriendExpress;

    public HuskyDuplexExpress( String name, MessageJunction messagram, Logger logger ) {
        super();
        this.mFriendExpress = new HuskyMCDuplexExpress( name, messagram, logger, this );
        this.mLogger = this.mFriendExpress.getLogger();
    }

    public HuskyDuplexExpress( String name, MessageJunction messagram ) {
        this( name, messagram, LoggerFactory.getLogger( HuskyDuplexExpress.class.getName() ) );
    }

    public HuskyDuplexExpress( MessageJunction messagram ) {
        this( null, messagram );
    }


    @Override
    public UMCMessage processResponse( UMCMessage request, UMCMessage response ) {
        response = this.mFriendExpress.processResponse( request, response );
        response = super.processResponse( request, response );
        // Maintain the chain-of-responsibility.
        // 保持责任链，确保每一层级的处理序.

        return response;
    }

    @Override
    protected void onSuccessfulMsgReceived( UMCConnection connection, Object[] args ) throws Exception {
        // Dummy
    }

    @Override
    public void onSuccessfulMsgReceived( Medium medium, UMCTransmit transmit, UMCReceiver receiver, UMCMessage msg, Object[] args ) throws Exception {
        this.mFriendExpress.onSuccessfulMsgReceived( medium, transmit, receiver, msg, args );
    }

    @Override
    public void onErrorMsgReceived( Medium medium, UMCTransmit transmit, UMCReceiver receiver, UMCMessage msg, Object[] args ) throws Exception {
        this.mFriendExpress.onErrorMsgReceived( medium, transmit, receiver, msg, args );
    }

    @Override
    public void onError( Object data, Throwable cause ) {
        this.mFriendExpress.onError( data, cause );
    }

    @Override
    public String getName() {
        return this.mFriendExpress.getName();
    }

    @Override
    public MessageJunction getJunction() {
        return this.mFriendExpress.getJunction();
    }

    @Override
    public Logger getLogger() {
        return this.mFriendExpress.getLogger();
    }

    @Override
    public MessageDeliver   recruit ( String szName ) {
        return this.mFriendExpress.recruit( szName );
    }

    @Override
    public HuskyDuplexExpress register    ( Deliver deliver ) {
        this.mFriendExpress.register( deliver );
        return this;
    }

    @Override
    public HuskyDuplexExpress   fired       ( Deliver deliver ) {
        this.mFriendExpress.fired( deliver );
        return this;
    }

    @Override
    public MessageDeliver getDeliver    ( String szName ) {
        return this.mFriendExpress.getDeliver( szName );
    }

    @Override
    public boolean hasOwnDeliver( Deliver deliver ) {
        return this.mFriendExpress.hasOwnDeliver( deliver );
    }

    @Override
    public boolean hasOwnDeliver( String deliverName ) {
        return this.mFriendExpress.hasOwnDeliver( deliverName );
    }

    @Override
    public int size() {
        return this.mFriendExpress.size();
    }



    static class HuskyMCDuplexExpress extends WolfMCExpress {
        private HuskyDuplexExpress husky;

        public HuskyMCDuplexExpress( String name, MessageJunction messagram, Logger logger, HuskyDuplexExpress self ) {
            super( name, messagram );

            this.husky   = self;
            this.mLogger = logger;
        }

        @Override
        protected void onSuccessfulMsgReceived( UMCConnection connection, Object[] args ) throws Exception {
            boolean isDuplexControlMessage = this.husky.handleDuplexControlMessage( connection, args );
            if ( !isDuplexControlMessage ) {
                super.onSuccessfulMsgReceived( connection, args );
            }
        }
    }
}
