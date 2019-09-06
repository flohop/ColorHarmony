package com.example.colorharmony;

import android.content.Context;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class SupportAdapter extends ArrayAdapter<SupportMeObject> implements View.OnClickListener {

    private ArrayList<SupportMeObject> dataSet;
    Context context;

    private static class ViewHolder {
        ImageView supportIcon;
        TextView support_text;
        TextView support_price;
    }

    public SupportAdapter(ArrayList<SupportMeObject> data, Context context) {
        super(context, R.layout.support_row, data);
        this.dataSet = data;
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();

        Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        SupportMeObject dataModel = getItem(position);

        ViewHolder viewHolder;

        final View result;

        if(convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.support_row, parent, false);

            viewHolder.supportIcon = (ImageView) convertView.findViewById(R.id.support_row_icon);
            viewHolder.support_text = (TextView) convertView.findViewById(R.id.support_row_text);
            viewHolder.support_price = (TextView) convertView.findViewById(R.id.support_row_price);


            result = convertView;
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }
    try {
        viewHolder.supportIcon.setImageResource(dataModel.getIcon_resource());
        viewHolder.support_text.setText((dataModel.getName()));
        viewHolder.support_price.setText(dataModel.getStringPrice());
    }
    catch (NullPointerException e){

    }

        return  convertView;
    }
}
