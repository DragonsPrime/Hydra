package com.pinecone.hydra.umct;

import java.util.List;

import com.pinecone.framework.system.ProxyProvokeHandleException;
import com.pinecone.framework.system.prototype.Pinenut;

public interface InvokeEntity extends Pinenut {
    String getAddress();

    String getEntityName();

    /**
     * Invocation Path | 调用实体路径
     * Address + EntityName
     * e.g. Package + MethodName     => com.dragonking.method
     * e.g. Namespace + FunctionName => std::printf
     * e.g. Mapping + TargetName     => /admin/audit/methodName
     */
    String getInvocationPath();

    List<Class<?> > getParameters();

    /**
     * Invoked Interface (defaulted, [::function], anonymous global namespace)
     * Usually a class, struct, namespace or interface
     */
    Object getInvokeIface();

    /**
     * Invoked Entity
     * Usually a method, function or apis
     */
    Object getInvokeEntity();

    Object invoke( Object... args ) throws Exception;

    default Object call( Object... args ) {
        try{
            return this.invoke( args );
        }
        catch ( Exception e ) {
            throw new ProxyProvokeHandleException( e );
        }
    }

    void execute( Object... args ) throws Exception;

    default void enforce( Object... args ) {
        try{
            this.execute( args );
        }
        catch ( Exception e ) {
            throw new ProxyProvokeHandleException( e );
        }
    }

}
