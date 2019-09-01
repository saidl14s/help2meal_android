package com.itcg.help2meal;

import android.app.Activity;
import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.Toast;


import com.google.gson.Gson;
import com.orhanobut.hawk.Hawk;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CategoriaAdapter extends ArrayAdapter<Categoria>{


    public Vars vars = new Vars();
    private final Context context;
    private final ArrayList<Categoria> categorias;
    String url;

    OnClickInAdapter onClickInAdapter;

    // View lookup cache
    private static class ViewHolder {
        Button txtName;
    }

    public CategoriaAdapter(Activity context, ArrayList<Categoria> categorias, OnClickInAdapter listener) {
        super(context, R.layout.list_item_categoria, categorias);
        this.context = context;
        this.categorias = categorias;
        this.onClickInAdapter = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        viewHolder = new ViewHolder();

        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(R.layout.list_item_categoria, parent, false);

        Button btn = (Button) convertView.findViewById(R.id.btn_load_category_);
        btn.setText(getItem(position).getNombre());
        btn.setId(getItem(position).getId());
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // activar el 'cargando..'

                url = vars.URL_SERVER +"api/auth/vget-ingredients-classification";
                //Log.e("HELP", url);
                OkHttpClient httpClient = new OkHttpClient();
                String token_user = Hawk.get("access_token");

                RequestBody formBody = new FormBody.Builder()
                        .add("clasificacion_id", ""+v.getId())
                        .build();

                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("Content-Type","application/x-www-form-urlencoded")
                        .addHeader("X-Requested-With","XMLHttpRequest")
                        .addHeader("Authorization" , "Bearer " + token_user)
                        .post(formBody)
                        .build();

                httpClient.newCall(request).enqueue(new Callback() {
                    @Override public void onFailure(Call call, IOException e) {
                        Log.e(vars.TAG, e.getMessage());
                        // error
                    }

                    @Override public void onResponse(Call call, Response response) {
                        try {
                            //Log.e("HELP2MEAL",""+dataModel.getId());
                            onClickInAdapter.onClickInAdapter(response.body().string());
                        }catch (Exception e){

                        }

                    }
                });


            }
        });

        convertView.setTag(viewHolder);

        return convertView;
    }

    public interface OnClickInAdapter {
        public void onClickInAdapter(String content);
    }


}
