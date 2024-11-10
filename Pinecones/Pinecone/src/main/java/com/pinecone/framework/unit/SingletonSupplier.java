package com.pinecone.framework.unit;

import java.util.function.Supplier;

import com.pinecone.framework.system.Nullable;
import com.pinecone.framework.util.Assert;

public class SingletonSupplier<T> implements Supplier<T> {
    @Nullable
    private final Supplier<? extends T> instanceSupplier;
    @Nullable
    private final Supplier<? extends T> defaultSupplier;
    @Nullable
    private volatile T singletonInstance;

    public SingletonSupplier(@Nullable T instance, Supplier<? extends T> defaultSupplier) {
        this.instanceSupplier = null;
        this.defaultSupplier = defaultSupplier;
        this.singletonInstance = instance;
    }

    public SingletonSupplier(@Nullable Supplier<? extends T> instanceSupplier, Supplier<? extends T> defaultSupplier) {
        this.instanceSupplier = instanceSupplier;
        this.defaultSupplier = defaultSupplier;
    }

    private SingletonSupplier(Supplier<? extends T> supplier) {
        this.instanceSupplier = supplier;
        this.defaultSupplier = null;
    }

    private SingletonSupplier(T singletonInstance) {
        this.instanceSupplier = null;
        this.defaultSupplier = null;
        this.singletonInstance = singletonInstance;
    }

    @Nullable
    @Override
    public T get() {
        T instance = this.singletonInstance;
        if (instance == null) {
            synchronized(this) {
                instance = this.singletonInstance;
                if (instance == null) {
                    if (this.instanceSupplier != null) {
                        instance = this.instanceSupplier.get();
                    }

                    if (instance == null && this.defaultSupplier != null) {
                        instance = this.defaultSupplier.get();
                    }

                    this.singletonInstance = instance;
                }
            }
        }

        return instance;
    }

    public T obtain() {
        T instance = this.get();
        Assert.state(instance != null, "No instance from Supplier");
        return instance;
    }

    public static <T> SingletonSupplier<T> of(T instance) {
        return new SingletonSupplier<>(instance);
    }

    @Nullable
    public static <T> SingletonSupplier<T> ofNullable( @Nullable T instance ) {
        return instance != null ? new SingletonSupplier<>(instance) : null;
    }

    public static <T> SingletonSupplier<T> of( Supplier<T> supplier ) {
        return new SingletonSupplier<>(supplier);
    }

    @Nullable
    public static <T> SingletonSupplier<T> ofNullable( @Nullable Supplier<T> supplier ) {
        return supplier != null ? new SingletonSupplier<>(supplier) : null;
    }
}