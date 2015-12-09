package com.example.tud23846.stockwatcher;

/**
 * Created by tud23846 on 12/5/2015.
 */
public class URLParser {

    public void StockInfo(String[] symbols) {
        int length = symbols.length;
        String symbolString = "";
        while(length > 0)
        {
            symbolString = symbolString + symbols[length];
        }
        String stockInfoURL = "http://finance.yahoo.com/webservice/v1/symbols/" + symbolString + "/quote?format=json&view=basic";
    }

    public String News(String[] symbols){
        int length = symbols.length;
        String symbolString = "";
        while(length > 0)
        {
            symbolString = symbolString + symbols[length];
        }
        String newsURL = "http://finance.yahoo.com/rss/headline?s=" + symbolString;
        return newsURL;
    }

    public String StockChart(String symbol){

        String stockChartURL = "https://chart.yahoo.com/z?t=<chart_code>&s=" + symbol;
        return stockChartURL;
    }

    public String CompanySearch(String[] symbols){
        int length = symbols.length;
        String symbolString = "";
        while(length > 0)
        {
            symbolString = symbolString + symbols[length];
        }
        String companySearchURL = "http://dev.markitondemand.com/MODApis/Api/v2/Lookup/json?input="+ symbolString;
        return companySearchURL;
    }

}
