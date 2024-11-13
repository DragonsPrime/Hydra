package com.pinecone.hydra.storage.volume.entity.local.striped;

public class StripCacheBlock implements CacheBlock{
    private byte[]              cache;
    private CacheBlockStatus    status;
    private Number              validByteStart;
    private Number              validByteEnd;
    private int                 cacheBlockNumber;

    public StripCacheBlock( Number cacheSize, int cacheBlockNumber ){
        this.cache = new byte[cacheSize.intValue()];
        this.status = CacheBlockStatus.free;
        this.cacheBlockNumber = cacheBlockNumber;
    }

    @Override
    public byte[] getCache() {
        return cache;
    }

    @Override
    public void setCache(byte[] cache) {
        this.cache = cache;
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
    public int getCacheBlockNumber() {
        return this.cacheBlockNumber;
    }

    @Override
    public void setCacheBlockNumber(int cacheBlockNumber) {
        this.cacheBlockNumber = cacheBlockNumber;
    }
}
