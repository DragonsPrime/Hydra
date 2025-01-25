package com.pinecone.hydra.umb.rocket;

import com.pinecone.hydra.umb.broadcast.PushConsumer;

public abstract class ArchMQConsumer implements PushConsumer {
    protected String mszNameServerAddr;

    protected String mszGroupName;

    protected String mszTopic;

    protected String mszTag;

    public ArchMQConsumer( String nameSrvAddr, String groupName, String topic, String tag ) {
        this.mszNameServerAddr = nameSrvAddr;
        this.mszGroupName      = groupName;
        this.mszTopic          = topic;
        this.mszTag            = tag;
    }
}
