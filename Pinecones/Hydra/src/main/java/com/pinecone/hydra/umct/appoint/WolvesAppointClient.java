package com.pinecone.hydra.umct.appoint;

import java.io.IOException;
import java.util.Map;

import com.pinecone.framework.unit.LinkedTreeMap;
import com.pinecone.framework.util.Debug;
import com.pinecone.hydra.umc.msg.ChannelControlBlock;
import com.pinecone.hydra.umc.msg.ChannelPool;
import com.pinecone.hydra.umc.msg.Medium;
import com.pinecone.hydra.umc.msg.UMCMessage;
import com.pinecone.hydra.umc.wolfmc.UlfAsyncMsgHandleAdapter;
import com.pinecone.hydra.umc.wolfmc.UlfInstructMessage;
import com.pinecone.hydra.umc.wolfmc.WolfMCStandardConstants;
import com.pinecone.hydra.umc.wolfmc.client.UlfAsyncMessengerChannelControlBlock;
import com.pinecone.hydra.umc.wolfmc.client.UlfClient;
import com.pinecone.hydra.umct.husky.HuskyCTPConstants;
import com.pinecone.hydra.umct.husky.compiler.BytecodeIfacCompiler;
import com.pinecone.hydra.umct.husky.compiler.CompilerEncoder;
import com.pinecone.hydra.umct.husky.compiler.InterfacialCompiler;
import com.pinecone.hydra.umct.mapping.BytecodeControllerInspector;
import com.pinecone.hydra.umct.mapping.ControllerInspector;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.util.AttributeKey;
import javassist.ClassPool;

public class WolvesAppointClient extends WolfAppointClient implements DuplexAppointClient {
    protected Map<ChannelId, ChannelControlBlock > mInstructedChannels;  // Standby controlled channels, waiting for server to instruct.

    public WolvesAppointClient( UlfClient messenger, InterfacialCompiler compiler, ControllerInspector controllerInspector ){
        super( messenger, compiler, controllerInspector );
        this.mInstructedChannels = new LinkedTreeMap<>();
    }

    public WolvesAppointClient( UlfClient messenger, CompilerEncoder encoder ){
        this( messenger, new BytecodeIfacCompiler(
                ClassPool.getDefault(), messenger.getTaskManager().getClassLoader(), encoder
        ), new BytecodeControllerInspector(
                ClassPool.getDefault(), messenger.getTaskManager().getClassLoader()
        ) );
    }

    public WolvesAppointClient( UlfClient messenger ){
        this( messenger, new BytecodeIfacCompiler(
                ClassPool.getDefault(), messenger.getTaskManager().getClassLoader()
        ), new BytecodeControllerInspector(
                ClassPool.getDefault(), messenger.getTaskManager().getClassLoader()
        ) );
    }

    @Override
    public boolean supportDuplex() {
        return true;
    }

    @Override
    public void embraces( int nLine ) throws IOException {
        // Join us, embracing uniformity.

        this.createPassiveChannel( nLine );
        for ( Map.Entry<ChannelId, ChannelControlBlock > kv : this.mInstructedChannels.entrySet() ) {
            UlfInstructMessage instructMessage = new UlfInstructMessage( HuskyCTPConstants.HCTP_DUP_CONTROL_REGISTER );
            instructMessage.getHead().setIdentityId( this.mMessenger.getMessageNodeId() );

            ChannelControlBlock ccb = kv.getValue();
            UlfAsyncMessengerChannelControlBlock cb = (UlfAsyncMessengerChannelControlBlock) ccb;
            cb.getChannel().getNativeHandle().attr( AttributeKey.valueOf( WolfMCStandardConstants.CB_ASYNC_MSG_HANDLE_KEY ) ).set(new UlfAsyncMsgHandleAdapter() {
                @Override
                public void onSuccessfulMsgReceived( Medium medium, ChannelControlBlock block, UMCMessage msg, ChannelHandlerContext ctx, Object rawMsg ) throws Exception {
                    Debug.redf( msg );
                }
            });
            cb.sendAsynMsg( instructMessage, true );
        }
    }

    @Override
    public void createPassiveChannel( int nLine ) {
        ChannelPool pool = this.getMessageNode().getChannelPool();

        ChannelControlBlock[] cbs = new ChannelControlBlock[ nLine ];
        for ( int i = 0; i < nLine; ++i ) {
            ChannelControlBlock ccb = pool.depriveIdleChannel();
            if ( ccb == null ) {
                for ( int j = 0; j < nLine; ++j ) {
                    if ( cbs[ j ] == null ) {
                        break;
                    }
                    ChannelId id = (ChannelId)cbs[ j ].getChannel().getChannelID();
                    this.mInstructedChannels.remove( id );
                    pool.add( cbs[ j ] );
                }
                throw new IllegalArgumentException( "Creating `PassiveChannel` is compromised due to insufficient free channels. Consider setting up sufficient parallel channels." );
            }

            ChannelId id = (ChannelId)ccb.getChannel().getChannelID();
            cbs[ i ] = ccb;
            this.mInstructedChannels.put( id, ccb );
        }
    }
}
