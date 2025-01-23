package com.pinecone.hydra.umq;

public interface MessageHandler {
    boolean handleMessage(String message);
}
