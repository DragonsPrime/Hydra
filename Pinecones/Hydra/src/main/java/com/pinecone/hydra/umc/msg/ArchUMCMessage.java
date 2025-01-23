package com.pinecone.hydra.umc.msg;

import com.pinecone.framework.unit.KeyValue;
import com.pinecone.framework.util.json.JSON;
import com.pinecone.framework.util.json.JSONEncoder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public abstract class ArchUMCMessage implements UMCMessage {
    protected UMCHead        mHead                ;

    public ArchUMCMessage( UMCHead head ) {
        this.mHead            = head;
    }

    ArchUMCMessage( Map<String,Object > joExHead, UMCMethod method, long controlBits ) {
        UMCHeadV1 head = new UMCHeadV1();
        head.setControlBits( controlBits );
        head.setMethod( method );
        head.applyExHead( joExHead );
        this.mHead = head;
    }

    ArchUMCMessage( Map<String,Object > joExHead, UMCMethod method ) {
        this( joExHead, method, 0 );
    }

    public ArchUMCMessage( Map<String,Object > joExHead, long controlBits ) {
        this( joExHead, UMCMethod.INFORM, controlBits );
    }

    public ArchUMCMessage( Map<String,Object > joExHead ) {
        this( joExHead, UMCMethod.INFORM );
    }



    protected ArchUMCMessage( Object protoExHead, ExtraEncode encode, UMCMethod method, long controlBits ) {
        UMCHeadV1 head = new UMCHeadV1();
        head.setControlBits( controlBits );
        head.setMethod( method );
        head.setExtraHead( protoExHead );
        head.setExtraEncode( encode );
        this.mHead = head;
    }

    protected ArchUMCMessage( Object protoExHead, UMCMethod method, long controlBits ) {
        this( protoExHead, ExtraEncode.Prototype, method, controlBits );
    }

    protected ArchUMCMessage( Object protoExHead, UMCMethod method ) {
        this( protoExHead, method, 0 );
    }

    protected ArchUMCMessage( Object protoExHead, ExtraEncode encode, UMCMethod method ) {
        this( protoExHead, encode, method, 0 );
    }

    public ArchUMCMessage( Object protoExHead, long controlBits ) {
        this( protoExHead, UMCMethod.INFORM, controlBits );
    }

    public ArchUMCMessage( Object protoExHead ) {
        this( protoExHead, UMCMethod.INFORM );
    }

    public ArchUMCMessage( Object protoExHead, ExtraEncode encode ) {
        this( protoExHead, encode, UMCMethod.INFORM );
    }



    @Override
    public UMCHead     getHead() {
        return this.mHead;
    }

    @Override
    public Object    getExHead() {
        return this.mHead.getExtraHead();
    }

    @Override
    public long        getMessageLength(){
        return UMCHeadV1.HeadBlockSize + this.mHead.getExtraHeadLength() + this.mHead.getBodyLength();
    }

    @Override
    public long        queryMessageLength(){
        this.mHead.inface().transApplyExHead();
        return this.getMessageLength();
    }

    @Override
    public void        release() {
        this.mHead        = null;
    }

    @Override
    public String      toString() {
        return this.toJSONString();
    }

    @Override
    public String      toJSONString() {
        return JSONEncoder.stringifyMapFormat( new KeyValue[]{
                new KeyValue<>( "Head"           , this.getHead().getExtraHead()                           ),
                new KeyValue<>( "Method"         , this.getHead().getMethod()                              ),
                new KeyValue<>( "BodyLength"     , this.getHead().getBodyLength()                          ),
                new KeyValue<>( "Status"         , this.getHead().getStatus()                              )
        } );
    }

}
