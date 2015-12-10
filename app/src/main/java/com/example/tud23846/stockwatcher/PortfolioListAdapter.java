package com.example.tud23846.stockwatcher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Rich on 12/9/2015.
 */
public class PortfolioListAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList stockSymbols;
    private final ArrayList stockNames;
    private final ArrayList stockPrices;
    private final ArrayList stockChanges;

    public PortfolioListAdapter(Context context, ArrayList stockSymbols, ArrayList stockNames, ArrayList stockPrices, ArrayList stockChanges) {
        super(context, -1, stockSymbols);
        this.context = context;
        this.stockSymbols = stockSymbols;
        this.stockNames = stockNames;
        this.stockPrices = stockPrices;
        this.stockChanges = stockChanges;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.portfolio_row_layout, parent, false);
        TextView txtName = (TextView) rowView.findViewById(R.id.txtName);
        TextView txtPrice = (TextView) rowView.findViewById(R.id.txtPrice);
        TextView txtSymbol = (TextView) rowView.findViewById(R.id.txtSymbol);
        ImageView imgArrow = (ImageView) rowView.findViewById(R.id.imgArrow);
        txtName.setText(stockNames.get(position).toString());
        txtPrice.setText(stockPrices.get(position).toString());
        txtSymbol.setText(stockSymbols.get(position).toString());

        double arrow = (double)stockChanges.get(position);

        if( arrow >= 0)
        {
            imgArrow.setImageResource(R.drawable.stock_index_up);
        }if(arrow <= 0){
            imgArrow.setImageResource(R.drawable.stock_index_down);
        }

        return rowView;
    }
}
