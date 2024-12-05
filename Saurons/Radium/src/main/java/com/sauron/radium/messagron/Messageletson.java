package com.sauron.radium.messagron;

import com.pinecone.hydra.umct.ArchMessagram;
import com.pinecone.hydra.umct.UMCConnection;
import com.pinecone.hydra.umct.JSONLetMsgDeliver;
import com.pinecone.hydra.umct.ArchMessagelet;
import com.pinecone.framework.util.json.JSONObject;
import com.pinecone.framework.util.json.JSONMaptron;
import com.pinecone.hydra.umc.msg.UMCMessage;

import java.util.Map;

public abstract class Messageletson extends ArchMessagelet {
    protected JSONObject       mLetLocal = new JSONMaptron();

    public Messageletson(UMCConnection msgPackage, ArchMessagram servtron ) {
        super( msgPackage, servtron );
        this.mUMCReceiver = this.getMessagePackage().getReceiver();
        this.mUMCTransmit = this.getMessagePackage().getTransmit();
    }

    // PHP Style
    @Override
    protected Map<String, Object > $_MSG() {
        return this.getReceivedMessage().getHead().evalMapExtraHead();
    }

    @Override
    public UMCMessage getReceivedMessage() {
        return this.getMessagePackage().getMessage();
    }

    @Override
    public JSONLetMsgDeliver getMessageDeliver() {
        return (JSONLetMsgDeliver)super.getMessageDeliver();
    }

    @Override
    public JSONObject getLetLocal()  {
        return this.mLetLocal;
    }

    @Override
    public String toJSONString() {
        return this.getLetLocal().toJSONString();
    }
}
