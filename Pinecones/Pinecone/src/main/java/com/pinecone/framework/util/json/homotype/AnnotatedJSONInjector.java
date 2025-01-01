package com.pinecone.framework.util.json.homotype;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class AnnotatedJSONInjector extends JSONInjector {
    public AnnotatedJSONInjector() {
        super();
    }

    @Override
    protected String getFieldName( Field field ){
        String szKey = AnnotatedJSONInjector.getAnnotatedKey( field );
        if( szKey == null ) {
            return null;
        }
        else if( szKey.isEmpty() ) {
            return field.getName();
        }

        return szKey;
    }

    public static String getAnnotatedKey( Field field ) {
        String szKey = null;

        Annotation[] annotations = field.getAnnotations();
        for ( Annotation a : annotations ) {
            if( a instanceof JSONGet ) {
                szKey = ( (JSONGet) a ).value();
                break;
            }
            else if( a instanceof MapStructure ) {
                szKey = ( (MapStructure) a ).value();
                break;
            }
        }
        return szKey;
    }

    public static AnnotatedJSONInjector instance() {
        return new AnnotatedJSONInjector();
    }
}
