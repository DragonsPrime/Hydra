package com.pinecone.hydra.account.source;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.account.entity.Credential;

public interface CredentialManipulator extends Pinenut {
    void insert(Credential credential);

    void remove(GUID credentialGuid);

    Credential queryCredential(GUID credentialGuid );
}
