package com.pinecone.hydra.umc.msg.extra;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.hydra.umc.msg.UMCHead;

public interface ExtraHeadDecoder extends Pinenut {
    Object decode( UMCHead head, byte[] raw );
}
