package com.pinecone.hydra.storage.volume.entity.local.striped;

public class StripTerminalStateRecord implements TerminalStateRecord {
    protected int       sequentialNumbering;
    protected Number    validByteStart;
    protected Number    validByteEnd;

    @Override
    public int getSequentialNumbering() {
        return this.sequentialNumbering;
    }

    @Override
    public void setSequentialNumbering(int sequentialNumbering) {
        this.sequentialNumbering = sequentialNumbering;
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
}
