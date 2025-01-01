package com.pinecone.framework.util.json.homotype;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;

import com.pinecone.framework.system.stereotype.JavaBeans;
import com.pinecone.framework.util.StringUtils;
import com.pinecone.framework.util.json.GenericJSONEncoder;
import com.pinecone.framework.util.json.JSON;
import com.pinecone.framework.util.json.JSONEncoder;

public class GenericBeanJSONEncoder implements BeanJSONEncoder {
    public GenericBeanJSONEncoder() {

    }

    @Override
    public String valueJsonify( Object val ) {
        return JSON.stringify( val );
    }

    @Override
    public void valueJsonify( Object val, Writer writer, int nIndentFactor, int nIndentBlankNum ) throws IOException {
        JSONEncoder.BASIC_JSON_ENCODER.write( val, writer, nIndentFactor, nIndentBlankNum );
    }

    @Override
    public String encode( Object bean, Set<String > exceptedKeys ) {
        Class klass = bean.getClass();
        boolean includeSuperClass = klass.getClassLoader() != null;
        Method[] methods = includeSuperClass ? klass.getMethods() : klass.getDeclaredMethods();

        StringBuilder sb = new StringBuilder( "{" );
        for( int i = 0; i < methods.length; ++i ) {
            try {
                Method method = methods[i];
                if ( Modifier.isPublic( method.getModifiers() ) ) {
                    String key = JavaBeans.getGetterMethodKeyName( method );
                    if( !StringUtils.isEmpty( key ) ) {
                        if ( Character.isUpperCase( key.charAt(0) ) && method.getParameterTypes().length == 0 ) {
                            key = JavaBeans.methodKeyNameLowerCaseNormalize( key );

                            if( exceptedKeys != null && exceptedKeys.contains( key ) ) {
                                continue;
                            }

                            Object val;
                            try {
                                val = method.invoke( bean );
                                sb.append( '\"' ).append( key ).append( "\":" );
                            }
                            catch ( IllegalAccessException | InvocationTargetException e ) {
                                continue;
                            }

                            sb.append( this.valueJsonify( val ) );
                            sb.append( ',' );
                        }
                    }
                }
            }
            catch ( Exception e ) {
                e.printStackTrace();
                // Do nothing.
            }
        }

        if( sb.charAt( sb.length() - 1 ) == ',' ) {
            sb.deleteCharAt( sb.length() - 1 );
        }
        sb.append( '}' );

        return sb.toString();

//        StringWriter w = new StringWriter();
//        try {
//            synchronized( w.getBuffer() ) {
//                this.encode( bean, w );
//                return w.toString();
//            }
//        }
//        catch ( IOException e ){
//            return null;
//        }
    }

    @Override
    public String encode( Object bean ) {
        return this.encode( bean, (Set<String >) null );
    }

    @Override
    public void encode( Object bean, Writer writer, int nIndentFactor ) throws IOException {
        this.encode0( bean, writer, nIndentFactor, 0 );
    }

    protected void encode0( Object bean, Writer writer, int nIndentFactor, int nIndentBlankNum ) throws IOException {
        Class<?> klass = bean.getClass();
        boolean includeSuperClass = klass.getClassLoader() != null;
        Method[] methods = includeSuperClass ? klass.getMethods() : klass.getDeclaredMethods();

        writer.write( "{" );
        boolean isFirst = true;

        for ( int i = 0; i < methods.length; ++i ) {
            try {
                Method method = methods[i];
                if ( Modifier.isPublic( method.getModifiers() ) ) {
                    String key = JavaBeans.getGetterMethodKeyName( method );
                    if ( !StringUtils.isEmpty( key ) ) {
                        if ( Character.isUpperCase( key.charAt( 0 ) ) && method.getParameterTypes().length == 0 ) {
                            key = JavaBeans.methodKeyNameLowerCaseNormalize( key );

                            if ( !isFirst ) {
                                writer.write( "," );
                            }

                            int nNewIndent = nIndentBlankNum + nIndentFactor;
                            if ( nNewIndent > 0 ) {
                                writer.write('\n');
                            }
                            GenericJSONEncoder.indentBlank( writer, nNewIndent );


                            Object val;
                            try {
                                val = method.invoke( bean );
                                writer.write( "\"" + key + "\":" );
                            }
                            catch ( IllegalAccessException | InvocationTargetException e ) {
                                continue;
                            }

                            this.valueJsonify( val, writer, nIndentFactor, nNewIndent );
                            isFirst = false;

                            GenericJSONEncoder.indentBlank( writer, nIndentBlankNum );
                        }
                    }
                }
            }
            catch ( Exception e ) {
                e.printStackTrace();
                // Do nothing.
            }
        }

        if ( nIndentFactor > 0 ) {
            writer.write( '\n' );
        }
        writer.write( "}" );
    }

}
