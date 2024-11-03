package com.pinecone.hydra.service.kom;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;

public interface NamespaceRules extends Pinenut {
    long getEnumId();
    void setEnumId(long id);

    GUID getGuid();
    void setGuid(GUID guid);

    String getScope();
    void setScope(String scope);

    String getName();
    void setName(String name);

    String getDescription();
    void setDescription(String description);
}