package com.pinecone.hydra.umct;

import com.pinecone.hydra.servgram.Servgram;

import java.util.Map;

public interface Messagram extends Servgram {
    Messagram addExpress( MessageExpress express );

    MessageExpress getExpressByName( String name );

    Messagram removeExpress(  String name  );

    Map<String, Object > getProtoConfig();

    Map<String, Object > getExpressesConfig();
}
