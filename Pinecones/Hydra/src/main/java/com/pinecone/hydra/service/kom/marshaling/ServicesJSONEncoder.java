package com.pinecone.hydra.service.kom.marshaling;


import com.pinecone.hydra.service.kom.ServicesInstrument;
import com.pinecone.hydra.service.kom.entity.ElementNode;

public class ServicesJSONEncoder implements ServicesInstrumentEncoder {
    protected ServicesInstrument instrument;

    public ServicesJSONEncoder( ServicesInstrument instrument ) {
        this.instrument = instrument;
    }

    @Override
    public Object encode( ElementNode node ) {
        return node.toJSONObject();
    }

}