package com.pinecone.hydra.account;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.account.entity.*;
import com.pinecone.hydra.system.ko.kom.KOMInstrument;

import java.util.List;

public interface AccountManager extends KOMInstrument {
    AccountConfig KernelAccountConfig = new KernelAccountConfig();

    Account affirmAccount( String path );

    Group   affirmGroup( String path );

    Domain  affirmDomain( String path );
    void insertCredential( Credential credential );

    ElementNode queryElement( String path );

    void addChildren(GUID parentGuid, GUID childrenGuid);

    boolean containsChild( GUID parentGuid, String childName );

    List<GUID> queryAccountByName(String userName);

    boolean queryAccountByGuid(GUID userGuid, String kernelCredential);
}
