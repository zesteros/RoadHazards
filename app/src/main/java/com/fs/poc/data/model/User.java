
package com.fs.poc.data.model;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable
{

    private Integer id;
    private String login;
    private String firstName;
    private String lastName;
    private String email;
    private String imageUrl;
    private Boolean activated;
    private Object langKey;
    private Object createdBy;
    private Object createdDate;
    private Object lastModifiedBy;
    private Object lastModifiedDate;
    private List<String> authorities = null;
    private Object password;
    private Object dateCreated;
    private Object lastConnection;
    private Object lastModified;
    private String telephone;
    private String mobile;
    private String address;
    private String city;
    private Object workArea;
    private String position;
    private Object deleted;
    private Object companyName;
    private final static long serialVersionUID = 7603949067995148549L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getActivated() {
        return activated;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }

    public Object getLangKey() {
        return langKey;
    }

    public void setLangKey(Object langKey) {
        this.langKey = langKey;
    }

    public Object getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Object createdBy) {
        this.createdBy = createdBy;
    }

    public Object getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Object createdDate) {
        this.createdDate = createdDate;
    }

    public Object getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(Object lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Object getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Object lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }

    public Object getPassword() {
        return password;
    }

    public void setPassword(Object password) {
        this.password = password;
    }

    public Object getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Object dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Object getLastConnection() {
        return lastConnection;
    }

    public void setLastConnection(Object lastConnection) {
        this.lastConnection = lastConnection;
    }

    public Object getLastModified() {
        return lastModified;
    }

    public void setLastModified(Object lastModified) {
        this.lastModified = lastModified;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Object getWorkArea() {
        return workArea;
    }

    public void setWorkArea(Object workArea) {
        this.workArea = workArea;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Object getDeleted() {
        return deleted;
    }

    public void setDeleted(Object deleted) {
        this.deleted = deleted;
    }

    public Object getCompanyName() {
        return companyName;
    }

    public void setCompanyName(Object companyName) {
        this.companyName = companyName;
    }

}
