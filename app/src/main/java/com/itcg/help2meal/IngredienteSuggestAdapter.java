package com.itcg.help2meal;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;

import java.util.ArrayList;

import nl.dionsegijn.steppertouch.OnStepCallback;
import nl.dionsegijn.steppertouch.StepperTouch;

public class IngredienteSuggestAdapter extends ArrayAdapter<Ingrediente> implements View.OnClickListener {

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
    }

    private final Context context;
    private final ArrayList<Ingrediente> ingredientes;

    public IngredienteSuggestAdapter(Activity context, ArrayList<Ingrediente> ingredientes) {
        super(context, R.layout.list_ingredient_suggest, ingredientes);
        this.context = context;
        this.ingredientes = ingredientes;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        final Ingrediente dataModel = getItem(position);


        viewHolder = new ViewHolder();
        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(R.layout.list_ingredient_suggest, parent, false);


        SimpleDraweeView draweeView = (SimpleDraweeView) convertView.findViewById(R.id.iv_image_ingredient);
        draweeView.setImageURI(dataModel.getUrl_imagen());
        //Log.i("HELP2MEAL", dataModel.getNombre() +" "+dataModel.getUnidad()+" "+dataModel.getUrl_imagen());
        TextView tipo_cantidad = (TextView) convertView.findViewById(R.id.ingredient_suggest);
        String unidad = "";
        if(dataModel.getCantidad() > 1) unidad = dataModel.getUnidad() + "s";

        tipo_cantidad.setText(dataModel.getCantidad() + " "+ unidad +" de " +dataModel.getNombre().toLowerCase());


        convertView.setTag(viewHolder);

        return convertView;
    }
}