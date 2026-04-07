package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "booths")
public class Booth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Bank bank;

    @Column(name = "account_number", nullable = false)
    private String accountNumber;

    protected Booth() {}

    private Booth(String name, String description, Long ownerId, Bank bank, String accountNumber) {
        this.name = name;
        this.description = description;
        this.ownerId = ownerId;
        this.bank = bank;
        this.accountNumber = accountNumber;
    }

    public static Booth create(String name, String description, Long ownerId, Bank bank, String accountNumber) {
        return new Booth(name, description, ownerId, bank, accountNumber);
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    @PrePersist
    private void prePersist() {
        this.createdAt = LocalDate.now();
    }
}