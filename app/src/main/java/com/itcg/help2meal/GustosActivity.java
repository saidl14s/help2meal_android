package com.itcg.help2meal;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jaeger.library.StatusBarUtil;
import com.orhanobut.hawk.Hawk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GustosActivity extends AppCompatActivity {

    GridView gv_enfermedades, gv_gustos;
    Vars vars = new Vars();
    String responseData, responseGusto;
    ArrayList<Enfermedad> dataEnfermedades, dataGusto;
    private static EnfermedadAdapter adapter, adapterGusto;
    final Activity context = GustosActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gustos);

        StatusBarUtil.setLightMode(this);

        loadEnfermedades();
        loadGustos();

    }

    private void loadEnfermedades(){

        OkHttpClient httpClient = new OkHttpClient();

        gv_enfermedades = (GridView) findViewById(R.id.gv_enfermedades);


        String urlLastRecipes = vars.URL_SERVER +"api/auth/get-enfermedades";
        String token_user = Hawk.get("access_token");

        RequestBody formBody = new FormBody.Builder()
                .add("user_token", token_user)
                .build();


        Request request = new Request.Builder()
                .url(urlLastRecipes)
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
                Log.i(vars.TAG, response.message()+" "+response.body().toString() );
                try{
                    responseData = response.body().string();
                    if(response.isSuccessful()){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    Gson gson = new Gson();

                                    Enfermedad[] mcArray = gson.fromJson(responseData, Enfermedad[].class);
                                    List<Enfermedad> prepareListRecipes = Arrays.asList(mcArray);
                                    dataEnfermedades= new ArrayList<>();

                                    for (Enfermedad enfermedad : prepareListRecipes) {
                                        dataEnfermedades.add(
                                                new Enfermedad(
                                                        enfermedad.getId(),
                                                        enfermedad.getNombre()
                                                )
                                        );
                                    }
                                    adapter = new EnfermedadAdapter(context, dataEnfermedades);
                                    gv_enfermedades.setAdapter(adapter);
                                    //progressdialog.dismiss();
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


        dataEnfermedades = new ArrayList<>();
        gv_enfermedades.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    private void loadGustos(){


        OkHttpClient httpClient = new OkHttpClient();

        gv_gustos = (GridView) findViewById(R.id.gv_gustos);


        String urlLastRecipes = vars.URL_SERVER +"api/auth/get-gustos";
        String token_user = Hawk.get("access_token");

        RequestBody formBody = new FormBody.Builder()
                .add("user_token", token_user)
                .build();


        Request request = new Request.Builder()
                .url(urlLastRecipes)
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
                Log.i(vars.TAG, response.message()+" "+response.body().toString() );
                try{
                    responseGusto = response.body().string();
                    if(response.isSuccessful()){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    Gson gson = new Gson();

                                    Enfermedad[] mcArray = gson.fromJson(responseGusto, Enfermedad[].class);
                                    List<Enfermedad> prepareListRecipes = Arrays.asList(mcArray);
                                    dataGusto= new ArrayList<>();

                                    for (Enfermedad enfermedad : prepareListRecipes) {
                                        dataGusto.add(
                                                new Enfermedad(
                                                        enfermedad.getId(),
                                                        enfermedad.getNombre()
                                                )
                                        );
                                    }
                                    adapterGusto = new EnfermedadAdapter(context, dataGusto);
                                    gv_gustos.setAdapter(adapterGusto);
                                    //progressdialog.dismiss();
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


        dataGusto = new ArrayList<>();
        gv_gustos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e(vars.TAG,""+position);
                Toast.makeText(getApplicationContext(), position+" "+adapterGusto.getItem(position).getNombre()+" " +adapterGusto.getItem(position).getId(), Toast.LENGTH_LONG).show();
            }
        });
    }
}