package org.my.springcachetest.domain;

/**
 * 고객 도메인 - 시나리오 학습용
 */
public class Customer {

    private Long id;
    private String name;
    private String grade;   // VIP, GOLD, STANDARD
    private int totalPurchase;  // 총 구매 금액

    public Customer() {}

    public Customer(Long id, String name, String grade) {
        this.id = id;
        this.name = name;
        this.grade = grade;
        this.totalPurchase = 0;
    }

    public Customer(Long id, String name, String grade, int totalPurchase) {
        this.id = id;
        this.name = name;
        this.grade = grade;
        this.totalPurchase = totalPurchase;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public int getTotalPurchase() { return totalPurchase; }
    public void setTotalPurchase(int totalPurchase) { this.totalPurchase = totalPurchase; }

    public boolean isVip() {
        return "VIP".equals(grade);
    }

    @Override
    public String toString() {
        return "Customer{id=" + id + ", name='" + name + "', grade='" + grade +
               "', totalPurchase=" + totalPurchase + "}";
    }
}
