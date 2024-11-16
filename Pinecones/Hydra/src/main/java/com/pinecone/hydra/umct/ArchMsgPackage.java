package com.pinecone.hydra.umct;

import com.pinecone.hydra.umc.msg.Medium;
import com.pinecone.hydra.umc.msg.UMCMessage;
import com.pinecone.hydra.umc.msg.UMCReceiver;
import com.pinecone.hydra.umc.msg.UMCTransmit;
import com.pinecone.hydra.express.Deliver;
import com.pinecone.hydra.system.Hydrarum;

public abstract class ArchMsgPackage implements MessagePackage {
    protected MessageDeliver  mDeliver;
    protected Medium mMessageSource;
    protected UMCMessage mUMCMessage;
    protected UMCTransmit mUMCTransmit;
    protected UMCReceiver mUMCReceiver;

    public ArchMsgPackage( Medium medium, UMCMessage message, UMCTransmit transmit, UMCReceiver receiver ) {
        this.mMessageSource = medium;
        this.mUMCMessage    = message;
        this.mUMCTransmit   = transmit;
        this.mUMCReceiver   = receiver;
    }


    @Override
    public Hydrarum getSystem() {
        return this.getDeliver().getSystem();
    }

    @Override
    public MessageDeliver getDeliver() {
        return this.mDeliver;
    }

    @Override
    public UMCMessage getMessage() {
        return this.mUMCMessage;
    }

    @Override
    public UMCTransmit getTransmit() {
        return this.mUMCTransmit;
    }

    @Override
    public UMCReceiver getReceiver() {
        return this.mUMCReceiver;
    }

    @Override
    public Medium getMessageSource() {
        return this.mMessageSource;
    }

    @Override
    public ArchMsgPackage entrust( Deliver deliver ) {
        this.mDeliver = (MessageDeliver)deliver;
        return this;
    }

    public void release() {
        this.mMessageSource.release();
        this.mDeliver       = null;
        this.mMessageSource = null;
        this.mUMCMessage    = null;
        this.mUMCTransmit   = null;
        this.mUMCReceiver   = null;
    }

}
