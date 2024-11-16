package com.pinecone.hydra.messagram;

import com.pinecone.hydra.system.Hydrarum;
import com.pinecone.hydra.umc.msg.UMCReceiver;
import com.pinecone.hydra.umc.msg.UMCTransmit;
import com.pinecone.framework.util.json.JSONObject;
import com.pinecone.framework.util.json.JSONMaptron;

import java.io.IOException;
import java.util.Map;

public abstract class ArchMessagelet implements Messagelet {
    protected Hydrarum                  mSystem;
    protected Map<String, Object >      mConfig;
    protected ArchMessagram             mMessagelet;
    protected MessagePackage            mMsgPackage;

    protected UMCTransmit               mUMCTransmit;
    protected UMCReceiver               mUMCReceiver;

    public ArchMessagelet( MessagePackage msgPackage, ArchMessagram servtron ) {
        this.mMsgPackage   = msgPackage;
        this.mSystem       = this.getMessageDeliver().getSystem();
        this.mMessagelet   = servtron;
        this.mConfig       = new JSONMaptron(); //TODO
    }

    protected abstract Map<String, Object > $_MSG();

    @Override
    public MessageDeliver getMessageDeliver() {
        return this.getMessagePackage().getDeliver();
    }

    @Override
    public MessagePackage getMessagePackage() {
        return this.mMsgPackage;
    }

    @Override
    public Hydrarum getSystem() {
        return this.mSystem;
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
