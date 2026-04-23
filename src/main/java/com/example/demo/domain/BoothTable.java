package com.example.demo.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "tables")
public class BoothTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booth_id", nullable = false)
    private Booth booth;

    @Column(name = "table_number", nullable = false)
    private int tableNumber;

    @Column(name = "table_token", unique = true, nullable = false, length = 20)
    private String tableToken;

    @Column(name = "qr_image_url")
    private String qrImageUrl;

    private boolean active = true;

    protected BoothTable() {}

    public BoothTable(Booth booth, int tableNumber, String tableToken) {
        this.booth = booth;
        this.tableNumber = tableNumber;
        this.tableToken = tableToken;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Booth getBooth() {
        return booth;
    }

    public void setBooth(Booth booth) {
        this.booth = booth;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public String getTableToken() {
        return tableToken;
    }

    public void setTableToken(String tableToken) {
        this.tableToken = tableToken;
    }

    public String getQrImageUrl() {
        return qrImageUrl;
    }

    public void setQrImageUrl(String qrImageUrl) {
        this.qrImageUrl = qrImageUrl;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
