package com.example.tud23846.stockwatcher;

import android.app.Activity;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class DetailsFragment extends Fragment {

    Button btnAddStock;
    AutoCompleteTextView advanced;
    ImageView stockGraph;
    String myURL;// = "https://chart.yahoo.com/z?t=%3Cchart_code%3E&s=GOOG";


    public static DetailsFragment newInstance(String param1, String param2) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //mListener = (OnFragmentInteractionListener) getActivity();

        btnAddStock = (Button) getActivity().findViewById(R.id.btnAddStock);
        stockGraph = (ImageView) getActivity().findViewById(R.id.stockGraph);

        if (myURL != null) {
            new AsyncGetGraph().execute(myURL);
        }

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

        //@Override
       // protected void onPostExecute(String[] suggestions) {
       //     try {

       //     } catch (Exception e) {
      //          e.printStackTrace();
      //      }
      //  }

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
                            }

                            else if(name.equals("link")){
                                link = text;
                            }

                            else{
                            }
                            break;
                    }
                    event = myParser.next();
                }
                parsingComplete = false;
            }

            catch (Exception e) {
                e.printStackTrace();
            }
        }


    }
}