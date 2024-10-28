package com.pinecone.hydra.service.kom.entity;

import com.pinecone.framework.util.json.hometype.BeanJSONDecoder;
import com.pinecone.hydra.service.kom.ServicesInstrument;

import java.util.Map;
import java.util.Set;

public class GenericServiceElement extends ArchServoElement implements ServiceElement {
    protected String                     serviceType;

    public GenericServiceElement() {
        super();
    }

    public GenericServiceElement( Map<String, Object > joEntity ) {
        super( joEntity );
        BeanJSONDecoder.BasicDecoder.decode( this, joEntity );
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