package com.pinecone.framework.util.json.homotype;

import com.pinecone.framework.system.functions.Function;

public class DirectObjectInjector extends ObjectInjector {
    protected String      mszFieldNS        = ""    ;
    protected boolean     mbUsingHungary    = false ;
    protected Function    mfnGetFieldName   = null  ;


    public DirectObjectInjector( String szFieldNS, Class stereotype ){
        super( stereotype );
        this.mszFieldNS  = szFieldNS;
    }

    public DirectObjectInjector( boolean bUsingHungary, Class stereotype ) {
        super( stereotype );
        this.mbUsingHungary = bUsingHungary;
        if( this.mbUsingHungary ){
            this.mszFieldNS = "m";
        }
    }

    public DirectObjectInjector( Function fnGetFieldName, Class stereotype ) {
        super( stereotype );
        this.mfnGetFieldName = fnGetFieldName;
    }

    public DirectObjectInjector( Class stereotype ) {
        this( "", stereotype );
    }

    @Override
    protected String getFieldName( String szKey ){
        if( this.mfnGetFieldName != null ) {
            try {
                szKey = (String) this.mfnGetFieldName.invoke( szKey );
            }
            catch ( Exception e ) {
                return szKey;
            }
        }
        else {
            if( !this.mszFieldNS.isEmpty() ){
                StringBuilder sb = new StringBuilder();
                sb.append( szKey );

                if( this.mbUsingHungary ){
                    sb.setCharAt( 0, Character.toUpperCase( sb.charAt(0) ) );
                }
                szKey = this.mszFieldNS + sb.toString();
            }
        }

        return szKey;
    }

    public String getFieldNamespace() {
        return this.mszFieldNS;
    }

    public void setFieldNamespace( String ns ) {
        this.mszFieldNS = ns;
    }


    public static DirectObjectInjector instance( boolean bUsingHungary, Class stereotype ) {
        return new DirectObjectInjector( bUsingHungary, stereotype );
    }

    public static DirectObjectInjector instance( String szFieldNS, Class stereotype ) {
        return new DirectObjectInjector( szFieldNS, stereotype );
    }

    public static DirectObjectInjector instance( Function fnGetFieldName, Class stereotype ) {
        return new DirectObjectInjector( fnGetFieldName, stereotype );
    }
}
