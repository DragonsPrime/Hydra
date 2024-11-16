package com.pinecone.framework.system.regimentation;

import com.pinecone.framework.util.name.Namespace;

/**
 *  Pinecone Framework For Java (Bean Nuts Pinecone Ursus for Java)
 *  Author: Harold.E / JH.W (DragonKing)
 *  Copyright © 2008 - 2028 Bean Nuts Foundation All rights reserved.
 *  *****************************************************************************************
 *  UniformNodus
 *  Regimentation Uniform Node
 *  统一编组节点
 *  *****************************************************************************************
 *  Dragon King, the undefined
 */
public interface UniformNodus extends Nodus {
    /**
     * Nomenclature of node`s name, usually the path of a cascade centralized tree.
     * 编制节点的系统命名，通常是级联中央集权树的路径
     */
    Namespace getUniformName();

    void setUniformName( Namespace name );
}
