package com.pinecone.hydra.umct.protocol.compiler;

import java.util.ArrayList;
import java.util.List;

public class GenericClassDigest implements ClassDigest {
    protected String                 mszClassName;
    protected List<MethodDigest >    mMethodDigests;

    public GenericClassDigest( String szClassName ) {
        this.mszClassName   = szClassName;
        this.mMethodDigests = new ArrayList<>();
    }

    @Override
    public String getClassName() {
        return this.mszClassName;
    }

    @Override
    public void addMethod( MethodDigest methodDigest ) {
        this.mMethodDigests.add( methodDigest );
    }

    @Override
    public List<MethodDigest> getMethodDigests() {
        return this.mMethodDigests;
    }
}
