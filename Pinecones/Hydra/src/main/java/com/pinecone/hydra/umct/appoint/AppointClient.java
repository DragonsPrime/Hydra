package com.pinecone.hydra.umct.appoint;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.protobuf.DynamicMessage;
import com.pinecone.hydra.umc.msg.UMCMessage;
import com.pinecone.hydra.umc.wolfmc.UlfInformMessage;
import com.pinecone.hydra.umc.wolfmc.client.WolfMCClient;
import com.pinecone.hydra.umct.IlleagalResponseException;
import com.pinecone.hydra.umct.protocol.compiler.DynamicMethodPrototype;

public interface AppointClient extends AppointNode {
    UMCMessage sendSyncMsg( UMCMessage request ) throws IOException;

    UMCMessage sendSyncMsg( UMCMessage request, boolean bNoneBuffered ) throws IOException ;

    void       sendAsynMsg( UMCMessage request ) throws IOException ;

    void       sendAsynMsg( UMCMessage request, AsynMsgHandler handler ) throws IOException ;


    void invokeInformAsyn( DynamicMethodPrototype method, Object[] args, AsynMsgHandler handler ) throws IOException;

    void invokeInformAsyn( DynamicMethodPrototype method, Object[] args, AsynReturnHandler handler ) throws IOException ;

    Object invokeInform( DynamicMethodPrototype method, Object[] args, long nWaitTimeMil ) throws IlleagalResponseException, IOException ;

    Object invokeInform( DynamicMethodPrototype method, Object... args ) throws IlleagalResponseException, IOException ;

    void invokeInformAsyn( String szMethodAddress, Object[] args, AsynMsgHandler handler ) throws IOException ;

    void invokeInformAsyn( String szMethodAddress, Object[] args, AsynReturnHandler handler ) throws IOException ;

    Object invokeInform( String szMethodAddress, Object[] args, long nWaitTimeMil ) throws IlleagalResponseException, IOException ;

    Object invokeInform( String szMethodAddress, Object... args ) throws IlleagalResponseException, IOException ;

}
