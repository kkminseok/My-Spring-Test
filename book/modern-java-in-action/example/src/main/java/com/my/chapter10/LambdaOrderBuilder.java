package com.my.chapter10;

import java.util.function.Consumer;

public class LambdaOrderBuilder {

    private Order order = new Order();

    public static Order order(Consumer<LambdaOrderBuilder> consumer) {
        LambdaOrderBuilder orderBuilder = new LambdaOrderBuilder();
        consumer.accept(orderBuilder);
        return orderBuilder.order;
    }

    public void forCustomer(String customer) {
        order.setCustomer(customer);
    }

    public void buy(Consumer<TradeBuilder> consumer) {
        trade(consumer, Trade.Type.BUY);
    }

    public void sell(Consumer<TradeBuilder> consumer) {
        trade(consumer, Trade.Type.SELL);
    }

    private void trade(Consumer<TradeBuilder> consumer, Trade.Type type) {
        TradeBuilder tradeBuilder = new TradeBuilder();
        tradeBuilder.trade.setType(type);
        consumer.accept(tradeBuilder);
        order.addTrade(tradeBuilder.trade);
    }
}
