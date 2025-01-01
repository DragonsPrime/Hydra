package com.pinecone.framework.system.construction;

import java.util.List;

public interface InstanceManufacturer extends InstanceDispenser {
    @Override
    InstanceManufacturer register( Class<?> type ) ;

    void onlyRegister( Class<?> type ) ;

    InstanceManufacturer registers( List<Class<?> > types );

    List<Class<?>> fetchRegistered();

    String[] fetchRegisteredNames();

    default InstanceManufacturer registerInstancing( Class<?> type ) {
        return this.registerInstancing( type, null );
    }

    InstanceManufacturer registerInstancing( Class<?> type, Object instance ) ;

    Object allotInstance( String type );

    Object autowire( Object that );

    void close();

    void refresh();
}
