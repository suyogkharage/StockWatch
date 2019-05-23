package com.example.stockwatch;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MyViewHolder extends RecyclerView.ViewHolder {

    public TextView company_name;
    public TextView symbol;
    public TextView current_price;
    public TextView price_change;
    public TextView price_change_per;
    public ImageView imageView;

    public MyViewHolder(View view) {
        super(view);

        company_name = view.findViewById(R.id.companyName);
        symbol = view.findViewById(R.id.symbol);
        current_price = view.findViewById(R.id.currentPrice);
        imageView = view.findViewById(R.id.imageView);
        price_change = view.findViewById(R.id.priceChange);
        price_change_per = view.findViewById(R.id.price_change_per);
    }
}
