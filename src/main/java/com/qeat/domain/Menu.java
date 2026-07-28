package com.qeat.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "menus")
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booth_id")
    private Booth booth;

    private String name;
    private String description;
    private int price;

    @Column(name = "sold_out")
    private Boolean soldOut;

    @Column(name = "image_url")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private Category category;

    public Long getId() {
        return id;
    }

    public Booth getBooth() {
        return booth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }

    public boolean isSoldOut() {
        return soldOut;
    }

    public void setSoldOut(Boolean soldOut) {
        this.soldOut = soldOut;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }


    public Menu(Booth booth, String name, String description, int price, String imageUrl, Category category) {
        this.booth = booth;
        this.name = name;
        this.description = description;
        this.price = price;
        this.soldOut = false;
        this.imageUrl = imageUrl;
        this.category = category;
    }

    public void update(String name, String description, Integer price, String imageUrl, Category category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
    }

    protected Menu() {}
}
