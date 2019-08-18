package com.itcg.help2meal;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.google.gson.Gson;
import com.jaeger.library.StatusBarUtil;
import com.orhanobut.hawk.Hawk;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RecetaActivity extends AppCompatActivity {
    public Vars vars = new Vars();
    String dataReceived = "";
    ProgressDialog progressdialog;

    TextView tv_name_recipe, tv_time,  tv_porcion, tv_description;
    SimpleDraweeView draweeView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receta);
        StatusBarUtil.setLightMode(this);


        OkHttpClient okHttpClient = new OkHttpClient();
        final ImagePipelineConfig config = OkHttpImagePipelineConfigFactory
                .newBuilder(this, okHttpClient)
                .build();
        Fresco.initialize(this,config);

        tv_name_recipe = (TextView) findViewById(R.id.tv_name_recipe);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_porcion = (TextView) findViewById(R.id.tv_porcion);
        tv_description = (TextView) findViewById(R.id.tv_description);
        draweeView = (SimpleDraweeView) findViewById(R.id.iv_recipe_banner);


        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String id_recipe = intent.getStringExtra("id_recipe");
        /*if(!id_recipe.isEmpty()){
            loadRecipeData(id_recipe);
        }else{
            loadRecipeData("3");
        }*/
        loadRecipeData("1");

        progressdialog = new ProgressDialog(this);
        progressdialog.setCancelable(false);
        progressdialog.setTitle("Cargando receta...");
        progressdialog.show();




    }

    public void loadRecipeData(String id){
        OkHttpClient httpClient = new OkHttpClient();

        String url = vars.URL_SERVER +"api/auth/platillos/"+id;
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

                try {
                    dataReceived = response.body().string();
                    if(response.isSuccessful()){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    Gson gson = new Gson();

                                    Platillo platilloData = gson.fromJson(dataReceived, Platillo.class);
                                    Log.e(vars.TAG,""+platilloData.getUrl_image());
                                    progressdialog.dismiss();

                                    tv_name_recipe.setText(""+platilloData.getNombre());
                                    tv_time.setText(""+platilloData.getPreparacion()+" mins");
                                    tv_porcion.setText(""+platilloData.getPorcion()+" "+platilloData.getPorcion_tipo()+"s");
                                    tv_description.setText(""+platilloData.getDescripcion());

                                    Uri uri = Uri.parse(platilloData.getUrl_image());
                                    draweeView.setImageURI(uri);


                                    Log.e(vars.TAG, dataReceived);
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
    }
}
