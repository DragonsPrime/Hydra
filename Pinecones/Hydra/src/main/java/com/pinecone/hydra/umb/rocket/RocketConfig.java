package com.pinecone.hydra.umb.rocket;

import com.pinecone.framework.system.prototype.Pinenut;

public interface RocketConfig extends Pinenut {
    String getNameServerAddr();

    String getGroupName();

    int getMaxMessageSize();

    int getSendMsgTimeout();

    int getRetryTimesWhenSendFailed();
}
