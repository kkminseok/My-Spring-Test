package org.my.springcachetest.domain;

import java.util.Objects;

/**
 * 도서 도메인 모델
 * 캐시 테스트용 엔티티
 */
public class Book {

    private String isbn;
    private String title;
    private String author;
    private int price;
    private boolean hardback;  // 양장본 여부 - unless 조건 테스트용

    public Book() {}

    public Book(String isbn, String title, String author, int price) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.price = price;
        this.hardback = false;
    }

    public Book(String isbn, String title, String author, int price, boolean hardback) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.price = price;
        this.hardback = hardback;
    }

    // Getters and Setters
    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isHardback() {
        return hardback;
    }

    public void setHardback(boolean hardback) {
        this.hardback = hardback;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(isbn, book.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }

    @Override
    public String toString() {
        return "Book{isbn='" + isbn + "', title='" + title + "', author='" + author +
               "', price=" + price + ", hardback=" + hardback + "}";
    }
}
