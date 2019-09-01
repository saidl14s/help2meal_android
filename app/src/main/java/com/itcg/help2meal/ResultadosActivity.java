package com.itcg.help2meal;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.androidstudy.networkmanager.Monitor;
import com.androidstudy.networkmanager.Tovuti;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.google.gson.Gson;
import com.jaeger.library.StatusBarUtil;
import com.orhanobut.hawk.Hawk;
import com.tapadoo.alerter.Alerter;

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

public class ResultadosActivity extends AppCompatActivity {

    GridView gv_results;
    Vars vars = new Vars();
    String responseData;
    ArrayList<ResultRecipe> dataResultRecipes;
    private static ResultRecipesAdapter adapter;
    ProgressDialog progressdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_resultados);
        StatusBarUtil.setLightMode(this);

        //gv_recipe_result
        Tovuti.from(getApplicationContext()).monitor(new Monitor.ConnectivityListener(){
            @Override
            public void onConnectivityChanged(int connectionType, boolean isConnected, boolean isFast){
                // TODO: Handle the connection...
                if(isConnected){
                    Hawk.init(getApplicationContext()).build();
                    progressdialog = new ProgressDialog(ResultadosActivity.this);
                    progressdialog.setCancelable(false);
                    progressdialog.setTitle("Armando tus recetas...");
                    progressdialog.show();

                    OkHttpClient okHttpClient = new OkHttpClient();
                    final ImagePipelineConfig config = OkHttpImagePipelineConfigFactory
                            .newBuilder(getApplicationContext(), okHttpClient)
                            .build();
                    Fresco.initialize(getApplicationContext(),config);

                    String tipo_recomendacion = getIntent().getStringExtra("type_recipe");

                    loadResults(tipo_recomendacion);
                }else{
                    Alerter.create(ResultadosActivity.this)
                            .setTitle("Vaya!")
                            .setText("Tenemos un problema con tu conexión a Internet.")
                            .setIcon(R.drawable.alerter_ic_notifications)
                            .setBackgroundColorRes(R.color.colorErrorMaterial)
                            .enableSwipeToDismiss()
                            .show();
                }
            }
        });


    }

    public void setGridViewHeightBasedOnChildren(GridView gridView, ResultRecipesAdapter adapterView, int columnas) {

        try {

            int alturaTotal = 0;
            int items = adapterView.getCount();
            int filas = 0;

            View listItem = adapterView.getView(0, null, gridView);
            listItem.measure(0, 0);
            alturaTotal = listItem.getMeasuredHeight();

            float x = 1;

            if (items > columnas) {
                x = items / columnas;
                filas = (int) (x + 1);
                alturaTotal *= filas;
            }

            ViewGroup.LayoutParams params = gridView.getLayoutParams();
            params.height = alturaTotal+30;
            gridView.setLayoutParams(params);

        } catch (IndexOutOfBoundsException e){}
    }

    private void loadResults(String tipo_recomendacion){
        final Activity context = ResultadosActivity.this;

        OkHttpClient httpClient = new OkHttpClient();

        gv_results = (GridView) findViewById(R.id.gv_recipe_result);


        String urlLastRecipes = vars.URL_SERVER +"api/auth/platillos-ai";
        String token_user = Hawk.get("access_token");

        RequestBody formBody = new FormBody.Builder()
                .add("user_token", token_user)
                .add("tipo_recomendacion", tipo_recomendacion)
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

                                    ResultRecipe[] mcArray = gson.fromJson(responseData, ResultRecipe[].class);
                                    List<ResultRecipe> prepareListRecipes = Arrays.asList(mcArray);
                                    dataResultRecipes= new ArrayList<>();

                                    for (ResultRecipe recipe : prepareListRecipes) {
                                        dataResultRecipes.add(
                                                new ResultRecipe(
                                                        recipe.getId(),
                                                        recipe.getUrl_image(),
                                                        recipe.getNombre(),
                                                        recipe.getDescripcion()
                                                )
                                        );
                                    }
                                    adapter = new ResultRecipesAdapter(context, dataResultRecipes);
                                    setGridViewHeightBasedOnChildren(gv_results,adapter,1);
                                    gv_results.setAdapter(adapter);
                                    progressdialog.dismiss();
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


        dataResultRecipes = new ArrayList<>();
        gv_results.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), RecetaActivity.class);
                intent.putExtra("id_recipe", ""+adapter.getItem(position).getId());
                startActivity(intent);
            }
        });
    }

}
