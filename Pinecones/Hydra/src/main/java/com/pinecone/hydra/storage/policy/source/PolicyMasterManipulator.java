package com.pinecone.hydra.storage.policy.source;

import com.pinecone.hydra.storage.version.source.VersionManipulator;
import com.pinecone.hydra.system.ko.driver.KOIMasterManipulator;

public interface PolicyMasterManipulator extends KOIMasterManipulator {
    PolicyManipulator                   getPolicyManipulator();
    PolicyFileMappingManipulator        getPolicyFileMappingManipulator();
}
