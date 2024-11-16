package com.pinecone.hydra.umc.msg;

import java.io.IOException;
import java.util.Map;

public interface UMCReceiver extends UMCProtocol{
    Object readInformMsg() throws IOException;

    UMCMessage readTransferMsg() throws IOException;

    UMCMessage readTransferMsgBytes() throws IOException;

    UMCMessage readMsg() throws IOException;

    UMCMessage readMsgBytes() throws IOException;
}
