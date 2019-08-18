package com.itcg.help2meal;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.google.gson.Gson;
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

public class HistorialActivity extends AppCompatActivity {

    GridView gv_results;
    Vars vars = new Vars();
    String responseData;
    ArrayList<ResultRecipe> dataResultRecipes;
    private static ResultRecipesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_historial);

        //gv_recipe_result
        OkHttpClient okHttpClient = new OkHttpClient();
        final ImagePipelineConfig config = OkHttpImagePipelineConfigFactory
                .newBuilder(this, okHttpClient)
                .build();
        Fresco.initialize(this,config);

        loadResults();

    }

    private void loadResults(){
        final Activity context = HistorialActivity.this;

        OkHttpClient httpClient = new OkHttpClient();

        gv_results = (GridView) findViewById(R.id.gv_recipe_history);


        String urlLastRecipes = vars.URL_SERVER +"api/auth/platillos-history";
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
                                    gv_results.setAdapter(adapter);
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

            }
        });
    }

}
