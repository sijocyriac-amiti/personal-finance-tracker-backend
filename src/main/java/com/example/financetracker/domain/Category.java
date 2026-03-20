package com.example.financetracker.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CategoryType type;

    @Column(length = 20)
    private String color;

    @Column(length = 50)
    private String icon;

    @Column(nullable = false)
    private boolean archived = false;

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getName() {
        return name;
    }

    public CategoryType getType() {
        return type;
    }

    public String getColor() {
        return color;
    }

    public String getIcon() {
        return icon;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(CategoryType type) {
        this.type = type;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public static class Builder {
        private final Category category = new Category();

        public Builder user(User user) {
            category.user = user;
            return this;
        }

        public Builder name(String name) {
            category.name = name;
            return this;
        }

        public Builder type(CategoryType type) {
            category.type = type;
            return this;
        }

        public Builder color(String color) {
            category.color = color;
            return this;
        }

        public Builder icon(String icon) {
            category.icon = icon;
            return this;
        }

        public Builder archived(boolean archived) {
            category.archived = archived;
            return this;
        }

        public Category build() {
            return category;
        }
    }
}
