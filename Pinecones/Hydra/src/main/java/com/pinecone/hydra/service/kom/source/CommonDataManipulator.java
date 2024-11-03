package com.pinecone.hydra.service.kom.source;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.service.kom.ServiceFamilyNode;
import com.pinecone.hydra.service.kom.entity.CommonMeta;
import com.pinecone.hydra.service.kom.entity.Namespace;

public interface CommonDataManipulator extends Pinenut {
    void insert( ServiceFamilyNode node );

    void insertNS( Namespace node );

    void remove( GUID guid );

    CommonMeta getNodeCommonData( GUID guid );

    void update( ServiceFamilyNode node );
}
