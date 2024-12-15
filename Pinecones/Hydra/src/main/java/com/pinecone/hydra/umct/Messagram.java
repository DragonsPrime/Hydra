package com.pinecone.hydra.umct;

import java.util.Map;

public interface Messagram extends MessageJunction {
    Messagram addExpress( MessageExpress express );

    MessageExpress getExpressByName( String name );

    Messagram removeExpress(  String name  );

    Map<String, Object > getProtoConfig();

    Map<String, Object > getExpressesConfig();
}
