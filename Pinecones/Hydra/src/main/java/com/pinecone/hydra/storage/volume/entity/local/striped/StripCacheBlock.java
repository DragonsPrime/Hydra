package com.pinecone.hydra.storage.volume.entity.local.striped;

import com.pinecone.hydra.storage.volume.entity.LogicVolume;

import java.nio.ByteBuffer;

public class StripCacheBlock implements CacheBlock{
    protected CacheBlockStatus    status;
    protected Number              validByteStart;
    protected Number              validByteEnd;
    protected int                 cacheBlockNumber;
    protected Number              byteStart;
    protected Number              byteEnd;
    protected int                 bufferWriteThreadId;

    protected LogicVolume         volume;

    public StripCacheBlock( int cacheBlockNumber, Number byteStart, Number byteEnd ){
        this.status = CacheBlockStatus.Free;
        this.byteStart = byteStart;
        this.byteEnd = byteEnd;
        this.cacheBlockNumber = cacheBlockNumber;
    }


    @Override
    public CacheBlockStatus getStatus() {
        return this.status;
    }

    @Override
    public void setStatus(CacheBlockStatus status) {
        this.status = status;
    }

    @Override
    public Number getValidByteStart() {
        return this.validByteStart;
    }

    @Override
    public void setValidByteStart(Number validByteStart) {
        this.validByteStart = validByteStart;
    }

    @Override
    public Number getValidByteEnd() {
        return this.validByteEnd;
    }

    @Override
    public void setValidByteEnd(Number validByteEnd) {
        this.validByteEnd = validByteEnd;
    }

    @Override
    public Number getByteStart() {
        return this.byteStart;
    }

    @Override
    public void setByteStart(Number byteStart) {
        this.byteStart = byteStart;
    }

    @Override
    public Number getByteEnd() {
        return this.byteEnd;
    }

    @Override
    public void setByteEnd(Number byteEnd) {
        this.byteEnd = byteEnd;
    }

    @Override
    public int getCacheBlockNumber() {
        return this.cacheBlockNumber;
    }

    @Override
    public void setCacheBlockNumber(int cacheBlockNumber) {
        this.cacheBlockNumber = cacheBlockNumber;
    }

    @Override
    public int getBufferWriteThreadId() {
        return this.bufferWriteThreadId;
    }

    @Override
    public void setBufferWriteThreadId(int bufferWriteThreadId) {
        this.bufferWriteThreadId = bufferWriteThreadId;
    }

    @Override
    public LogicVolume getVolume() {
        return this.volume;
    }

    @Override
    public void setVolume(LogicVolume volume) {
        this.volume = volume;
    }
}
