package com.pinecone.hydra.umct;

import com.pinecone.hydra.umc.msg.UMCReceiver;
import com.pinecone.hydra.umc.msg.UMCTransmit;
import com.pinecone.framework.util.json.JSONObject;
import com.pinecone.framework.util.json.JSONMaptron;

import java.io.IOException;
import java.util.Map;

public abstract class ArchMessagelet implements Messagelet {
    protected Map<String, Object >      mConfig;
    protected ArchMessagram             mMessagelet;
    protected UMCConnection mMsgPackage;

    protected UMCTransmit               mUMCTransmit;
    protected UMCReceiver               mUMCReceiver;

    public ArchMessagelet( UMCConnection msgPackage, ArchMessagram servtron ) {
        this.mMsgPackage   = msgPackage;
        this.mMessagelet   = servtron;
        this.mConfig       = new JSONMaptron(); //TODO
    }

    protected abstract Map<String, Object > $_MSG();

    @Override
    public MessageDeliver getMessageDeliver() {
        return this.getMessagePackage().getDeliver();
    }

    @Override
    public UMCConnection getMessagePackage() {
        return this.mMsgPackage;
    }

    @Override
    public UMCTransmit getTransmit(){
        return this.mUMCTransmit;
    }

    @Override
    public UMCReceiver getReceiver(){
        return this.mUMCReceiver;
    }

    @Override
    public ArchMessagram getMessagelet() {
        return this.mMessagelet;
    }

    protected void sendDefaultConfirmResponse() throws IOException {
        JSONObject jo = new JSONMaptron();
        jo.put( "Messagelet", "ReceiveConfirm" );
        this.getTransmit().sendInformMsg( jo );
    }

    @Override
    public Map<String, Object > getConfig() {
        return this.mConfig;
    }

    @Override
    public abstract void dispatch();

    @Override
    public String serviceName() {
        return this.className();
    }
}
