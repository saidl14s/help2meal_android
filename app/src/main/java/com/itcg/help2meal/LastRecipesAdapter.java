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


public class LastRecipesAdapter extends ArrayAdapter<LastRecipe> implements View.OnClickListener {

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
    }

    private final Context context;
    private final ArrayList<LastRecipe> recipe;

    public LastRecipesAdapter(Activity context, ArrayList<LastRecipe> recipe) {
        super(context, R.layout.list_last_recipes, recipe);
        this.context = context;
        this.recipe = recipe;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        LastRecipe dataModel = getItem(position);


        viewHolder = new ViewHolder();
        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(R.layout.list_last_recipes, parent, false);


        SimpleDraweeView draweeView = (SimpleDraweeView) convertView.findViewById(R.id.iv_banner_recipe_last);
        draweeView.setImageURI(dataModel.getUrl_image());

        convertView.setTag(viewHolder);

        return convertView;
    }
}