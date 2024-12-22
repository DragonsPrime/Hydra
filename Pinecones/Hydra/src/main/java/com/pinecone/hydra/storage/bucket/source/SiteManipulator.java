package com.pinecone.hydra.storage.bucket.source;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.bucket.entity.Site;

import java.util.List;

public interface SiteManipulator extends Pinenut {
    void insert(Site site );

    void remove( GUID siteGuid );

    void removeByName( String siteName );

    Site querySite( GUID siteGuid );

    Site querySiteByName( String siteName );

    List<Site> listSite();
}
