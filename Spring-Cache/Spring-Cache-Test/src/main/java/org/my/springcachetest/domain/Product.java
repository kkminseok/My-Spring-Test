package org.my.springcachetest.domain;

/**
 * 상품 도메인 - 시나리오 학습용
 */
public class Product {

    private Long id;
    private String name;
    private int price;
    private int stock;           // 재고 수량
    private boolean soldOut;     // 품절 여부
    private String category;     // 카테고리 (PREMIUM, STANDARD 등)

    public Product() {}

    public Product(Long id, String name, int price, int stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.soldOut = stock <= 0;
        this.category = "STANDARD";
    }

    public Product(Long id, String name, int price, int stock, String category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.soldOut = stock <= 0;
        this.category = category;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public int getStock() { return stock; }
    public void setStock(int stock) {
        this.stock = stock;
        this.soldOut = stock <= 0;
    }

    public boolean isSoldOut() { return soldOut; }
    public void setSoldOut(boolean soldOut) { this.soldOut = soldOut; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public boolean isPremium() {
        return "PREMIUM".equals(category);
    }

    @Override
    public String toString() {
        return "Product{id=" + id + ", name='" + name + "', price=" + price +
               ", stock=" + stock + ", soldOut=" + soldOut + ", category='" + category + "'}";
    }
}
