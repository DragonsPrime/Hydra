package com.pinecone.hydra.account.source;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.account.entity.Authorization;
import com.pinecone.hydra.account.entity.Credential;

public interface AuthorizationManipulator extends Pinenut {
    void insert(Authorization authorization);

    void remove(GUID authorizationGuid);

    Authorization queryCredential(GUID authorizationGuid );
}
