package com.pinecone.hydra.storage.volume.entity.local.striped;

import com.pinecone.framework.system.prototype.Pinenut;

public interface TerminalStateRecord extends Pinenut {
    int getSequentialNumbering();
    void setSequentialNumbering( int sequentialNumbering );

    Number getValidByteStart();
    void setValidByteStart( Number validByteStart );

    Number getValidByteEnd();
    void setValidByteEnd( Number validByteEnd );
}
