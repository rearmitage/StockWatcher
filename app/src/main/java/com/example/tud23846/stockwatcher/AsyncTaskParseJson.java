package com.example.tud23846.stockwatcher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by tud23846 on 12/5/2015.
 */
//https://www.codeofaninja.com/2013/11/android-json-parsing-tutorial.html

 public class AsyncTaskParseJson extends AsyncTask<String, String, String> {

    String yourJsonStringUrl;

    public AsyncTaskParseJson(String URL)
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
            dataJsonArr = json.getJSONArray("Users");

            // loop through all users
            for (int i = 0; i < dataJsonArr.length(); i++) {

                JSONObject c = dataJsonArr.getJSONObject(i);

                // Storing each json item in variable
                String firstname = c.getString("firstname");
                String lastname = c.getString("lastname");
                String username = c.getString("username");

                // show the values in our logcat
                Log.e(TAG, "firstname: " + firstname
                        + ", lastname: " + lastname
                        + ", username: " + username);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String strFromDoInBg) {

    }
}


