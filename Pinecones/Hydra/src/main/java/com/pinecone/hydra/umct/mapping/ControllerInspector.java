package com.pinecone.hydra.umct.mapping;

import java.util.List;

import com.pinecone.framework.system.prototype.Pinenut;

import javassist.CtMethod;
import javassist.NotFoundException;

public interface ControllerInspector extends Pinenut {
    List<CtMethod> inspect( String className ) throws NotFoundException;

    List<CtMethod > inspect( Class<?> clazz ) throws NotFoundException;

    List<MappingDigest > characterize( String className ) throws NotFoundException;

    List<MappingDigest > characterize( Class<?> clazz ) throws NotFoundException;
}
