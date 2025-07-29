package com.sharevortex.sharevortex.model;

public class FileSession {
    private String token;

    public FileSession() {}
    public FileSession(String token) { this.token = token; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}

