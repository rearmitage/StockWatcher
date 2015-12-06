package com.example.tud23846.stockwatcher;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tud23846 on 12/5/2015.
 */
public class AsyncStockInfo extends AsyncTask<String, String, String> {

    String yourJsonStringUrl;

    public AsyncStockInfo(String URL)
    {
        yourJsonStringUrl = URL;
    }


    final String TAG = "AsyncTaskParseJson.java";


    // contacts JSONArray
    JSONArray dataJsonArr = null;

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected String doInBackground(String... arg0) {

        try {

            // instantiate our json parser
            JsonParser jParser = new JsonParser();

            // get json string from url
            JSONObject json = jParser.getJSONFromUrl(yourJsonStringUrl);

            // get the array of users
            dataJsonArr = json.getJSONArray("resources");

            // loop through all users
            for (int i = 0; i < dataJsonArr.length(); i++) {

                JSONObject c = dataJsonArr.getJSONObject(i);

                // Storing each json item in variable
                String name = c.getString("name");
                String price = c.getString("price");
                String symbol = c.getString("symbol");
                String change = c.getString("change");


                // show the values in our logcat
                Log.e(TAG, "name: " + name
                        + ", price: " + price
                        + ", symbol: " + symbol
                        + ", change: " + change);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String strFromDoInBg) {
//http://stackoverflow.com/questions/12071451/android-asynctask-return-multiple-values-in-doinbackground-to-be-used-for-onpost
    }
}
