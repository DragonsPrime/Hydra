package com.pinecone.framework.util.comparator;

import com.pinecone.framework.system.prototype.Pinenut;

public interface Ordered extends Pinenut {
    int HIGHEST_PRECEDENCE = -2147483648;
    int LOWEST_PRECEDENCE = 2147483647;

    int getOrder();
}
