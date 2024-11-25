package com.pinecone.hydra.umct.protocol.compiler;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import com.pinecone.hydra.umct.stereotype.Iface;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public class BytecodeIfacCompiler extends ArchIfacCompiler implements InterfacialCompiler {
    public BytecodeIfacCompiler( ClassPool classPool ) {
        super( classPool );
    }

    public List<CtMethod> conform( String className, boolean bAsIface ) {
        List<CtMethod> ifaceMethods = new ArrayList<>();

        return ifaceMethods;
    }
}
