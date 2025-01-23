package com.pinecone.hydra.account.source;

import com.pinecone.hydra.system.ko.driver.KOIMasterManipulator;

public interface UserMasterManipulator extends KOIMasterManipulator {
    DomainNodeManipulator       getDomainNodeManipulator();

    GroupNodeManipulator        getGroupNodeManipulator();

    UserNodeManipulator         getUserNodeManipulator();

    CredentialManipulator       getCredentialManipulator();

    AuthorizationManipulator    getAuthorizationManipulator();
}
