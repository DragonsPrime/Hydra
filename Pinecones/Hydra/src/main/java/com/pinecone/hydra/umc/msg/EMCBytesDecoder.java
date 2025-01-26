package com.pinecone.hydra.umc.msg;

import java.io.IOException;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.hydra.umc.msg.extra.ExtraHeadCoder;

public interface EMCBytesDecoder extends Pinenut {

    UMCHead decode( byte[] buf, ExtraHeadCoder extraHeadCoder ) throws IOException;

    UMCHead decodeIntegrated( byte[] buf, ExtraHeadCoder extraHeadCoder ) throws IOException;

}
