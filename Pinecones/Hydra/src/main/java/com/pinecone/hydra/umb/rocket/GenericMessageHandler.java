package com.pinecone.hydra.umb.rocket;

import com.pinecone.framework.util.Debug;

public class GenericMessageHandler implements MessageHandler{
    @Override
    public boolean handleMessage(String message) {
        Debug.trace(message);
        return true;
    }
}
