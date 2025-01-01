package com.pinecone.hydra.storage.version.source;

import com.pinecone.hydra.system.ko.driver.KOIMasterManipulator;

public interface VersionMasterManipulator extends KOIMasterManipulator {
    VersionManipulator getVersionManipulator();
}
