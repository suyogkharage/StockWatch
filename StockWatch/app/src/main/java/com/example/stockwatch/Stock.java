package com.example.stockwatch;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class Stock implements Serializable, Comparable<Stock> {
    private String company_name;
    private String symbol;
    private Double current_price;
    private Double price_change;
    private String price_change_per;


    private static int ctr = 1;

    public Stock(String symbol, String name, Double current_price, Double price_change, String price_change_per) {
        this.company_name = name;
        this.symbol = symbol;
        this.current_price = current_price;
        this.price_change = price_change;
        this.price_change_per = price_change_per;

        ctr++;
    }

    public String getName() {
        return company_name;
    }

    public String getSymbol() {
        return symbol;
    }

    public Double getCurrent_price() {return current_price;}

    public Double getPrice_change() {
        return price_change;
    }

    public String getPrice_change_per() {
        return price_change_per;
    }

    @Override
    public int compareTo(@NonNull Stock o) {
        return getSymbol().compareTo(o.getSymbol());
        /*if(current_price > o.current_price)
            return -1;
        else if(current_price < o.current_price)
                return  1;
        else
            return 0;*/
    }


}
