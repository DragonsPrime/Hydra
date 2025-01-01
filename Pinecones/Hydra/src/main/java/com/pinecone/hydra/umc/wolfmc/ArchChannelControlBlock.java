package com.pinecone.hydra.umc.wolfmc;

import com.pinecone.hydra.umc.io.IOCounter;
import com.pinecone.hydra.umc.msg.Medium;
import com.pinecone.hydra.umc.msg.MessageNode;
import com.pinecone.hydra.umc.msg.UMCMessage;
import com.pinecone.framework.system.executum.ArchThreadum;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;

public abstract class ArchChannelControlBlock extends ArchThreadum implements NettyChannelControlBlock {
    protected UlfChannel             mChannel;
    protected MessageNode            mMessageNode;

    // For Load Balance.
    protected IOCounter              mIOCounter;

    protected boolean                mbForceSyncMode;
    protected boolean                mbInSyncMode;

    protected UlfMCTransmit          mTransmit;
    protected UlfMCReceiver          mReceiver;

    protected Map<String, Object >   mAttributesMap = new TreeMap<>();

    protected ArchChannelControlBlock( MessageNode parentNode, UlfChannel channel, boolean bForceSyncMode ) {
        super( null, parentNode );
        this.mChannel          = channel;
        this.mbForceSyncMode   = bForceSyncMode;
        this.mbInSyncMode      = bForceSyncMode;
        this.mMessageNode      = parentNode;
    }


    @Override
    public Map<String, Object >        getAttributes(){
        return this.mAttributesMap;
    }

    @Override
    public ArchChannelControlBlock setThreadAffinity( Thread affinity ) {
        super.setThreadAffinity( affinity );
        this.getChannel().setThreadAffinity( affinity );
        return this;
    }

    public UlfChannel        getChannel() {
        return this.mChannel;
    }

    public IOCounter         getIOCounter() {
        return this.mIOCounter;
    }

    public boolean           getInSyncMode() {
        return this.mbInSyncMode;
    }

    public UlfMCTransmit     getTransmit() {
        return this.mTransmit;
    }

    public UlfMCReceiver     getReceiver() {
        return this.mReceiver;
    }

    public MessageNode       getParentMessageNode() {
        return this.mMessageNode;
    }


    @Override
    public void              sendMsg( UMCMessage request, boolean bNoneBuffered ) throws IOException {
        this.getChannel().setChannelStatus( UlfChannelStatus.WAITING_FOR_SEND );
        this.mTransmit.sendMsg( request, bNoneBuffered );
        this.getChannel().setChannelStatus( UlfChannelStatus.WAITING_FOR_RECEIVE );
    }

    protected void           afterConnectionArrive( Medium medium, boolean bRenew, Lock forceSyncLock ) {
        if( this.mbForceSyncMode ) {
            forceSyncLock.lock();
        }

        try{
            if( bRenew ) {
                this.mTransmit.applyMessageSource( medium );
                this.mReceiver.applyMessageSource( medium );
            }
            this.mTransmit = new UlfMCTransmit( medium );
            this.mReceiver = new UlfMCReceiver( medium );
        }
        finally {
            if( this.mbForceSyncMode ) {
                forceSyncLock.unlock();
            }
        }
    }

    public void              release() {
        this.mChannel.release();
        //this.mChannel            = null;
        this.mIOCounter          = null;
        this.mTransmit           = null;
        this.mReceiver           = null;
    }

    public void              close(){
        this.mChannel.close();
    }

    public boolean           isShutdown() {
        return this.getChannel().isShutdown() || !this.getChannel().getNativeHandle().isActive();
    }

    public UlfChannelStatus  getChannelStatus() {
        return this.getChannel().getChannelStatus();
    }

    @Override
    public void              kill() {
        this.close();
        this.release();
    }
}
