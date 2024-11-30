package com.protobuf;

import com.pinecone.hydra.umct.stereotype.Iface;

//@Iface
public interface Raccoon {
    @Iface
    String scratch( String target, long time );

    @Iface( name = "scratchF1" )
    default String scratch( String target, long[] times ) {
        return null;
    }

    @Iface
    default void nil() {

    }
}
