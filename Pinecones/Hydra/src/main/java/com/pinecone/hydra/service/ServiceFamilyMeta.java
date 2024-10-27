package com.pinecone.hydra.service;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.Identification;

public interface ServiceFamilyMeta extends Pinenut  {
    //long getEnumId();

    //GUID getGuid();

    Identification getId() ;

    String getName();

    String getScenario();

    String getPrimaryImplLang();

    String getExtraInformation();

    String getLevel();

    String getDescription();
}
