package com.pinecone.hydra.umct;

import java.util.List;

import com.pinecone.framework.lang.field.FieldEntity;

public class UlfMessageHandler implements MessageHandler {
    @Override
    public String getAddressMapping() {
        return null;
    }

    @Override
    public Object invoke( Object... args ) throws Exception {
        return null;
    }

    @Override
    public List<String > getArgumentsKey() {
        return null;
    }

    @Override
    public Object getReturnDescriptor() {
        return null;
    }

    @Override
    public Object getArgumentsDescriptor() {
        return null;
    }

    @Override
    public FieldEntity[] getArgumentTemplate() {
        return new FieldEntity[0];
    }
}
