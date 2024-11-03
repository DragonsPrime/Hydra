package com.pinecone.hydra.umc.msg.extra;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.hydra.umc.msg.UMCHead;

import java.util.Map;

public interface ExtraHeadEncoder extends Pinenut {
    byte[] encode( UMCHead head, Object jo );
}
