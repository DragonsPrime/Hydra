package com.pinecone.hydra.umct.protocol.function;

import com.pinecone.framework.system.prototype.Pinenut;

public interface FunctionMold<TR > extends Pinenut {
    ArgumentRequest getArgumentForm();

    ReturnResponse<TR> getReturnForm();
}
