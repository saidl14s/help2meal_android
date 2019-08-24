package com.itcg.help2meal;


import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;


public class EnfermedadAdapter extends ArrayAdapter<Enfermedad> implements View.OnClickListener {

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
    }

    private final Context context;
    private final ArrayList<Enfermedad> recipe;

    public EnfermedadAdapter(Activity context, ArrayList<Enfermedad> recipe) {
        super(context, R.layout.list_enfermedades, recipe);
        this.context = context;
        this.recipe = recipe;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        final Enfermedad dataModel = getItem(position);


        viewHolder = new ViewHolder();
        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(R.layout.list_enfermedades, parent, false);



        CheckBox item = (CheckBox) convertView.findViewById(R.id.checkBox_enfermedad);
        item.setChecked(dataModel.isActivo());
        item.setText(dataModel.getNombre());
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((CompoundButton) view).isChecked()){
                    //Toast.makeText(context, "check "+dataModel.getNombre()+" "+dataModel.getId(), Toast.LENGTH_LONG).show();
                    dataModel.setActivo(true);
                } else {
                    //Toast.makeText(context, "un-check "+dataModel.getNombre()+" "+dataModel.getId(), Toast.LENGTH_LONG).show();
                    dataModel.setActivo(false);
                }
            }
        });


        convertView.setTag(viewHolder);

        return convertView;
    }
}