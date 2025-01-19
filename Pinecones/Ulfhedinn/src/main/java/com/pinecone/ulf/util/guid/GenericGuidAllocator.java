package com.pinecone.ulf.util.guid;

import com.pinecone.framework.util.StringUtils;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.id.GuidGenerateException;
import com.pinecone.ulf.util.guid.utils.DateUtils;
import com.pinecone.ulf.util.guid.worker.GenericDisposableWorkerIdAssigner;
import com.pinecone.ulf.util.guid.worker.WorkerIdAssigner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class GenericGuidAllocator implements GuidAllocator72, InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(GenericGuidAllocator.class);

    /** Bits allocate */
    protected int timeBits = 28;
    protected int workerBits = 22;
    protected int seqBits = 13;

    /** Customer epoch, unit as second. For example 2016-05-20 (ms: 1463673600000)*/
    // TODO
    protected String epochStr = "2024-10-01";
    protected long epochSeconds = TimeUnit.MILLISECONDS.toSeconds( DateUtils.parseByDayPattern( this.epochStr ).getTime() );

    /** Stable fields after spring bean initializing */
    protected BitsAllocator bitsAllocator;
    protected long workerId;

    /** Volatile fields caused by nextId() */
    protected long sequence = 0L;
    protected long lastSecond = -1L;

    /** Spring property */
    protected WorkerIdAssigner workerIdAssigner;

    public GenericGuidAllocator() {
        this( new GenericDisposableWorkerIdAssigner() );
    }

    public GenericGuidAllocator( WorkerIdAssigner idAssigner ) {
        this.workerIdAssigner = idAssigner;
        this.afterPropertiesSet();
    }

    @Override
    public void afterPropertiesSet() {
        // initialize bits allocator
        this.bitsAllocator = new BitsAllocator(this.timeBits, this.workerBits, this.seqBits);

        // initialize worker id
        this.workerId = this.workerIdAssigner.assignWorkerId();
        if ( this.workerId > this.bitsAllocator.getMaxWorkerId() ) {
            throw new IllegalStateException( "Worker id " + this.workerId + " exceeds the max " + this.bitsAllocator.getMaxWorkerId() );
        }

        LOGGER.info( "Initialized bits(1, {}, {}, {}) for workerID:{}", this.timeBits, this.workerBits, this.seqBits, this.workerId );
    }

    @Override
    public long getGUID64() throws GuidGenerateException {
        try {
            return this.nextId();
        }
        catch ( Exception e ) {
            LOGGER.error("Generate unique id exception. ", e);
            throw new GuidGenerateException(e);
        }
    }

    @Override
    public String parseGUID64( long guid64 ) {
        long totalBits = BitsAllocator.TOTAL_BITS;
        long signBits = this.bitsAllocator.getSignBits();
        long timestampBits = this.bitsAllocator.getTimestampBits();
        long workerIdBits = this.bitsAllocator.getWorkerIdBits();
        long sequenceBits = this.bitsAllocator.getSequenceBits();

        // parse UID
        long sequence = (guid64 << (totalBits - sequenceBits)) >>> (totalBits - sequenceBits);
        long workerId = (guid64 << (timestampBits + signBits)) >>> (totalBits - workerIdBits);
        long deltaSeconds = guid64 >>> (workerIdBits + sequenceBits);

        Date thatTime = new Date(TimeUnit.SECONDS.toMillis(epochSeconds + deltaSeconds));
        String thatTimeStr = DateUtils.formatByDateTimePattern(thatTime);

        // format as string
        return String.format("{\"UID\":\"%d\",\"timestamp\":\"%s\",\"workerId\":\"%d\",\"sequence\":\"%d\"}",
                guid64, thatTimeStr, workerId, sequence);
    }

//    @Override
//    public GUID64 parseGUID64(long uid) {
//        long totalBits = BitsAllocator.TOTAL_BITS;
//        long signBits = bitsAllocator.getSignBits();
//        long timestampBits = bitsAllocator.getTimestampBits();
//        long workerIdBits = bitsAllocator.getWorkerIdBits();
//        long sequenceBits = bitsAllocator.getSequenceBits();
//
//        // parse UID
//        long sequence = (uid << (totalBits - sequenceBits)) >>> (totalBits - sequenceBits);
//        long workerId = (uid << (timestampBits + signBits)) >>> (totalBits - workerIdBits);
//        long deltaSeconds = uid >>> (workerIdBits + sequenceBits);
//
//        Date thatTime = new Date(TimeUnit.SECONDS.toMillis(epochSeconds + deltaSeconds));
//        String thatTimeStr = DateUtils.formatByDateTimePattern(thatTime);
//
//        // format as string
//        return new GUID64(sequence, workerId, deltaSeconds);
//    }
//


    @Override
    public GUID nextGUID() {
        return this.nextGUID72();
    }

    @Override
    public GUID nextGUID72() {
        //先获取GUID64
        long guid64 = this.getGUID64();
        //Debug.trace( guid64 );

        //获取纳秒种子
        LocalDateTime now = LocalDateTime.now();
        long nanoseconds = now.toLocalTime().truncatedTo( ChronoUnit.NANOS ).getNano();
        int truncatedNanos = (int) (nanoseconds % 256L); // 截取为8位
        //String nanoSeed = String.format("%02x", truncatedNanos);

        return new GUID72( guid64, (byte) truncatedNanos );
    }

    @Override
    public GUID nextGUID64() {
        return new GUID64( this.getGUID64() );
    }

    /**
     * Get UID
     *
     * @return UID
     * @throws GuidGenerateException in the case: Clock moved backwards; Exceeds the max timestamp
     */
    protected synchronized long nextId() {
        long currentSecond = getCurrentSecond();

        // Clock moved backwards, refuse to generate uid
        if (currentSecond < this.lastSecond) {
            long refusedSeconds = this.lastSecond - currentSecond;
            throw new GuidGenerateException("Clock moved backwards. Refusing for %d seconds", refusedSeconds);
        }

        // At the same second, increase sequence
        if (currentSecond == this.lastSecond) {
            this.sequence = ( this.sequence + 1 ) & this.bitsAllocator.getMaxSequence();
            // Exceed the max sequence, we wait the next second to generate uid
            if ( this.sequence == 0 ) {
                currentSecond = this.getNextSecond( this.lastSecond );
            }

            // At the different second, sequence restart from zero
        }
        else {
            this.sequence = 0L;
        }

        this.lastSecond = currentSecond;

        // Allocate bits for UID
        return this.bitsAllocator.allocate(currentSecond - epochSeconds, this.workerId, this.sequence);
    }

    /**
     * Get next millisecond
     */
    private long getNextSecond( long lastTimestamp ) {
        long timestamp = getCurrentSecond();
        while (timestamp <= lastTimestamp) {
            timestamp = getCurrentSecond();
        }

        return timestamp;
    }

    /**
     * Get current second
     */
    private long getCurrentSecond() {
        long currentSecond = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        if (currentSecond - epochSeconds > this.bitsAllocator.getMaxDeltaSeconds()) {
            throw new GuidGenerateException("Timestamp bits is exhausted. Refusing UID generate. Now: " + currentSecond);
        }

        return currentSecond;
    }

    @Override
    public void setWorkerIdAssigner( WorkerIdAssigner workerIdAssigner ) {
        this.workerIdAssigner = workerIdAssigner;
    }

    public void setTimeBits   ( int timeBits    ) {
        if (timeBits > 0) {
            this.timeBits = timeBits;
        }
    }

    public void setWorkerBits ( int workerBits  ) {
        if (workerBits > 0) {
            this.workerBits = workerBits;
        }
    }

    public void setSeqBits    ( int seqBits     ) {
        if (seqBits > 0) {
            this.seqBits = seqBits;
        }
    }

    public void setEpochStr   ( String epochStr ) {
        if ( StringUtils.isNotBlank(epochStr) ) {
            this.epochStr = epochStr;
            this.epochSeconds = TimeUnit.MILLISECONDS.toSeconds(DateUtils.parseByDayPattern(epochStr).getTime());
        }
    }


}
