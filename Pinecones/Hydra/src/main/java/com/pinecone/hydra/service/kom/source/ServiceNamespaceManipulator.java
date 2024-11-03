package com.pinecone.hydra.service.kom.source;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.service.kom.entity.Namespace;
import com.pinecone.hydra.system.ko.dao.GUIDNameManipulator;

import java.util.List;

public interface ServiceNamespaceManipulator extends GUIDNameManipulator {
    void insert( Namespace ns );

    void remove( GUID guid );

    Namespace getNamespace( GUID guid );

    void update( Namespace ns );

    List<Namespace > fetchNamespaceNodeByName( String name );
}
