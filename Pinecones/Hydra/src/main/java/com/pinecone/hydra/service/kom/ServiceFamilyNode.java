package com.pinecone.hydra.service.kom;

import java.util.Map;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.id.Identification;
import com.pinecone.hydra.service.ServiceFamilyMeta;
import com.pinecone.hydra.service.kom.entity.ArchElementNode;

public interface ServiceFamilyNode extends ServiceFamilyMeta {
    long getEnumId();

    void setEnumId( long id );

    void setName( String name );

    GUID getGuid();

    void setGuid( GUID guid );

    @Override
    default Identification getId() {
        return this.getGuid();
    }

    String getScenario();

    void setScenario( String scenario );

    String getPrimaryImplLang();

    void setPrimaryImplLang( String primaryImplLang );

    String getExtraInformation();

    void setExtraInformation( String extraInformation );

    String getLevel();

    void setLevel( String level );

    String getDescription();

    void setDescription( String description );

    ServiceFamilyNode apply( Map<String, Object > joEntity ) ;
}