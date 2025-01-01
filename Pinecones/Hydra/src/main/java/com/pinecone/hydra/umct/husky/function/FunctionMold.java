package com.pinecone.hydra.umct.husky.function;

import com.pinecone.framework.system.prototype.Pinenut;

public interface FunctionMold<TR > extends Pinenut {
    ArgumentRequest getArgumentForm();

    ReturnResponse<TR> getReturnForm();
}
