package com.pinecone.hydra.umc.msg;

import com.pinecone.framework.system.prototype.Pinenut;

public interface MessageStereotypes extends Pinenut {
    Class<? >  putType();

    Class<? > postBytesType();

    Class<? > postStreamType();
}
