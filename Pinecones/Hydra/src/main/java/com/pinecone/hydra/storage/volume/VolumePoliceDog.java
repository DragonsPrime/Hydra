package com.pinecone.hydra.storage.volume;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;

public interface VolumePoliceDog extends Pinenut {
    GUID simpleDfsSearch(String path,String szSeparator);
}
