package com.itcg.help2meal;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.google.gson.Gson;
import com.orhanobut.hawk.Hawk;
import com.tapadoo.alerter.Alerter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Belal on 1/23/2018.
 */

public class CupboardFragment extends Fragment  implements CategoriaAdapter.OnClickInAdapter {

    Button btn_save_ingredientes;
    public Vars vars = new Vars();
    GridView gv_ingredientes, gv_categories;
    ArrayList<Ingrediente> dataIngredientes;
    ArrayList<Categoria> dataCategoria;
    private static IngredienteAdapter adapter;
    private static CategoriaAdapter adapterCategoria;
    String i = "", x = "";
    ProgressDialog progressdialog;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cupboard, null);

        progressdialog = new ProgressDialog(super.getContext());
        progressdialog.setCancelable(false);
        progressdialog.setTitle("Actualizando lista de ingredientes...");
        progressdialog.show();

        OkHttpClient httpClient = new OkHttpClient();
        OkHttpClient httpClientClasificacion = new OkHttpClient();
        gv_ingredientes = (GridView) view.findViewById(R.id.gv_ingredientes);
        gv_categories = (GridView)  view.findViewById(R.id.gv_categories);

        dataIngredientes = new ArrayList<>();
        dataCategoria = new ArrayList<>();

        OkHttpClient okHttpClient = new OkHttpClient();
        final ImagePipelineConfig config = OkHttpImagePipelineConfigFactory
                .newBuilder(super.getActivity(), okHttpClient)
                .build();
        Fresco.initialize(super.getActivity(),config );

        gv_ingredientes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        String url = vars.URL_SERVER +"api/auth/ingredientes";
        String urlClasificacion = vars.URL_SERVER +"api/auth/clasificaciones";
        String token_user = Hawk.get("access_token");

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type","application/x-www-form-urlencoded")
                .addHeader("X-Requested-With","XMLHttpRequest")
                .addHeader("Authorization" , "Bearer " + token_user)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                Log.e(vars.TAG, e.getMessage());
                // error
            }

            @Override public void onResponse(Call call, Response response) {
                Log.i(vars.TAG, response.message()+" "+response.body().toString() );

                try {
                    i = response.body().string();
                    if(response.isSuccessful()){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    Gson gson = new Gson();

                                    Ingrediente[] mcArray = gson.fromJson(i, Ingrediente[].class);
                                    List<Ingrediente> prepareListIngredients = Arrays.asList(mcArray);
                                    dataIngredientes= new ArrayList<>();

                                    for (Ingrediente ingrediente : prepareListIngredients) {
                                        Log.i(vars.TAG, ingrediente.getNombre() + " "+ ingrediente.getUnidad());
                                        dataIngredientes.add(
                                                new Ingrediente(
                                                        ingrediente.getId(),
                                                        ingrediente.getNombre(),
                                                        ingrediente.getUnidad(),
                                                        ingrediente.getCaducidad(),
                                                        ingrediente.getClasificacion_id(),
                                                        ingrediente.getUrl_imagen()
                                                )
                                        );
                                    }
                                    adapter = new IngredienteAdapter(CupboardFragment.super.getActivity(), dataIngredientes);
                                    gv_ingredientes.setAdapter(adapter);
                                    progressdialog.dismiss();
                                }catch (Exception e){
                                    Log.e(vars.TAG, e.getMessage());
                                }

                            }
                        });

                    } else {
                        //Log.e(vars.TAG, response.message()+" "+response.body().toString() );
                    }
                }catch (Exception e){

                }

            }
        });


        Request requestClasificaciones = new Request.Builder()
                .url(urlClasificacion)
                .addHeader("Content-Type","application/x-www-form-urlencoded")
                .addHeader("X-Requested-With","XMLHttpRequest")
                .addHeader("Authorization" , "Bearer " + token_user)
                .build();

        httpClientClasificacion.newCall(requestClasificaciones).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                Log.e(vars.TAG, e.getMessage());
                // error
            }

            @Override public void onResponse(Call call, Response response) {
                Log.i(vars.TAG, response.message()+" "+response.body().toString() );

                try {
                    x = response.body().string();
                    if(response.isSuccessful()){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    Gson gson = new Gson();

                                    Categoria[] mcArray = gson.fromJson(x, Categoria[].class);
                                    List<Categoria> prepareListClasificacion = Arrays.asList(mcArray);
                                    dataCategoria= new ArrayList<>();

                                    for (Categoria categoria : prepareListClasificacion) {
                                        dataCategoria.add(
                                                new Categoria(
                                                        categoria.getId(),
                                                        categoria.getNombre()
                                                )
                                        );
                                    }
                                    adapterCategoria = new CategoriaAdapter(CupboardFragment.super.getActivity(), dataCategoria, CupboardFragment.this);
                                    gv_categories.setAdapter(adapterCategoria);

                                }catch (Exception e){

                                }

                            }
                        });

                    } else {
                        //Log.e(vars.TAG, response.message()+" "+response.body().toString() );
                    }
                }catch (Exception e){

                }

            }
        });


        btn_save_ingredientes = (Button) view.findViewById(R.id.btn_save_ingredientes);
        btn_save_ingredientes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String url = vars.URL_SERVER +"api/auth/ingredientes";
                String token_user = Hawk.get("access_token");

                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("Content-Type","application/x-www-form-urlencoded")
                        .addHeader("X-Requested-With","XMLHttpRequest")
                        .addHeader("Authorization" , "Bearer " + token_user)
                        .build();
                Intent intent = new Intent(CupboardFragment.super.getActivity(), HomeActivity.class);
                startActivity(intent);
            }
        });

        return view;

    }


    @Override
    public void onClickInAdapter(String content) {

        //Log.e(vars.TAG,"in activity: "+content);
        /*getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try{
                    Gson gson = new Gson();

                    Ingrediente[] mcArray = gson.fromJson(content, Ingrediente[].class);
                    List<Ingrediente> prepareListIngredients = Arrays.asList(mcArray);
                    dataIngredientes= new ArrayList<>();

                    for (Ingrediente ingrediente : prepareListIngredients) {
                        Log.i(vars.TAG, ingrediente.getNombre() + " "+ ingrediente.getUnidad());
                        dataIngredientes.add(
                                new Ingrediente(
                                        ingrediente.getId(),
                                        ingrediente.getNombre(),
                                        ingrediente.getUnidad(),
                                        ingrediente.getCaducidad(),
                                        ingrediente.getClasificacion_id(),
                                        ingrediente.getUrl_imagen()
                                )
                        );
                    }
                    adapter = new IngredienteAdapter(CupboardFragment.super.getActivity(), dataIngredientes);
                    lv_ingredientes.setAdapter(adapter);
                    //progressdialog.dismiss();
                }catch (Exception e){
                    Log.e(vars.TAG, e.getMessage());
                }

            }
        });*/

    }
}
