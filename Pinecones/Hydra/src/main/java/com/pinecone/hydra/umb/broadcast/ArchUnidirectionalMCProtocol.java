package com.pinecone.hydra.umb.broadcast;

import java.util.Map;

import com.pinecone.hydra.umc.msg.Medium;
import com.pinecone.hydra.umc.msg.UMCHead;
import com.pinecone.hydra.umc.msg.UMCHeadV1;
import com.pinecone.hydra.umc.msg.UMCProtocol;

public class ArchUnidirectionalMCProtocol implements UMCProtocol {

    protected String        mszVersion     = UMCHeadV1.ProtocolVersion;

    protected String        mszSignature   = UMCHeadV1.ProtocolSignature;

    protected Medium        mMessageSource ;

    public ArchUnidirectionalMCProtocol( Medium messageSource ) {
        this.mMessageSource = messageSource;
        this.applyMessageSource( messageSource );
    }

    @Override
    public UMCProtocol applyMessageSource( Medium medium ) {
        this.mMessageSource = medium;
        return this;
    }

    @Override
    public Medium getMessageSource() {
        return this.mMessageSource;
    }

    @Override
    public String getVersion(){
        return this.mszVersion;
    }

    @Override
    public String getSignature() {
        return this.mszSignature;
    }

    @Override
    public void setHead( UMCHead head ) {

    }

    @Override
    public UMCHead getHead() {
        return null;
    }

    @Override
    public void setHead( String szKey, Object val ) {

    }

    @Override
    public Object getHead( String szKey ) {
        return null;
    }

    @Override
    public void setExHead( Map<String, Object > jo ) {

    }

    @Override
    public void setExHead( Object jo ) {

    }

    @Override
    public void release() {

    }
}
