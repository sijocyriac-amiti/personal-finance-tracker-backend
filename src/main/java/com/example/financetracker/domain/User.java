package com.example.financetracker.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String displayName;

    @Column(nullable = false, length = 100)
    private String roles;

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getRoles() {
        return roles;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public static class Builder {
        private final User user = new User();

        public Builder username(String username) {
            user.username = username;
            return this;
        }

        public Builder password(String password) {
            user.password = password;
            return this;
        }

        public Builder email(String email) {
            user.email = email;
            return this;
        }

        public Builder displayName(String displayName) {
            user.displayName = displayName;
            return this;
        }

        public Builder roles(String roles) {
            user.roles = roles;
            return this;
        }

        public User build() {
            return user;
        }
    }
}
