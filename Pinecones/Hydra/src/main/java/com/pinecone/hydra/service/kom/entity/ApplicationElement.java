package com.pinecone.hydra.service.kom.entity;

import com.pinecone.hydra.service.kom.ServiceFamilyNode;

public interface ApplicationElement extends FolderElement, ServiceFamilyNode {
    String getDeploymentMethod();

    void setDeploymentMethod( String deploymentMethod );

    @Override
    default ApplicationElement evinceApplicationElement() {
        return this;
    }
}
