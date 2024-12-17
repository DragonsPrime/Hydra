package com.pinecone.hydra.storage.version;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.policy.source.PolicyMasterManipulator;
import com.pinecone.hydra.storage.version.entity.Version;
import com.pinecone.hydra.storage.version.source.VersionManipulator;
import com.pinecone.hydra.storage.version.source.VersionMasterManipulator;
import com.pinecone.hydra.system.Hydrarum;
import com.pinecone.hydra.system.ko.driver.KOIMasterManipulator;
import com.pinecone.ulf.util.id.GuidAllocator;
import com.pinecone.ulf.util.id.impl.GenericGuidAllocator;

public class TitanVersionManage implements VersionManage{
    protected Hydrarum                 hydrarum;

    protected GuidAllocator            guidAllocator;

    protected VersionMasterManipulator masterManipulator;

    protected VersionManipulator       versionManipulator;

    public TitanVersionManage(Hydrarum hydrarum, KOIMasterManipulator masterManipulator, String name ){
        this.hydrarum               = hydrarum;
        this.masterManipulator      = (VersionMasterManipulator) masterManipulator;
        this.guidAllocator          = new GenericGuidAllocator();
        this.versionManipulator     = this.masterManipulator.getVersionManipulator();
    }

    @Override
    public void insert(Version version) {

    }

    @Override
    public void remove(String version, GUID fileGuid) {

    }

    @Override
    public Version query(String version, GUID fileGuid) {
        return null;
    }
}
