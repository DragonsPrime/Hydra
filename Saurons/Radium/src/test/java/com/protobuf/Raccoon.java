package com.protobuf;

import java.util.Map;

import com.pinecone.hydra.umct.bind.ArgParam;
import com.pinecone.hydra.umct.stereotype.Iface;

//@Iface
public interface Raccoon {
    @Iface
    String scratch( String target, int time );

    @Iface
    default String scratchA( String target, int time, Rabbit rabbit ) {
        return null;
    }

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
