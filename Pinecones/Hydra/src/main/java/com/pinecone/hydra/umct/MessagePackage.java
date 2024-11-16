package com.pinecone.hydra.umct;

import com.pinecone.hydra.umc.msg.Medium;
import com.pinecone.hydra.umc.msg.UMCMessage;
import com.pinecone.hydra.umc.msg.UMCReceiver;
import com.pinecone.hydra.umc.msg.UMCTransmit;
import com.pinecone.hydra.express.Deliver;
import com.pinecone.hydra.express.Package;

public interface MessagePackage extends Package {
    MessageDeliver getDeliver();

    UMCMessage getMessage();

    UMCTransmit getTransmit();

    UMCReceiver getReceiver();

    Medium getMessageSource();

    @Override
    default String  getConsignee() {
        Object e = this.getMessage().getHead().getExHeaderVal( this.getDeliver().getServiceKeyword() );
        if( e instanceof String ) {
            return (String) e;
        }
        return e.toString();
    }

    MessagePackage entrust( Deliver deliver );
}
