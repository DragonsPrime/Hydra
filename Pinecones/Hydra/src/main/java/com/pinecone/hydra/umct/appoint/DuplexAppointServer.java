package com.pinecone.hydra.umct.appoint;

import java.io.IOException;

import com.google.protobuf.DynamicMessage;
import com.pinecone.hydra.umc.msg.UMCMessage;
import com.pinecone.hydra.umc.wolfmc.UlfAsyncMsgHandleAdapter;
import com.pinecone.hydra.umc.wolfmc.UlfInformMessage;
import com.pinecone.hydra.umct.DuplexExpress;
import com.pinecone.hydra.umct.IlleagalResponseException;
import com.pinecone.hydra.umct.husky.HuskyCTPConstants;
import com.pinecone.hydra.umct.husky.compiler.MethodPrototype;

public interface DuplexAppointServer extends AppointServer, DuplexAppointNode {

    @Override
    DuplexExpress getUMCTExpress();

    void sendAsynMsg( long clientId, UMCMessage request, boolean bNoneBuffered, UlfAsyncMsgHandleAdapter handler ) throws IOException;

    void sendAsynMsg( long clientId, UMCMessage request, boolean bNoneBuffered, AsynMsgHandler handler ) throws IOException;

    void sendAsynMsg( long clientId, UMCMessage request, AsynMsgHandler handler ) throws IOException;



    void invokeInformAsyn( long clientId, MethodPrototype method, Object[] args, AsynMsgHandler handler ) throws IOException ;

    void invokeInformAsyn( long clientId, MethodPrototype method, Object[] args, AsynReturnHandler handler ) throws IOException ;

    void invokeInformAsyn( long clientId, String szMethodAddress, Object[] args, AsynMsgHandler handler ) throws IOException ;

    void invokeInformAsyn( long clientId, String szMethodAddress, Object[] args, AsynReturnHandler handler ) throws IOException ;



    Object invokeInform( long clientId, MethodPrototype method, Object[] args, long nWaitTimeMil ) throws IlleagalResponseException, IOException ;

    Object invokeInform( long clientId, MethodPrototype method, Object... args ) throws IlleagalResponseException, IOException ;

    Object invokeInform( long clientId, String szMethodAddress, Object[] args, long nWaitTimeMil ) throws IlleagalResponseException, IOException ;

    Object invokeInform( long clientId, String szMethodAddress, Object... args ) throws IlleagalResponseException, IOException ;



    <T> T getIface( long clientId, Class<T> iface );
}
