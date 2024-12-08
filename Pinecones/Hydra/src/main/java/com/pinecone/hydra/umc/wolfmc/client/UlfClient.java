package com.pinecone.hydra.umc.wolfmc.client;

import java.io.IOException;

import com.pinecone.hydra.umc.msg.UMCMessage;
import com.pinecone.hydra.umc.wolfmc.UlfAsyncMsgHandleAdapter;
import com.pinecone.hydra.umc.wolfmc.UlfMessageNode;

public interface UlfClient extends UlfMessageNode {

    UMCMessage sendSyncMsg( UMCMessage request ) throws IOException;

    UMCMessage sendSyncMsg( UMCMessage request, boolean bNoneBuffered ) throws IOException ;

    void       sendAsynMsg( UMCMessage request ) throws IOException ;

    void       sendAsynMsg( UMCMessage request, UlfAsyncMsgHandleAdapter handler ) throws IOException;

}
