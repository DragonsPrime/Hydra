package com.pinecone.hydra.umct.appoint;

import java.io.IOException;

import com.pinecone.hydra.umc.msg.UMCMessage;
import com.pinecone.hydra.umct.IlleagalResponseException;
import com.pinecone.hydra.umct.husky.compiler.DynamicMethodPrototype;

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

    <T> T getIface( Class<T> iface );
}
