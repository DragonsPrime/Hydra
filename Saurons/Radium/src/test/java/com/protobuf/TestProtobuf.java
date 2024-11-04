package com.protobuf;

import com.google.protobuf.ByteString;
import com.mc.JesusChrist;
import com.pinecone.Pinecone;
import com.pinecone.framework.system.CascadeSystem;
import com.pinecone.framework.util.Debug;

class Appleby extends JesusChrist {
    public Appleby( String[] args, CascadeSystem parent ) {
        this( args, null, parent );
    }

    public Appleby( String[] args, String szName, CascadeSystem parent ){
        super( args, szName, parent );
    }

    @Override
    public void vitalize () throws Exception {
        RpcRequest request = RpcRequest.newBuilder()
                .setMethod("haha")
                .setPayload( ByteString.copyFrom( new byte[]{123} ) )
                .build();

        byte[] serializedData = request.toByteArray();

        RpcRequest deserializedReq = RpcRequest.parseFrom(serializedData);
        Debug.trace( deserializedReq.getMethod() );
    }


}

public class TestProtobuf {
    public static void main( String[] args ) throws Exception {
        //String[] as = args;
        String[] as = new String[]{ "TestWolfMCClient=true" };
        Pinecone.init( (Object...cfg )->{
            Appleby appleby = (Appleby) Pinecone.sys().getTaskManager().add( new Appleby( as, Pinecone.sys() ) );
            appleby.vitalize();
            return 0;
        }, (Object[]) as );
    }
}