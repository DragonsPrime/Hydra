package com.pinecone.hydra.storage.version;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.version.entity.TitanVersion;
import com.pinecone.hydra.storage.version.source.VersionManipulator;
import com.pinecone.hydra.storage.version.source.VersionMasterManipulator;
import com.pinecone.hydra.system.Hydrarum;
import com.pinecone.hydra.system.ko.driver.KOIMappingDriver;
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

    public TitanVersionManage(Hydrarum hydrarum, KOIMasterManipulator masterManipulator ){
        this( hydrarum, masterManipulator, VersionManage.class.getSimpleName() );
    }

    public TitanVersionManage(KOIMappingDriver driver ) {
        this(
                driver.getSystem(),
                driver.getMasterManipulator()
        );
    }


    @Override
    public void insert(TitanVersion version) {
        this.versionManipulator.insertObjectVersion( version.getVersion(), version.getTargetStorageObjectGuid(), version.getFileGuid());
    }

    @Override
    public void remove(String version, GUID fileGuid) {
        this.versionManipulator.removeObjectVersion( version, fileGuid );
    }

    @Override
    public GUID queryObjectGuid(String version, GUID fileGuid) {
        return this.versionManipulator.queryObjectGuid( version, fileGuid );
    }
}
