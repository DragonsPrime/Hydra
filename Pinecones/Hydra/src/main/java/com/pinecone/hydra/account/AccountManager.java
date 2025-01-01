package com.pinecone.hydra.account;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.account.entity.Account;
import com.pinecone.hydra.account.entity.Domain;
import com.pinecone.hydra.account.entity.ElementNode;
import com.pinecone.hydra.account.entity.Group;
import com.pinecone.hydra.system.ko.kom.KOMInstrument;

public interface AccountManager extends KOMInstrument {
    AccountConfig KernelAccountConfig = new KernelAccountConfig();

    Account affirmAccount( String path );

    Group   affirmGroup( String path );

    Domain  affirmDomain( String path );

    ElementNode queryElement( String path );

    void addChildren(GUID parentGuid, GUID childrenGuid);

    boolean containsChild( GUID parentGuid, String childName );

}
