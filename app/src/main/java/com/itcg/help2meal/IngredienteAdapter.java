package com.itcg.help2meal;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class IngredienteAdapter extends ArrayAdapter<Ingrediente> implements View.OnClickListener {

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
    }

    private final Context context;
    private final ArrayList<Ingrediente> ingredientes;

    public IngredienteAdapter(Activity context, ArrayList<Ingrediente> ingredientes) {
        super(context, R.layout.list_item, ingredientes);
        this.context = context;
        this.ingredientes = ingredientes;
    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();

        Snackbar.make(v, "" +v.getId(), Snackbar.LENGTH_LONG).setAction("No action", null).show();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        Ingrediente dataModel = getItem(position);


        viewHolder = new ViewHolder();
        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(R.layout.list_item, parent, false);


        SimpleDraweeView draweeView = (SimpleDraweeView) convertView.findViewById(R.id.iv_ingredient);
        draweeView.setImageURI(dataModel.getUrl_imagen());
        //Log.i("HELP2MEAL", dataModel.getNombre() +" "+dataModel.getUnidad()+" "+dataModel.getUrl_imagen());
        TextView tipo_cantidad = (TextView) convertView.findViewById(R.id.tv_cantidad_tipo);
        tipo_cantidad.setText(dataModel.getUnidad());
        //tipo_cantidad.setText(dataModel.get);


        convertView.setTag(viewHolder);

        return convertView;
    }
}