package com.example.stockwatch;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;


public class StocksAdapter extends RecyclerView.Adapter<MyViewHolder>{

    private static final String TAG = "StocksAdapter";
    private List<Stock> stockList;
    private MainActivity mainAct;


    public StocksAdapter(List<Stock> empList, MainActivity ma) {
        this.stockList = empList;
        mainAct = ma;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Log.d(TAG, "onCreateViewHolder: MAKING NEW");
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.stock_list_row, viewGroup, false);

        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int position) {

        Stock stock = stockList.get(position);

        Double var = stock.getPrice_change();
        if(var > 0) {
            myViewHolder.company_name.setTextColor(Color.GREEN);
            myViewHolder.symbol.setTextColor(Color.GREEN);
            myViewHolder.current_price.setTextColor(Color.GREEN);

            int color = Color.parseColor("#00FF00");
            myViewHolder.imageView.setColorFilter(color);
            myViewHolder.imageView.setImageResource(R.drawable.sharp_arrow_drop_up_24);

            myViewHolder.price_change.setTextColor(Color.GREEN);
            myViewHolder.price_change_per.setTextColor(Color.GREEN);
        }
        else{
            myViewHolder.company_name.setTextColor(Color.RED);
            myViewHolder.symbol.setTextColor(Color.RED);
            myViewHolder.current_price.setTextColor(Color.RED);

            int color = Color.parseColor("#FF0000");
            myViewHolder.imageView.setColorFilter(color);
            myViewHolder.imageView.setImageResource(R.drawable.sharp_arrow_drop_down_24);

            myViewHolder.price_change.setTextColor(Color.RED);
            myViewHolder.price_change_per.setTextColor(Color.RED);
        }
        myViewHolder.company_name.setText(stock.getName());
        myViewHolder.symbol.setText(stock.getSymbol());
        myViewHolder.current_price.setText(Double.toString(stock.getCurrent_price()));
        myViewHolder.price_change.setText(Double.toString(stock.getPrice_change()));
        myViewHolder.price_change_per.setText("(" + stock.getPrice_change_per() + "%)");

    }


    @Override
    public int getItemCount() {
        return stockList.size();
    }
}
