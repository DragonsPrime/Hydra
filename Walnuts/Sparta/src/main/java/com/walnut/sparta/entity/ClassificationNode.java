package com.walnut.sparta.entity;

import java.util.UUID;
public class ClassificationNode {
    //节点id
    private String id;
    //节点uuid
    private String UUID;
    //节点名称
    private String name;
    //分类规则uuid
    private String rulesUUID;


    public ClassificationNode() {
    }

    public ClassificationNode(String id, String UUID, String name, String rulesUUID) {
        this.id = id;
        this.UUID = UUID;
        this.name = name;
        this.rulesUUID = rulesUUID;
    }

    /**
     * 获取
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * 设置
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取
     * @return UUID
     */
    public String getUUID() {
        return UUID;
    }

    /**
     * 设置
     * @param UUID
     */
    public void setUUID(String UUID) {
        this.UUID = UUID;
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
     * @return rulesUUID
     */
    public String getRulesUUID() {
        return rulesUUID;
    }

    /**
     * 设置
     * @param rulesUUID
     */
    public void setRulesUUID(String rulesUUID) {
        this.rulesUUID = rulesUUID;
    }

    public String toString() {
        return "ClassificationNode{id = " + id + ", UUID = " + UUID + ", name = " + name + ", rulesUUID = " + rulesUUID + "}";
    }
}
