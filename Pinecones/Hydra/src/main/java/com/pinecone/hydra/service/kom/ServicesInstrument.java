package com.pinecone.hydra.service.kom;

import com.pinecone.hydra.service.kom.entity.ApplicationElement;
import com.pinecone.hydra.service.kom.entity.Namespace;
import com.pinecone.hydra.service.kom.entity.ServiceElement;
import com.pinecone.hydra.service.kom.entity.ServiceTreeNode;
import com.pinecone.hydra.system.ko.kom.KOMInstrument;

public interface ServicesInstrument extends KOMInstrument {

    ServiceConfig KernelServiceConfig = new KernelServiceConfig();

    ApplicationElement affirmApplication ( String path );

    Namespace          affirmNamespace   ( String path );

    ServiceElement     affirmService     ( String path );

    ServiceTreeNode    queryElement      ( String path );

}
