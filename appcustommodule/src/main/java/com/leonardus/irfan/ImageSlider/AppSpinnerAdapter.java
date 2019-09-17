package com.leonardus.irfan.ImageSlider;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.leonardus.irfan.R;
import com.leonardus.irfan.SimpleObjectModel;

import java.util.List;

public class AppSpinnerAdapter extends ArrayAdapter<SimpleObjectModel> {

    private Context context;
    private List<SimpleObjectModel> list;

    public AppSpinnerAdapter(@NonNull Context context, List<SimpleObjectModel> list) {
        super(context, R.layout.custom_spinner_item , list);
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.custom_spinner_item,parent,false);

        SimpleObjectModel item = list.get(position);
        TextView txt_spinner_item = listItem.findViewById(R.id.txt_item);
        txt_spinner_item.setText(item.getValue());

        return listItem;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        TextView v = (TextView) super.getView(position, convertView, parent);
        v.setText(list.get(position).getValue());
        return v;
    }
}
