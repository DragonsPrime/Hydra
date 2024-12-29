package com.pinecone.hydra.umc.wolfmc;

import com.pinecone.hydra.umc.msg.ChannelStatus;

public enum UlfChannelStatus implements ChannelStatus {
    IDLE                     ( 0x00, "Idle"                 ),
    WAITING_FOR_SEND         ( 0x01, "WaitingSend"          ),
    WAITING_FOR_RECEIVE      ( 0x02, "WaitingReceive"       ),
    WAITING_FOR_RECALL_FUN   ( 0x03, "WaitingRecallFun"     ),
    WAITING_THREAD_RESUME    ( 0x04, "WaitingThreadResume"  ),

    FORCE_SYNCHRONIZED       ( 0x05, "ForceSynchronized"    ),
    WAITING_FOR_SHUTDOWN     ( 0x06, "WaitingShutdown"      ),
    SHUTDOWN                 ( 0x07, "Shutdown"             );

    private final int value;

    private final String name;

    UlfChannelStatus( int value, String name ){
        this.value = value;
        this.name  = name;
    }

    @Override
    public String getName(){
        return this.name;
    }

    @Override
    public int getValue() {
        return this.value;
    }

    @Override
    public byte getByteValue() {
        return (byte) this.value;
    }

    @Override
    public boolean isIdle() {
        return this == UlfChannelStatus.IDLE;
    }

    @Override
    public boolean isTerminated() {
        return this.value >= UlfChannelStatus.WAITING_FOR_SHUTDOWN.value;
    }

    @Override
    public boolean isWaitingForIOCompleted(){
        return this.value >= UlfChannelStatus.WAITING_FOR_SEND.value && this.value <= UlfChannelStatus.WAITING_FOR_RECEIVE.value;
    }

    @Override
    public boolean isWaitingForLocalCompleted(){
        return this.value >= UlfChannelStatus.WAITING_FOR_RECALL_FUN.value && this.value <= UlfChannelStatus.WAITING_THREAD_RESUME.value;
    }

    @Override
    public boolean isAsynAvailable() {
        return !this.isTerminated() && this != UlfChannelStatus.FORCE_SYNCHRONIZED;
    }

    @Override
    public boolean isSyncAvailable() {
        return !this.isTerminated() || this.isIdle();
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
