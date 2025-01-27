package com.pinecone.hydra.umb.rocket;

import java.io.InputStream;
import java.io.OutputStream;

import com.pinecone.framework.system.NotImplementedException;
import com.pinecone.hydra.umc.msg.Medium;
import com.pinecone.hydra.umc.msg.MessageNodus;

public class RocketMedium implements Medium {
    protected MessageNodus mMessageNode;

    public RocketMedium( MessageNodus medium ) {
        this.mMessageNode = medium;
    }

    @Override
    public OutputStream getOutputStream(){
        throw new NotImplementedException();
    }

    @Override
    public InputStream getInputStream(){
        throw new NotImplementedException();
    }

    @Override
    public Object getNativeMessageSource(){
        throw new NotImplementedException();
    }

    @Override
    public String sourceName(){
        return "RocketMQ";
    }

    @Override
    public MessageNodus getMessageNode() {
        return this.mMessageNode;
    }

    @Override
    public void release() {

    }
}
