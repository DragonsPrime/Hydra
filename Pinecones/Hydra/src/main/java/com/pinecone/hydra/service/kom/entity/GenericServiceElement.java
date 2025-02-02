package com.pinecone.hydra.service.kom.entity;

import com.pinecone.framework.util.json.homotype.BeanMapDecoder;
import com.pinecone.hydra.service.kom.ServicesInstrument;

import java.util.Map;

public class GenericServiceElement extends ArchServoElement implements ServiceElement {
    protected String                     serviceType;

    public GenericServiceElement() {
        super();
    }

    public GenericServiceElement( Map<String, Object > joEntity ) {
        super( joEntity );
        BeanMapDecoder.BasicDecoder.decode( this, joEntity );
    }

    public GenericServiceElement( Map<String, Object > joEntity, ServicesInstrument servicesInstrument ) {
        super( joEntity, servicesInstrument );
        BeanMapDecoder.BasicDecoder.decode( this, joEntity );
    }

    public GenericServiceElement( ServicesInstrument servicesInstrument ) {
        super( servicesInstrument );
    }

    @Override
    public String getServiceType() {
        return this.serviceType;
    }

    @Override
    public void setServiceType( String serviceType ) {
        this.serviceType = serviceType;
    }

}