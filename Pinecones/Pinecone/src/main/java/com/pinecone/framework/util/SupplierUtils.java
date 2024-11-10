package com.pinecone.framework.util;

import java.util.function.Supplier;

import com.pinecone.framework.system.Nullable;

public abstract class SupplierUtils {
    public SupplierUtils() {
    }

    @Nullable
    public static <T> T resolve( @Nullable Supplier<T> supplier ) {
        return supplier != null ? supplier.get() : null;
    }
}
