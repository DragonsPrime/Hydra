package com.pinecone.hydra.umct;

import java.util.List;

import com.pinecone.framework.lang.field.FieldEntity;
import com.pinecone.framework.system.functions.Function;

public interface MessageHandler extends Function {
    String getAddressMapping();

    @Override
    Object invoke( Object... args ) throws Exception;

    List<String > getArgumentsKey();

    default boolean isArgsIndexed() {
        return this.getArgumentsKey() == null;
    }

    Object getReturnDescriptor();

    Object getArgumentsDescriptor();

    FieldEntity[] getArgumentTemplate();
}
