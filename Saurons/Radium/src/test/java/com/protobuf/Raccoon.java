package com.protobuf;

import com.pinecone.hydra.umct.bind.ArgParam;
import com.pinecone.hydra.umct.stereotype.Iface;

//@Iface
public interface Raccoon {
    @Iface
    String scratch( String target, int time );

    @Iface
    default void scratchV( String target, int time ) {

    }

//    @Iface( name = "scratchF1" )
//    default String scratch( String target, long[] times ) {
//        return null;
//    }
//
//    @Iface
//    default void nil() {
//
//    }
}
