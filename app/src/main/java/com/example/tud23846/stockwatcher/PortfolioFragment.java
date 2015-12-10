package com.example.tud23846.stockwatcher;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.tud23846.stockwatcher.db.StockDBContract;
import com.example.tud23846.stockwatcher.db.StockDBHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

public class PortfolioFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;

    SQLiteDatabase db;
    StockDBHelper mDbHelper;
    ListView myPortfolio;
    String symbol;
    ArrayList symbolsFromDb = new ArrayList();
    ArrayList stockNames = new ArrayList();
    ArrayList stockSymbols = new ArrayList();
    ArrayList stockPrices = new ArrayList();
    ArrayList stockChanges = new ArrayList();
    String joined;
    int count;


    public static PortfolioFragment newInstance(String param1, String param2) {
        PortfolioFragment fragment = new PortfolioFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    public PortfolioFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDbHelper = new StockDBHelper(getActivity());
        myPortfolio = (ListView) getActivity().findViewById(R.id.portfolioListView);
        populateListView();

        joined = TextUtils.join(",", symbolsFromDb);
        new AsyncTaskPopulateListView().execute(symbol);



        myPortfolio.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String symbol = (String) stockSymbols.get(position);
                mListener.onFragmentInteraction(symbol);
                Log.d("RICH", "after click");
            }
        });



    }


    @Override
    public void onClick(View v) {
        mListener.onFragmentInteraction(symbol);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_portfolio, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String symbol);
    }

    private Void populateListView() {
        Log.d("Rich", "Test");

        db = mDbHelper.getReadableDatabase();
        try {
            Cursor c = db.rawQuery("SELECT * FROM " + StockDBContract.StockEntry.TABLE_NAME, null);

            int Column1 = c.getColumnIndex("symbol");

            // Check if our result was valid.
            c.moveToFirst();
            if (c != null) {
                // Loop through all Results
                do {
                    symbol = c.getString(Column1);
                    symbolsFromDb.add(symbol);
                    Log.d("RICH", symbol);
                    count++;
                } while (c.moveToNext());
            }

        } catch (Exception e) {
            Log.e("Error", "Error", e);
        } finally {
            if (db != null)
                db.close();
        }

        return null;

    }


    public class AsyncTaskPopulateListView extends AsyncTask<String, Void, ArrayList> {
        ArrayList stockInfo = new ArrayList();

        @Override
        protected ArrayList doInBackground(String... params) {
            Log.d("Rich", "AsyncStart");
            stockNames.clear();
            stockPrices.clear();
            stockSymbols.clear();
            stockChanges.clear();

            try {
                String url = "http://finance.yahoo.com/webservice/v1/symbols/" + joined + "/quote?format=json&view=basic";
                URL myUrl = new URL(url);

                BufferedReader reader = new BufferedReader(new InputStreamReader(myUrl.openStream()));
                String response = "", tmpResponse;
                tmpResponse = reader.readLine();
                while (tmpResponse != null) {
                    response = response + tmpResponse;
                    tmpResponse = reader.readLine();
                }

                try {
                    JSONObject responseObject = new JSONObject(response);


                    JSONObject c = responseObject.getJSONObject("list").getJSONArray("resources").getJSONObject(0);
                    JSONArray array = responseObject.getJSONObject("list").getJSONArray("resources");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject x = array.getJSONObject(i).getJSONObject("resource").getJSONObject("fields");
                        stockNames.add(x.getString("name"));
                        stockSymbols.add(x.getString("symbol"));
                        stockPrices.add(x.getDouble("price"));
                        stockChanges.add(x.getDouble("change"));

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
                return stockInfo;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return stockInfo;
        }

        @Override
        protected void onPostExecute(ArrayList stockInfo1) {
            try {

                myPortfolio.setAdapter(new PortfolioListAdapter(getActivity(), stockSymbols, stockNames, stockPrices, stockChanges));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

}