package com.example.tud23846.stockwatcher;

import android.app.Activity;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tud23846.stockwatcher.db.StockDBContract;
import com.example.tud23846.stockwatcher.db.StockDBHelper;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.Buffer;
import java.util.ArrayList;


public class DetailsFragment extends Fragment implements View.OnClickListener {
    private OnFragmentInteractionListener mListener;

    Button btnAddRemove;
    Button btnSearch;
    Button btnPortfolio;
    Button btn1day;
    Button btn5days;
    Button btn1month;
    Button btn6months;
    Button btn1year;
    TextView txtStockName;
    TextView txtStockSymbol;
    TextView txtStockPrice;
    TextView txtStockChange;
    TextView txtStockVolume;
    String switchtoPort;

    AutoCompleteTextView advanced;
    ImageView stockGraph;
    ListView newsList;
    String symbol;
    ArrayList newsTitle = new ArrayList();
    ArrayList newsLink = new ArrayList();

    Boolean haveStock = false;

    SQLiteDatabase db;
    StockDBHelper mDbHelper;


    public static DetailsFragment newInstance(String param1, String param2) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        switchtoPort = "switchToPort";
        mDbHelper = new StockDBHelper(getActivity());
        symbol = getArguments().getString("number");

        txtStockName = (TextView) getActivity().findViewById(R.id.txtName);
        txtStockSymbol =(TextView) getActivity().findViewById(R.id.txtSymbol);
        txtStockPrice = (TextView) getActivity().findViewById(R.id.txtPrice);
        txtStockChange = (TextView) getActivity().findViewById(R.id.txtChange);
        txtStockVolume = (TextView) getActivity().findViewById(R.id.txtVolume);

        btnSearch = (Button) getActivity().findViewById(R.id.btnsearch);
            btnSearch.setOnClickListener(this);
        btnAddRemove = (Button) getActivity().findViewById(R.id.btnAddRemove);
            btnAddRemove.setOnClickListener(this);
        btnPortfolio = (Button) getActivity().findViewById(R.id.btnPortfolio);
            btnPortfolio.setOnClickListener(this);
        btn1day = (Button) getActivity().findViewById(R.id.btn1Day);
            btn1day.setOnClickListener(this);
        btn5days = (Button) getActivity().findViewById(R.id.btn5days);
            btn5days.setOnClickListener(this);
        btn1month = (Button) getActivity().findViewById(R.id.btn1Month);
            btn1month.setOnClickListener(this);
        btn6months = (Button) getActivity().findViewById(R.id.btn6months);
            btn6months.setOnClickListener(this);
        btn1year = (Button) getActivity().findViewById(R.id.btn1year);
            btn1year.setOnClickListener(this);
        stockGraph = (ImageView) getActivity().findViewById(R.id.stockGraph);

        newsList = (ListView) getActivity().findViewById(R.id.lvStockNews);
        checkDB(symbol);
        if(symbol!=null) {
            new AsyncGetGraph().execute("https://chart.yahoo.com/z?t=1d&s="+symbol);
            new AsyncTextGetInfo().execute(symbol);
            new AsyncGetNews().execute("http://finance.yahoo.com/rss/2.0/headline?s="+symbol+"&region=US&lang=en-US");
        }
        newsList.setAdapter(new MyListAdapter(getActivity(), newsTitle, newsLink));

        advanced = (AutoCompleteTextView) getActivity().findViewById(R.id.autoCompleteTextView);

        advanced.addTextChangedListener(new TextWatcher() {


            // We keep previous length to ensure that we only query for suggestions when the user enters new characters
            int previousTextLength;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                previousTextLength = charSequence.length();

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (charSequence.length() >= 2)
                    updateSuggestions(charSequence.toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        newsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String url =(String)newsLink.get(position);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });



    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnsearch:
                symbol = advanced.getText().toString();
                new AsyncGetGraph().execute("https://chart.yahoo.com/z?t=1d&s="+symbol);
                new AsyncTextGetInfo().execute(symbol);
                new AsyncGetNews().execute("http://finance.yahoo.com/rss/2.0/headline?s="+symbol+"&region=US&lang=en-US");
                break;

            case R.id.btnAddRemove:

                    try {
                        saveData(symbol);
                        Toast.makeText(getActivity(), "Stock Added", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "Error Not Added", Toast.LENGTH_SHORT).show();
                    }

                break;

                case R.id.btnPortfolio:
                    mListener.onFragmentInteraction(switchtoPort);
                break;

            case R.id.btn1Day:
                new AsyncGetGraph().execute("https://chart.yahoo.com/z?t=1d&s="+symbol);
                break;

            case R.id.btn5days:
                new AsyncGetGraph().execute("https://chart.yahoo.com/z?t=5d&s="+symbol);
                break;

            case R.id.btn1Month:
                new AsyncGetGraph().execute("https://chart.yahoo.com/z?t=1m&s="+symbol);
                break;

            case R.id.btn6months:
                new AsyncGetGraph().execute("https://chart.yahoo.com/z?t=6m&s="+symbol);
                break;

            case R.id.btn1year:
                new AsyncGetGraph().execute("https://chart.yahoo.com/z?t=1y&s="+symbol);
                break;

            default:
                break;
        }

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String switchtoPort);
    }

    public void updateSuggestions(String substring) {
        new AsyncTextPrediction().execute(substring);
    }


    public DetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_details, container, false);


        return v;

    }


    private void saveData(String symbol){

        // Gets the data repository in write mode
        db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(StockDBContract.StockEntry.COLUMN_NAME_SYMBOL, symbol);

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert( StockDBContract.StockEntry.TABLE_NAME,null,values);

        if (newRowId > 0) {
            Log.d("Stock data saved ", newRowId + " - " + symbol);
        }

    }


    public class AsyncGetGraph extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            InputStream is = null;
            Bitmap bMap = null;
            try {
                is = new java.net.URL(params[0]).openStream();
                bMap = BitmapFactory.decodeStream(is);
            } catch (MalformedURLException e) {
            } catch (IOException e) {
                e.printStackTrace();
            }

            return bMap;
        }

        public void onPostExecute(Bitmap graph) {
            stockGraph.setImageBitmap(graph);
        }

    }

    public class AsyncTextPrediction extends AsyncTask<String, Void, String[]> {
        @Override
        protected String[] doInBackground(String... params) {
            String[] suggestions = null;
            try {
                String url = "http://dev.markitondemand.com/MODApis/Api/v2/Lookup/json?input=" + params[0];

                String response = new BufferedReader(new InputStreamReader(new URL(url).openConnection().getInputStream())).readLine();

                try {
                    JSONArray responseObject = new JSONArray(response);
                    suggestions = new String[responseObject.length()];
                    for (int i = 0; i < responseObject.length(); i++)
                        suggestions[i] = responseObject.getJSONObject(i).getString("Symbol");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return suggestions;
        }

        @Override
        protected void onPostExecute(String[] suggestions) {
            try {
                advanced.setAdapter(new ArrayAdapter<>(getActivity().getBaseContext(), android.R.layout.simple_dropdown_item_1line, suggestions));
                advanced.setThreshold(2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    public class AsyncGetNews extends AsyncTask<String, Void, XmlPullParser> {
        private String title = "title";
        private String link = "link";
        private String urlString = null;
        private XmlPullParserFactory xmlFactoryObject;
        public volatile boolean parsingComplete = true;

        public void HandleXML(String url){
            this.urlString = url;
        }

        public String getTitle(){
            return title;
        }

        public String getLink(){
            return link;
        }

        @Override
        protected XmlPullParser doInBackground(String... params) {

            newsTitle.clear();
            newsLink.clear();
            try {
                    String urlString = params[0];
                    URL url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();

                    conn.setReadTimeout(10000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream stream = conn.getInputStream();
                    xmlFactoryObject = XmlPullParserFactory.newInstance();
                    XmlPullParser myparser = xmlFactoryObject.newPullParser();

                    myparser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                    myparser.setInput(stream, null);

                    parseXMLAndStoreIt(myparser);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
              return null;
        }

        @Override
        protected void onPostExecute(XmlPullParser xml) {
            try {

                newsList.setAdapter(new MyListAdapter(getActivity(), newsTitle, newsLink));

          } catch (Exception e) {
                e.printStackTrace();
            }
        }


        public void parseXMLAndStoreIt(XmlPullParser myParser) {
            int event;
            String text = null;

            try {
                event = myParser.getEventType();

                while (event != XmlPullParser.END_DOCUMENT) {
                    String name=myParser.getName();

                    switch (event){
                        case XmlPullParser.START_TAG:
                            break;

                        case XmlPullParser.TEXT:
                            text = myParser.getText();
                            break;

                        case XmlPullParser.END_TAG:
                            if(name.equals("title")){
                                title = text;
                                newsTitle.add(title);
                            }

                            else if(name.equals("link")){
                                link = text;
                                newsLink.add(link);
                            }

                            else{
                            }
                            break;
                    }
                    event = myParser.next();



                }
                newsTitle.remove(0);
                newsTitle.remove(0);
                //Test this next
                parsingComplete = false;
            }

            catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public class AsyncTextGetInfo extends AsyncTask<String, Void, ArrayList> {
        ArrayList stockInfo = new ArrayList();
        @Override
        protected ArrayList doInBackground(String... params) {



            try {
                String url = "http://finance.yahoo.com/webservice/v1/symbols/"+ symbol +"/quote?format=json&view=basic";
                URL myUrl = new URL(url);

                BufferedReader reader = new BufferedReader(new InputStreamReader(myUrl.openStream()));
                String response ="", tmpResponse;
                tmpResponse =reader.readLine();
                while(tmpResponse != null)
                {
                    response = response+tmpResponse;
                    tmpResponse = reader.readLine();
                }



                try {
                    JSONObject responseObject = new JSONObject(response);
                    JSONObject c = responseObject.getJSONObject("list").getJSONArray("resources").getJSONObject(0).getJSONObject("resource").getJSONObject("fields");
                    Log.d("HELP",response);

                        stockInfo.add(c.getString("name"));
                    stockInfo.add(c.getString("symbol"));
                        stockInfo.add(c.getDouble("price"));
                        stockInfo.add(c.getDouble("change"));
                    stockInfo.add(c.getDouble("volume"));

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return stockInfo;
            }catch (Exception e) {
                e.printStackTrace();
            }
            return stockInfo;
        }

        @Override
        protected void onPostExecute(ArrayList stockInfo1) {
            try {
                txtStockName.setText(stockInfo.get(0).toString());
                txtStockSymbol.setText(stockInfo.get(1).toString());
                txtStockPrice.setText("P: " + stockInfo.get(2).toString());
                txtStockChange.setText("C: " + stockInfo.get(3).toString());
                txtStockVolume.setText("V: " + stockInfo.get(4).toString());
            }

             catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    private Void checkDB(String symbol) {
        Log.d("Rich", "Test");
        Log.d("RICH", "STOCK CHANge");
        db = mDbHelper.getReadableDatabase();
        try {
            Cursor c = db.rawQuery("SELECT * FROM " + StockDBContract.StockEntry.TABLE_NAME, null);

            int Column1 = c.getColumnIndex("symbol");

            // Check if our result was valid.
            c.moveToFirst();
            if (c != null) {
                // Loop through all Results
                do {
                    String temp = c.getString(Column1);
                    Log.d("RICH", "TESTING " + temp);
                    if(temp == symbol)
                    {
                        btnAddRemove.setText("Remove");
                        haveStock = true;
                        Log.d("RICH", "STOCK CHANge");
                    }
                } while (c.moveToNext());
            }

        }catch(Exception e) {
            Log.e("Error", "Error", e);
        } finally {
            if (db != null)
                db.close();
        }

        return  null;

    }
}

