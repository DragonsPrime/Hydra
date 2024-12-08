package com.pinecone.hydra.account.source;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.system.ko.dao.GUIDNameManipulator;
import com.pinecone.hydra.account.entity.Account;

public interface UserNodeManipulator extends GUIDNameManipulator {
    void insert(Account account);

    void remove(GUID userGuid);

    Account queryUser(GUID userGuid );

}
