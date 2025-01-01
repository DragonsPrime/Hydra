package com.pinecone.hydra.umct.husky;

public abstract class ArchAddressedEntity implements MessagePackage {
    protected String mszInterceptedPath;

    public ArchAddressedEntity( String szInterceptedPath ) {
        this.mszInterceptedPath = szInterceptedPath;
    }

    @Override
    public String getInterceptedPath() {
        return this.mszInterceptedPath;
    }

    @Override
    public String getInterceptorName() {
        String[] debris = this.mszInterceptedPath.split( MessagePackage.StandardPathSeparator );
        if( debris.length > 1 ) {
            return debris [ debris.length - 1 ];
        }
        return this.mszInterceptedPath;
    }

    @Override
    public String getAddressPath() {
        String interceptor = this.getInterceptorName();
        int lastIndexof = this.mszInterceptedPath.lastIndexOf( interceptor );
        if( lastIndexof > 0 ) {
            return this.mszInterceptedPath.substring( 0, lastIndexof );
        }
        return this.mszInterceptedPath;
    }
}
