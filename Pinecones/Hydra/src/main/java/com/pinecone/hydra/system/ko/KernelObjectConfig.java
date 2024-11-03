package com.pinecone.hydra.system.ko;

import com.pinecone.framework.system.prototype.Pinenut;

public interface KernelObjectConfig extends Pinenut {
    String getPathNameSeparator();

    String getFullNameSeparator();

    String getPathNameSepRegex();

    String getFullNameSepRegex();

    int getShortPathLength();
}
