package com.pinecone.ulf.util.protobuf;

import java.util.LinkedHashMap;

import com.pinecone.framework.system.prototype.Pinenut;

public class Options implements Pinenut {
    public static final FileDescriptorFormater DefaultFileDescriptorFormater = new FileDescriptorFormater() {
        @Override
        public String format( Class<?> type ) {
            return type.getName().replace( '.', '_' );
        }
    };

    public static final FileDescriptorFormater DefaultFileDescriptorSimpleFormater = new FileDescriptorFormater() {
        @Override
        public String format( Class<?> type ) {
            return type.getSimpleName();
        }
    };

    public static final Class<?> DefaultMapType = LinkedHashMap.class;

    public static final String DescriptorFileExtend = "$File";

    public static final Options DefaultOptions = new Options();

    public static final Options DefaultSimpleOptions = new Options() {
        @Override
        public String formatFileDescType( Class<?> type ) {
            return this.formatFileDescType( type, Options.DefaultFileDescriptorSimpleFormater );
        }
    };

    protected FileDescriptorFormater mFileDescriptorFormater;

    protected String                 mszDescriptorFileExtend;

    protected Class<?>               mDefaultMapType;

    public Options( FileDescriptorFormater formater, String szDescriptorFileExtend,  Class<?> defaultMapType ) {
        this.mFileDescriptorFormater = formater;
        this.mszDescriptorFileExtend = szDescriptorFileExtend;
        this.mDefaultMapType         = defaultMapType;
    }

    public Options() {
        this( Options.DefaultFileDescriptorFormater, Options.DescriptorFileExtend, Options.DefaultMapType );
    }

    public String formatFileDescType( Class<?> type, FileDescriptorFormater formater ) {
        return formater.format( type );
    }

    public String formatFileDescType( Class<?> type ) {
        return this.formatFileDescType( type, this.mFileDescriptorFormater );
    }

    public String getDescriptorFileExtend() {
        return this.mszDescriptorFileExtend;
    }

    @SuppressWarnings( "unchecked" )
    public <T> Class<T> getDefaultMapType() {
        return (Class<T>) this.mDefaultMapType;
    }
}
