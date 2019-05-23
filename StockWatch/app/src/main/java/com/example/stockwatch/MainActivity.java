package com.example.stockwatch;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static android.widget.Toast.*;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener{

    private static final String TAG = "MainActivity";
    private List<Stock> stockList = new ArrayList<>();  // Main content is here
    private List<Stock> tempList = new ArrayList<>();

    private RecyclerView recyclerView; // Layout's recyclerview

    private StocksAdapter sAdapter; // Data to recyclerview adapter

    private SwipeRefreshLayout swiper;

    private String user_entered_stock_name;

    private DatabaseHandler databaseHandler;

    private String[] key = {};
    private String[] value = {};

    private EditText et1;

    private static final String webURL = "http://www.marketwatch.com/investing/stock/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swiper = findViewById(R.id.swiper);


        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        sAdapter = new StocksAdapter(stockList, this);

        recyclerView.setAdapter(sAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                HashMap<String,String> list = databaseHandler.loadStocks();
                stockList.clear();

                if(doNetCheck()==1){
                    callAsyncAfterSwipe(list);
                }
                else{
                    disappearingDialog("No Network Connection","Stocks Cannot Be Added Without A Network Connection");
                    }
                swiper.setRefreshing(false); // This stops the busy-circle
            }
        });

        databaseHandler = new DatabaseHandler(this);
    }

    public void callAsyncAfterSwipe(HashMap<String,String> list){
        if(list.size()>0) {

            key = list.keySet().toArray(new String[0]);
            value = list.values().toArray(new String[0]);

            String CODE = "3";
            for (int i = 0; i < key.length; i++) {
                new MyAsyncTask(this).execute(CODE, key[i], value[i]);
            }
        }
    }

    public void disappearingDialog(String title, String message){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setTitle(title).setMessage(message);

        final AlertDialog alert = dialog.create();
        alert.show();
        new CountDownTimer(5000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {}

            @Override
            public void onFinish() { alert.dismiss(); }
        }.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return true;
    }


    private int doNetCheck() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        /*if (cm == null) {
            //Toast.makeText(this, "Cannot access ConnectivityManager", Toast.LENGTH_SHORT).show();
            return;
        }*/
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
           return 1;
        } else {
            return 0;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.addStock:
                int i = doNetCheck();
                if(i==1) {
                    LayoutInflater inflater = LayoutInflater.from(this);
                    final View view = inflater.inflate(R.layout.add_stock_dialog, null);

                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Please enter a stock symbol:");
                    builder.setTitle("Stock Selection");

                    builder.setView(view);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, int id) {

                            et1 = (EditText) view.findViewById(R.id.stockName);
                            et1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                            et1.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

                                user_entered_stock_name = et1.getText().toString();
                                if(user_entered_stock_name==null){
                                    disappearingDialog("Empty Stock Symbol","Please Enter Stock Symbol");
                                    Toast.makeText(getApplicationContext(),"Please enter stock symbol properly",Toast.LENGTH_SHORT).show();
                                }
                                else if(user_entered_stock_name.equals(user_entered_stock_name.toLowerCase()) && user_entered_stock_name!=null)
                                {
                                    //disappearingDialog("Please enter stock symbol in Uppercase","");
                                    Toast.makeText(getApplicationContext(),"Please enter stock symbol properly",Toast.LENGTH_SHORT).show();
                                }

                                else
                                    callAsyncForSymbol(user_entered_stock_name);

                        }
                    });
                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });

                    final AlertDialog dialog = builder.create();
                    dialog.show();

                }
                else{
                    disappearingDialog("No Network Connection","Stocks Cannot Be Added Without A Network Connection");
                }
            return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {

        HashMap<String,String> list = databaseHandler.loadStocks();
        stockList.clear();

        if(doNetCheck()==1){
            if(list.size()>0) {

                key = list.keySet().toArray(new String[0]);
                value = list.values().toArray(new String[0]);

                String CODE = "3";
                for (int i = 0; i < key.length; i++) {
                    new MyAsyncTask(this).execute(CODE, key[i], value[i]);
                }

                //afterOnResume();
                //stockList.addAll(list);
                Log.d(TAG, "onResume: " + list);
                //sAdapter.notifyDataSetChanged();
            }

        }
        else{
            disappearingDialog("No Network Connection","Stocks Cannot Be Added Without A Network Connection");

            if(list.size()>1){
                key = list.keySet().toArray(new String[0]);
                value = list.values().toArray(new String[0]);

                for(int j=0;j<list.size();j++) {
                    stockList.add(new Stock(key[j],value[j],0.0,0.0,"0"));
                    Collections.sort(stockList);
                    sAdapter.notifyDataSetChanged();
                }
            }

        }
        //afterOnResume();
        super.onResume();
    }

    public void addToTempList(ArrayList<Stock> cList){

        stockList.addAll(cList);
        Collections.sort(stockList);
        sAdapter.notifyDataSetChanged();
        //tempList.addAll(cList);
        //stockList.addAll(cList);
        //Collections.sort(stockList);
        //sAdapter.notifyDataSetChanged();
        /*Collections.sort(stockList);
        sAdapter.notifyDataSetChanged();*/
    }

    public void afterOnResume(){
        //Collections.copy(stockList,tempList);
        //stockList = tempList;

    }

    @Override
    protected void onDestroy() {
        databaseHandler.shutDown();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        int pos = recyclerView.getChildLayoutPosition(view);
        Stock m = stockList.get(pos);
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(webURL + m.getSymbol()));
        startActivity(i);
    }

    @Override
    public boolean onLongClick(View view) {

        final int pos = recyclerView.getChildLayoutPosition(view);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                databaseHandler.deleteStock(stockList.get(pos).getSymbol());
                stockList.remove(pos);
                sAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        builder.setIcon(R.drawable.baseline_delete_24);
        builder.setMessage("Delete Stock " + stockList.get(pos).getSymbol() + "?");
        builder.setTitle("Delete Stock");

        AlertDialog dialog = builder.create();
        dialog.show();

        return true;
    }

    public void displayItemDialog(HashMap<String,String> cList) {

        final String[] key = cList.keySet().toArray(new String[0]);
        final String[] value = cList.values().toArray(new String[0]);

        if(cList.size()==0)
        {
            disappearingDialog("Symbol Not Found: "+ user_entered_stock_name,"Data for stck symbol");

        }
        else if(cList.size()==1) {
            int flag;
            flag = databaseHandler.checkAlreadyExist(key[0]);
            if(flag==1){
                callAsyncForFinancialData(key[0], value[0]);
            }
            else{
                final AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                        .setTitle("Duplicate Stock ").setIcon(R.drawable.baseline_report_problem_24).setMessage(
                                "Stock Symbol " + user_entered_stock_name + " is already displayed");

                final AlertDialog alert = dialog.create();
                alert.show();
                new CountDownTimer(3000, 1000) {

                    @Override
                    public void onTick(long millisUntilFinished) { }

                    @Override
                    public void onFinish() { alert.dismiss(); }
                }.start();
            }

        }
         else{
            final String[] complete = new String[cList.size()];
            for (int i = 0; i < cList.size(); i++) {
                complete[i] = key[i] + " - " + value[i];
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Make a selection");
            //builder.setIcon(R.drawable.icon2);

            builder.setItems(complete, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    //tv2.setText(sArray[which]);
                    String currentKey = key[which];
                    String currentValue = value[which];

                    dialog.cancel();
                    int flag = checkExistFromItemView(currentKey);
                    if(flag==1)
                        {callAsyncForFinancialData(currentKey, currentValue);}

                }
            });

            builder.setNegativeButton("Nevermind", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });
            AlertDialog dialog = builder.create();

            dialog.show();
        }

    }

    public  int checkExistFromItemView(String s){
        int flag = databaseHandler.checkAlreadyExist(s);
        if(flag == 0)
        {
            final AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                    .setTitle("Duplicate Stock ").setIcon(R.drawable.baseline_report_problem_24).setMessage(
                            "Stock Symbol " + s + " is already displayed");

            final AlertDialog alert = dialog.create();
            alert.show();
            new CountDownTimer(3000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) { }

                @Override
                public void onFinish() { alert.dismiss();}
            }.start();
        }

        return flag;
    }
    public  void callAsyncForSymbol(String s){
        String CODE = "1";
        new MyAsyncTask(this).execute(CODE,s);
    }

    public void callAsyncForFinancialData (String key, String value){
        String CODE = "2";
        new MyAsyncTask(this).execute(CODE,key,value);
    }

    public void updateData(ArrayList<Stock> cList) {
        stockList.addAll(cList);
        Collections.sort(stockList);
        sAdapter.notifyDataSetChanged();

        databaseHandler.addStocks(cList);

    }
}
