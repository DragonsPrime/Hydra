package com.pinecone.hydra.service.kom.source;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.service.kom.entity.ApplicationElement;
import com.pinecone.hydra.service.kom.entity.GenericApplicationElement;
import com.pinecone.hydra.system.ko.dao.GUIDNameManipulator;

import java.util.List;

public interface ApplicationNodeManipulator extends GUIDNameManipulator {
    void insert( ApplicationElement applicationElement );

    void remove( GUID guid);

    ApplicationElement getApplicationNode(GUID guid);

    void update( ApplicationElement applicationElement );

    List<ApplicationElement> fetchApplicationNodeByName( String name );
}
