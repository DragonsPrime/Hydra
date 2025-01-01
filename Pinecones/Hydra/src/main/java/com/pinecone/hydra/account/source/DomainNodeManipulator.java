package com.pinecone.hydra.account.source;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.system.ko.dao.GUIDNameManipulator;
import com.pinecone.hydra.account.entity.Domain;
import com.pinecone.hydra.account.entity.Account;

public interface DomainNodeManipulator extends GUIDNameManipulator {
    void insert(Domain domain);

    void remove(GUID domainGuid);

    Account queryDomain(GUID domainGuid );
}
