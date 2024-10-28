package com.pinecone.hydra.service.kom.entity;

public interface ServiceElement extends ServoElement {
    @Override
    default ServiceElement evinceServiceElement() {
        return this;
    }

    String getServiceType();

    void setServiceType( String serviceType );
}