package com.pinecone.hydra.account.source;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.system.ko.dao.GUIDNameManipulator;
import com.pinecone.hydra.account.entity.Group;
import com.pinecone.hydra.account.entity.Account;

public interface GroupNodeManipulator extends GUIDNameManipulator {
    void insert(Group group);

    void remove(GUID groupGuid);

    Account queryGroup(GUID groupGuid );

}
