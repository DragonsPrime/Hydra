package com.pinecone.hydra.service.kom.source;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.service.kom.ServicesInstrument;
import com.pinecone.hydra.service.kom.entity.ApplicationElement;

public interface ApplicationMetaManipulator extends Pinenut {
    void insert( ApplicationElement applicationElement );

    void remove( GUID guid );

    ApplicationElement getApplicationElement( GUID guid, ServicesInstrument servicesInstrument );

    void update( ApplicationElement applicationElement );
}
