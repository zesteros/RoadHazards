
package com.fs.poc.data.model;

import java.io.Serializable;

public class Category implements Serializable
{

    private Integer id;
    private String name;
    private String prefix;
    private Object estimatedContactHours;
    private Object estimatedSolutionHours;
    private Boolean deleted;
    private Object criticalityId;
    private Integer categoryTypeId;
    private final static long serialVersionUID = 5035543580442212884L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Object getEstimatedContactHours() {
        return estimatedContactHours;
    }

    public void setEstimatedContactHours(Object estimatedContactHours) {
        this.estimatedContactHours = estimatedContactHours;
    }

    public Object getEstimatedSolutionHours() {
        return estimatedSolutionHours;
    }

    public void setEstimatedSolutionHours(Object estimatedSolutionHours) {
        this.estimatedSolutionHours = estimatedSolutionHours;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Object getCriticalityId() {
        return criticalityId;
    }

    public void setCriticalityId(Object criticalityId) {
        this.criticalityId = criticalityId;
    }

    public Integer getCategoryTypeId() {
        return categoryTypeId;
    }

    public void setCategoryTypeId(Integer categoryTypeId) {
        this.categoryTypeId = categoryTypeId;
    }

}
