package ru.vsu.cs.oop.pronin_s_v.task1.model;

import java.util.Objects;

public final class Password {
    private final String id;
    private final String service;
    private String login;
    private String secret;

    public Password(String id, String service, String login, String secret) {
        this.id = requireNonBlank(id, "id");
        this.service = requireNonBlank(service, "service");
        this.login = requireNonBlank(login, "login");
        this.secret = requireNonBlank(secret, "secret");
    }

    public String getId() { return id; }
    public String getService() { return service; }
    public String getLogin() { return login; }
    public String getSecret() { return secret; }

    public void setLogin(String login)   { this.login = requireNonBlank(login, "login"); }
    public void setSecret(String secret) { this.secret = requireNonBlank(secret, "secret"); }

    private static String requireNonBlank(String s, String field) {
        Objects.requireNonNull(s, field + " cannot be null");
        if (s.trim().isEmpty()) throw new IllegalArgumentException(field + " cannot be blank");
        return s;
    }

    @Override
    public String toString() {
        return service + " / " + login + " [" + masked() + "]";
    }

    private String masked() {
        return "*".repeat(Math.min(secret.length(), 6)) + (secret.length() > 6 ? "…" : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Password that)) return false;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}