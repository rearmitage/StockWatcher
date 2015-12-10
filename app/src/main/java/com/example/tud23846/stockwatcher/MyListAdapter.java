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
 * Created by tud23846 on 12/8/2015.
 */
public class MyListAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final ArrayList newsTitles;
    private final ArrayList newsLinks;

    public MyListAdapter(Context context, ArrayList newsTitles, ArrayList newLinks) {
        super(context, -1, newsTitles);
        this.context = context;
        this.newsTitles = newsTitles;
        this.newsLinks = newLinks;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.txtNewsTitle);
        //ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        textView.setText(newsTitles.get(position).toString());
        // change the icon for Windows and iPhone
        String s = (newsTitles.get(position).toString());

        return rowView;
    }
}