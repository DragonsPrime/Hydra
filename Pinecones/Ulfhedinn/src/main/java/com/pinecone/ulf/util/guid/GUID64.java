package com.pinecone.ulf.util.guid;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.id.Identification;
import com.pinecone.framework.util.id.IllegalIdentificationException;

public class GUID64 implements GUID {
    public static final long SignBits      =  1;
    public static final long TimestampBits = 29;
    public static final long WorkerIdBits  = 21;
    public static final long SequenceBits  = 13;

    protected long guid;

    public GUID64() {

    }

    public GUID64( String hexID64 ) {
        this.parse( hexID64 );
    }

    public GUID64( long guid ) {
        this.guid = guid;
    }

    public long getSequence() {
        long totalBits     = BitsAllocator.TOTAL_BITS;
        return (this.guid << (totalBits - GUID64.SequenceBits)) >>> (totalBits - GUID64.SequenceBits);
    }

    public long getWorkerId() {
        long totalBits     = BitsAllocator.TOTAL_BITS;
        return (this.guid << (GUID64.TimestampBits + GUID64.SignBits)) >>> (totalBits - GUID64.WorkerIdBits);
    }

    public long getDeltaSeconds() {
        return this.guid >>> (GUID64.WorkerIdBits + GUID64.SequenceBits);
    }


    protected void parseByStringParts( String[] parts ) throws IllegalIdentificationException {
        try{
            // 将十六进制字符串转换为十进制整数
            long deltaSeconds = Long.parseLong(parts[0], 16);
            long workerId     = Long.parseLong(parts[1], 16);
            long sequence     = Long.parseLong(parts[2], 16);

            long deltaSecondsPart = deltaSeconds << (GUID64.WorkerIdBits + GUID64.SequenceBits);
            long workerIdPart = workerId << GUID64.SequenceBits;
            this.guid = deltaSecondsPart | workerIdPart | sequence;
        }
        catch ( RuntimeException e ) {
            throw new IllegalIdentificationException( e );
        }
    }

    @Override
    public Identification parse( String hexID64 ) throws IllegalIdentificationException {
        // 分离UUID的各个部分
        String[] parts = hexID64.split("-");

        this.parseByStringParts( parts );
        return this;
    }

    @Override
    public String toString(){
        String deltaSecondsHex = String.format( "%07x", this.getDeltaSeconds() );
        String workerIdHex     = String.format( "%06x", this.getWorkerId()     );
        String sequenceHex     = String.format( "%04x", this.getSequence()     );
        return deltaSecondsHex + "-" + workerIdHex + "-" + sequenceHex;
    }

    @Override
    public String toJSONString() {
        return "\"" + this.toString() + "\"";
    }

    @Override
    public boolean equals( Object obj ) {
        if( !super.equals(obj) )  {
            if( obj instanceof GUID64 ) {
                return this.guid == ((GUID64) obj).guid;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Long.hashCode( this.guid );
    }
}
