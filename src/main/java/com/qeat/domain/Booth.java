package com.qeat.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "booth_status", nullable = false)
    private BoothStatus boothStatus;

    @Column(nullable = false)
    private boolean open = true;
    private LocalTime openTime;
    private LocalTime closeTime;

    protected Booth() {}

    private Booth(String name, String description, Long ownerId, Bank bank, String accountNumber) {
        this.name = name;
        this.description = description;
        this.ownerId = ownerId;
        this.bank = bank;
        this.accountNumber = accountNumber;
        this.boothStatus = BoothStatus.PENDING;
        this.open = true;
        this.openTime = null;
        this.closeTime = null;
    }

    public static Booth create(String name, String description, Long ownerId, Bank bank, String accountNumber) {
        return new Booth(name, description, ownerId, bank, accountNumber);
    }

    public void update(String name, Bank bank, String accountNumber, String description) {
        this.name = name;
        this.bank = bank;
        this.accountNumber = accountNumber;
        this.description = description;
    }

    @PrePersist
    private void prePersist() {
        this.createdAt = LocalDate.now();
    }

    public boolean canOrder() {
        if (this.boothStatus != BoothStatus.APPROVED) {
            return false;
        }

        if (!this.open) {
            return false;
        }

        LocalTime now = LocalTime.now();

        if (this.openTime != null && this.closeTime != null) {
            return !now.isBefore(this.openTime) && now.isBefore(this.closeTime);
        }

        return true;
    }

    public void changeOpenStatus(Boolean open) {
        this.open = open;
    }

    public void changeOperatingTime(LocalTime openTime, LocalTime closeTime) {
        this.openTime = openTime;
        this.closeTime = closeTime;
    }

    public void clearOperatingTime() {
        this.openTime = null;
        this.closeTime = null;
    }

    public void approve() {
        this.boothStatus = BoothStatus.APPROVED;
    }

    public void reject() {
        this.boothStatus = BoothStatus.REJECTED;
    }

    public void suspend() {
        this.boothStatus = BoothStatus.SUSPENDED;
        this.open = false;
    }

    public void delete() {
        this.boothStatus = BoothStatus.DELETED;
        this.open = false;
    }
}
