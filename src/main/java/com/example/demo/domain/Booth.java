package com.example.demo.domain;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "booths")
public class Booth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "owner_id")
    private Long ownerId;

    public Long getId() {
        return id;
    }

    protected Booth() {}

    private Booth(String name, String description, Long ownerId) {
        this.name = name;
        this.description = description;
        this.ownerId = ownerId;
    }

    public static Booth create(String name, String description, Long ownerId) {
        return new Booth(name, description, ownerId);
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    @PrePersist
    private void prePersist() {
        this.createdAt = LocalDate.now();
    }
}
