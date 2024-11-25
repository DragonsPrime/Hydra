package com.pinecone.hydra.umct.protocol.function;

public class GenericFunctionMold<TR > implements FunctionMold<TR > {
    protected ArgumentRequest    mArgumentRequest;
    protected ReturnResponse<TR> mReturnResponse;

    @Override
    public ArgumentRequest getArgumentForm() {
        return this.mArgumentRequest;
    }

    @Override
    public ReturnResponse<TR> getReturnForm() {
        return this.mReturnResponse;
    }
}
