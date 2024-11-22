package com.pinecone.framework.lang.field;

import java.util.Arrays;
import java.util.Map;

import com.pinecone.framework.system.prototype.Pinenut;

public interface FieldEntity extends Pinenut {
    String getName();

    Class<?> getType();

    Object getValue();

    void setValue( Object value );

    static FieldEntity[] typeFrom( Map map ) {
        FieldEntity[] entities = new FieldEntity[ map.size() ];
        int i = 0;
        for( Object em : map.entrySet() ) {
            Map.Entry kv = (Map.Entry) em;

            entities[ i ] = new GenericFieldEntity( kv.getKey().toString(), kv.getValue().getClass() );
            ++i;
        }

        return entities;
    }

    static FieldEntity[] from( Map map ) {
        FieldEntity[] entities = new FieldEntity[ map.size() ];
        int i = 0;
        for( Object em : map.entrySet() ) {
            Map.Entry kv = (Map.Entry) em;

            entities[ i ] = new GenericFieldEntity( kv.getKey().toString(), kv.getValue() );
            ++i;
        }

        return entities;
    }

    static FieldEntity[] from( Class<? >[] parameters ) {
        FieldEntity[] entities = new FieldEntity[ parameters.length ];
        int i = 0;
        for( Class<? > parameter : parameters ) {
            entities[ i ] = new GenericFieldEntity(
                    parameter.getName().replace( ".", "_" ) + "_" + i, parameter.getComponentType()
            );
            ++i;
        }

        return entities;
    }

    default FieldEntity[] copy( FieldEntity[] that ) {
        return Arrays.copyOf( that, that.length );
    }
}
