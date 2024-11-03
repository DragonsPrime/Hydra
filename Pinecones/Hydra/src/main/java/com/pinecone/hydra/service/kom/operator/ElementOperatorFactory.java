package com.pinecone.hydra.service.kom.operator;

import com.pinecone.hydra.service.kom.ServicesInstrument;
import com.pinecone.hydra.service.kom.entity.ApplicationElement;
import com.pinecone.hydra.service.kom.entity.Namespace;
import com.pinecone.hydra.service.kom.entity.ServiceElement;
import com.pinecone.hydra.service.kom.source.ServiceMasterManipulator;
import com.pinecone.hydra.unit.udtt.operator.OperatorFactory;
import com.pinecone.hydra.unit.udtt.operator.TreeNodeOperator;

public interface ElementOperatorFactory extends OperatorFactory {
    String DefaultServiceNode     =  ServiceElement.class.getSimpleName();
    String DefaultNamespace       =  Namespace.class.getSimpleName();
    String DefaultApplicationNode =  ApplicationElement.class.getSimpleName();

    void register( String typeName, TreeNodeOperator functionalNodeOperation );

    void registerMetaType( Class<?> clazz, String metaType );

    void registerMetaType( String classFullName, String metaType );

    String getMetaType( String classFullName );

    ElementOperator getOperator(String typeName );

    ServicesInstrument getServicesTree();

    ServiceMasterManipulator getServiceMasterManipulator();

}
