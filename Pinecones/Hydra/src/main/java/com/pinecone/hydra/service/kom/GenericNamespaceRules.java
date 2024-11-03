package com.pinecone.hydra.service.kom;

import com.pinecone.framework.util.id.GUID;

public class GenericNamespaceRules implements NamespaceRules {
    // 规则id
    private long enumId;

    // 规则uuid
    private GUID guid;

    // 作用域
    private String scope;

    // 名称
    private String name;

    // 规则描述
    private String description;


    public GenericNamespaceRules() {
    }

    public GenericNamespaceRules(long enumId, GUID guid, String scope, String name, String description) {
        this.enumId = enumId;
        this.guid = guid;
        this.scope = scope;
        this.name = name;
        this.description = description;
    }

    /**
     * 获取
     * @return enumId
     */
    public long getEnumId() {
        return enumId;
    }

    /**
     * 设置
     * @param enumId
     */
    public void setEnumId(long enumId) {
        this.enumId = enumId;
    }

    /**
     * 获取
     * @return guid
     */
    public GUID getGuid() {
        return guid;
    }

    /**
     * 设置
     * @param guid
     */
    public void setGuid(GUID guid) {
        this.guid = guid;
    }

    /**
     * 获取
     * @return scope
     */
    public String getScope() {
        return scope;
    }

    /**
     * 设置
     * @param scope
     */
    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * 获取
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * 设置
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public String toString() {
        return "GenericClassificationRules{enumId = " + enumId + ", guid = " + guid + ", scope = " + scope + ", name = " + name + ", description = " + description + "}";
    }
}