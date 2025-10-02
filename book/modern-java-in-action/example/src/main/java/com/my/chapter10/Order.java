package com.my.chapter10;

import java.util.ArrayList;
import java.util.List;

public class Order {

    private String customer;
    private List<Trade> trades = new ArrayList<>();

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public void addTrade(Trade trade) {
        trades.add(trade);
    }

    public double getValue() {
        return trades.stream().mapToDouble(Trade::getPrice).sum();
    }

    @Override
    public String toString() {
        return "Order for " + customer + " with trades: " + trades;
    }
}
