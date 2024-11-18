package com.pinecone.ulf.util.protobuf;

import com.pinecone.framework.system.prototype.Pinenut;

public interface FileDescriptorFormater extends Pinenut {
    String format( Class<?> type );
}
