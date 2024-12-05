package com.pinecone.hydra.umct;

import java.io.IOException;
import java.util.Map;

import com.pinecone.hydra.umc.msg.Medium;
import com.pinecone.hydra.umc.msg.Status;
import com.pinecone.hydra.umc.msg.UMCMessage;
import com.pinecone.framework.system.ProvokeHandleException;
import com.pinecone.hydra.umc.msg.UMCReceiver;
import com.pinecone.hydra.umc.msg.UMCTransmit;

/**
 *  Pinecone Ursus For Java Hydra Ulfar, Wolf Express
 *  Author: Harold.E / JH.W (DragonKing)
 *  Copyright © 2008 - 2028 Bean Nuts Foundation All rights reserved.
 */
public class WolfMCExpress extends ArchMsgExpress implements UMCTExpressHandler {
    public WolfMCExpress( String name, ArchMessagram messagram ) {
        super( name, messagram );
        this.register(
                this.recruit( "Messagelet" )
        );
    }

    public WolfMCExpress( ArchMessagram messagram ) {
        this( null, messagram );
    }

    @Override
    protected MessageDeliver spawn( String szName ) { // TODO
        if( szName.equals( "Messagelet" ) ) {
            return new JSONLetMsgDeliver( this );
        }
        return null;
    }


    @Override
    public void onSuccessfulMsgReceived( Medium medium, UMCTransmit transmit, UMCReceiver receiver, UMCMessage msg, Object[] args ) throws Exception {
        UlfConnection connection = new UlfConnection(  medium, msg, transmit, receiver, args );
        this.onSuccessfulMsgReceived( connection, args );
    }

    @Override
    public void onSuccessfulMsgReceived( UMCConnection connection, Object[] args ) throws Exception {
        int c = 0;
        for( Map.Entry<String, MessageDeliver > kv : this.mDeliverPool.entrySet() ) {
            try{
                MessageDeliver deliver = kv.getValue();
                deliver.toDispatch( connection );
            }
            catch ( IOException e ) {
                // Just continue.
                // 你不干有的是人干.
                ++c;
            }
        }

        if( c == this.mDeliverPool.size() ) {
            connection.getTransmit().sendInformMsg( null, Status.MappingNotFound );
        }

        connection.release();
    }

    @Override
    public void onErrorMsgReceived( Medium medium, UMCTransmit transmit, UMCReceiver receiver, UMCMessage msg, Object[] args ) throws Exception {

    }

    @Override
    public void onError( Object ctx, Throwable cause ) {
        if( cause instanceof Exception ) {
            this.warnSimple( cause.getMessage() );
        }
        else {
            throw new ProvokeHandleException( cause );
        }
    }
}
