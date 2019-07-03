package ru.mediaserver.client.msdesktop.business.auth.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public class Principal {
    @JsonIgnore
    private List authorities;
    @JsonIgnore
    private Object details;
    @JsonIgnore
    private boolean authenticated;
    @JsonIgnore
    private Object userAuthentication;
    @JsonIgnore
    private Object oauth2Request;
    @JsonIgnore
    private User principal;
    @JsonIgnore
    private boolean clientOnly;
    @JsonIgnore
    private String credentials;

    private String name;

    public Principal() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List getAuthorities() {
        return authorities;
    }

    public Object getDetails() {
        return details;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public Object getUserAuthentication() {
        return userAuthentication;
    }

    public Object getOauth2Request() {
        return oauth2Request;
    }

    public User getPrincipal() {
        return principal;
    }

    public boolean isClientOnly() {
        return clientOnly;
    }

    public String getCredentials() {
        return credentials;
    }
}
