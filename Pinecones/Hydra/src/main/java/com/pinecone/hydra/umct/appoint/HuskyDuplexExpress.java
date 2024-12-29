package com.pinecone.hydra.umct.appoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pinecone.hydra.express.Package;
import com.pinecone.hydra.umc.msg.ChannelControlBlock;
import com.pinecone.hydra.umc.msg.MultiClientChannelRegistry;
import com.pinecone.hydra.umc.msg.UMCChannel;
import com.pinecone.hydra.umc.msg.UMCMessage;
import com.pinecone.hydra.umct.MessageJunction;
import com.pinecone.hydra.umct.DuplexExpress;
import com.pinecone.hydra.umct.UMCConnection;
import com.pinecone.hydra.umct.WolfMCExpress;
import com.pinecone.hydra.umct.appoint.pool.GenericMultiClientChannelRegistry;
import com.pinecone.hydra.umct.husky.HuskyCTPConstants;

public class HuskyDuplexExpress extends WolfMCExpress implements DuplexExpress {
    protected MultiClientChannelRegistry<Long > mMultiClientChannelRegistry;

    public HuskyDuplexExpress( String name, MessageJunction messagram ) {
        super( name, messagram );
        this.mMultiClientChannelRegistry = new GenericMultiClientChannelRegistry<>();
    }

    public HuskyDuplexExpress( MessageJunction messagram ) {
        this( null, messagram );
    }

    @Override
    protected void onSuccessfulMsgReceived( UMCConnection connection, Object[] args ) throws Exception {
        boolean isInterceptPassiveChannel = this.interceptPassiveChannel( connection, args );
        if ( !isInterceptPassiveChannel ) {
            super.onSuccessfulMsgReceived( connection, args );
        }
    }

    protected UMCConnection wrap( Package that ) {
        return (UMCConnection) that;
    }

    protected boolean interceptPassiveChannel( UMCConnection connection, Object[] args ) {
        UMCConnection uc          = this.wrap( connection );
        UMCMessage msg            = uc.getMessage();
        long controlBits          = msg.getHead().getControlBits();
        if ( controlBits == HuskyCTPConstants.HCTP_DUP_CONTROL_REGISTER ) {
            this.registerPassiveChannel( uc, connection, args );
            return true;
        }

        return false;
    }

    protected void registerPassiveChannel( UMCConnection uc, UMCConnection connection, Object[] args ) {
        ChannelControlBlock ccb = (ChannelControlBlock) args[ 0 ];
        UMCChannel      channel = ccb.getChannel();
        long                cid = channel.getIdentityID();

        this.mMultiClientChannelRegistry.register( cid, ccb );
        this.getLogger().info( "[PassiveChannel] [ClientId: {}, ChannelId: {}] <{}>", cid, ccb.getChannel().getChannelID(), "Registered" );
    }
}
