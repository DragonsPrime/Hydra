package com.pinecone.hydra.service.kom.marshaling;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.hydra.service.kom.entity.ElementNode;

public interface ServicesInstrumentEncoder extends Pinenut {
    Object encode( ElementNode node );
}
