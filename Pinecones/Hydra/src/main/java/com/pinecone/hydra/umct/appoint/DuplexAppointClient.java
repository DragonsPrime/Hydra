package com.pinecone.hydra.umct.appoint;

import java.io.IOException;

import com.pinecone.hydra.umc.wolfmc.UlfAsyncMsgHandleAdapter;
import com.pinecone.hydra.umct.UMCTExpressHandler;
import com.pinecone.hydra.umct.husky.machinery.RouteDispatcher;

public interface DuplexAppointClient extends AppointClient, DuplexAppointNode {

    void createPassiveChannel( int nLine );

    void embraces( int nLine, UlfAsyncMsgHandleAdapter handler ) throws IOException;

    void embraces( int nLine, UMCTExpressHandler handler ) throws IOException;

    void embraces( int nLine ) throws IOException ;

    RouteDispatcher getRouteDispatcher();

}
