package com.example.stockwatch;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class MyAsyncTask extends AsyncTask<String, Integer, String> {

    private MainActivity mainActivity;
    private static final String SYMBOL_URL = "https://api.iextrading.com/1.0/ref-data/symbols";

    private static final String TAG = "MyAsyncTask";

    //private DialogInterface.OnClickListener ms;

    private String symbol_name;
    private int code;
    private String key;
    private String value;

    private static DecimalFormat precision = new DecimalFormat("#.##");
    //private DatabaseHandler databaseHandler;

    public MyAsyncTask(MainActivity ma) {
        mainActivity = ma;
    }



    @Override
    protected void onPreExecute() {
        Toast.makeText(mainActivity, "Please wait for the result.", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onPostExecute(String s) {

        if(code==1)
        {
            HashMap<String,String> stockHM = parseJsonForSymbol(s);
            if (stockHM != null)
                //Toast.makeText(mainActivity, "Loaded " + stockHM.size() + " Stocks.", Toast.LENGTH_SHORT).show();
            mainActivity.displayItemDialog(stockHM);
        }
        else if(code==2)
        {
            ArrayList<Stock> stockList = parseJsonForFinancialData(s);

            if (stockList != null)
                //Toast.makeText(mainActivity, "Loaded " + stockList.size() + " Stocks.", Toast.LENGTH_SHORT).show();
            mainActivity.updateData(stockList);
        }
        else {
            ArrayList<Stock> stockList = parseJsonForFinancialData(s);

            if (stockList != null)
                //Toast.makeText(mainActivity, "Loaded " + stockList.size() + " Stocks.", Toast.LENGTH_SHORT).show();
            mainActivity.addToTempList(stockList);
        }
    }



    @Override
    protected String doInBackground(String... params) {
        Uri dataUri;
        if(params[0]=="2"){
            code = 2;
            key = params[1];
            value = params[2];
            dataUri = Uri.parse("https://api.iextrading.com/1.0/stock/" + key + "/quote?displayPercent=true");
        }
        else if(params[0]=="3"){
            code = 3;
            key = params[1];
            value = params[2];
            dataUri = Uri.parse("https://api.iextrading.com/1.0/stock/" + key + "/quote?displayPercent=true");
        }
        else{
            code = 1;
            symbol_name = params[1];
            dataUri = Uri.parse(SYMBOL_URL);
        }
        /*if(params[0]=="1")
        {
            code = 1;
            symbol_name = params[1];
            dataUri = Uri.parse(SYMBOL_URL);
        }
        else
        {
            code = 2;
            key = params[1];
            value = params[2];
            dataUri = Uri.parse("https://api.iextrading.com/1.0/stock/" + key + "/quote?displayPercent=true");
        }*/

        String urlToUse = dataUri.toString();
        Log.d(TAG, "doInBackground: " + urlToUse);

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            Log.d(TAG, "doInBackground: ResponseCode: " + conn.getResponseCode());

            conn.setRequestMethod("GET");

            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            if(code==2 || code==3) {
                sb.append("[");
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
                sb.append("]");
            }
            else{
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
            }
            Log.d(TAG, "doInBackground: " + sb.toString());

        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ", e);
            return null;
        }

        Log.d(TAG, "doInBackground: " + sb.toString());

        return sb.toString();
    }

    private HashMap<String, String> parseJsonForSymbol(String s) {


        HashMap<String,String> hm  = new HashMap<String,String>();
        try
        {
            JSONArray jObjMain = new JSONArray(s);


                for (int i = 0; i < jObjMain.length(); i++)
                {
                    JSONObject jStock = (JSONObject) jObjMain.get(i);
                    String symbol = jStock.getString("symbol");
                    String name = jStock.getString("name");
                    if(symbol==symbol_name){
                        hm.put(symbol,name);
                        return hm;
                    }
                    if(symbol.contains(symbol_name) || name.contains(symbol_name)) {
                        hm.put(symbol, name);
                    }
                    else{

                    }
                }

            return hm;
        }
        catch (Exception e)
        {
            Log.d(TAG, "parseJsonForSymbol: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }


    private ArrayList<Stock> parseJsonForFinancialData(String s) {

        ArrayList<Stock> stockList = new ArrayList<>();
        try {
            JSONArray jObjMain = new JSONArray(s);

            //for (int i = 0; i < jObjMain.length(); i++) {
                JSONObject jStock = (JSONObject) jObjMain.get(0);

                    String symbol = jStock.getString("symbol");

                    String name = jStock.getString("companyName");

                    String lP = jStock.getString("latestPrice");
                    Double latestPrice=0.0;
                    if (lP != null && !lP.trim().isEmpty() && !lP.trim().equals("null"))
                        latestPrice = Double.parseDouble(lP);

                    String pc = jStock.getString("change");
                    Double price_change=0.0;
                    if (pc != null && !pc.trim().isEmpty() && !pc.trim().equals("null"))
                        price_change = Double.parseDouble(pc);

                    String pcp = jStock.getString("changePercent");
                    Double price_change_per = 0.0;
                    if (pcp != null && !pcp.trim().isEmpty() && !pcp.trim().equals("null"))
                        price_change_per =  Double.parseDouble(pcp);



                    Stock stock = new Stock(symbol, name, latestPrice, price_change, precision.format(price_change_per));
                    //databaseHandler.addStocks(stock);
                stockList.add(stock);

            //}
            return stockList;
        } catch (Exception e) {
            Log.d(TAG, "parseJsonForFinancialData: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

}
