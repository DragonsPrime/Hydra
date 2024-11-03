package com.pinecone.hydra.service;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.name.Namespace;

public interface ServiceMeta extends Pinenut {
    GUID getGuid() ;

    String getName();        // Service Name, e.g. WpnService

    String getDisplayName(); // Service Long Name, e.g. Windows Push Notification System Service

    String getDescription();

    String getFullName();

    Namespace getGroupNamespace();

    String getGroupName();

    String getScenario() ;

    String getPrimaryImplLang() ;

    String getExtraInformation() ;

    String getLevel() ;
}
