package com.itcg.help2meal;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
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

public class RecetaActivity extends AppCompatActivity {
    public Vars vars = new Vars();
    String dataReceived = "", dataIngredients;
    ProgressDialog progressdialog;

    TextView tv_name_recipe, tv_time,  tv_porcion, tv_description, tv_instructions,tv_instructions_title;
    SimpleDraweeView draweeView;
    GridView gv_ingredients_recipe_suggest;
    IngredienteSuggestAdapter adapterIngredient;
    ArrayList<Ingrediente> dataIngredientes;
    Button btn_cocinar_now;
    String id_recipe;


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
        tv_instructions = (TextView) findViewById(R.id.tv_instructions);
        tv_instructions_title = (TextView) findViewById(R.id.tv_instructions_title);

        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_porcion = (TextView) findViewById(R.id.tv_porcion);
        tv_description = (TextView) findViewById(R.id.tv_description);
        draweeView = (SimpleDraweeView) findViewById(R.id.iv_recipe_banner);
        gv_ingredients_recipe_suggest = findViewById(R.id.gv_ingredients_recipe_suggest);
        btn_cocinar_now = (Button) findViewById(R.id.btn_cocinar_now);


        // Get the Intent that started this activity and extract the string
        //Intent intent = getIntent();
        ///String id_recipe = intent.getStringExtra("id_recipe");
        id_recipe = getIntent().getStringExtra("id_recipe");
        if(id_recipe!=null){
            loadRecipeData(""+id_recipe);
            loadIngredients(""+id_recipe);

            progressdialog = new ProgressDialog(this);
            progressdialog.setCancelable(false);
            progressdialog.setTitle("Cargando receta...");
            progressdialog.show();
        }else{
            finish();
        }





    }

    public void cocinarNow(View view){
        tv_instructions.setVisibility(View.VISIBLE);
        btn_cocinar_now.setVisibility(View.GONE);
        tv_instructions_title.setVisibility(View.VISIBLE);
        String url = vars.URL_SERVER +"api/auth/ingredientes-user-update";
        OkHttpClient httpClient = new OkHttpClient();
        String token_user = Hawk.get("access_token");

        for(int x = 0; x < dataIngredientes.size(); x++) {
            Log.e("Gelp2mEAL",dataIngredientes.get(x).getNombre() +" "+dataIngredientes.get(x).getId() + " "+dataIngredientes.get(x).getCantidad());

            RequestBody formBody = new FormBody.Builder()
                    .add("ingrediente_id", ""+dataIngredientes.get(x).getId())
                    .add("cantidad", ""+dataIngredientes.get(x).getCantidad())
                    .add("platillo_id", id_recipe)
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Content-Type","application/x-www-form-urlencoded")
                    .addHeader("X-Requested-With","XMLHttpRequest")
                    .addHeader("Authorization" , "Bearer " + token_user)
                    .post(formBody)
                    .build();
            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(vars.TAG, e.getMessage());

                    // error
                }

                @Override
                public void onResponse(Call call, Response response) {


                }
            });
        }
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
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                        tv_instructions.setText(Html.fromHtml(platilloData.getInstrucciones(),Html.FROM_HTML_MODE_LEGACY));
                                    } else {
                                        tv_instructions.setText(Html.fromHtml(platilloData.getInstrucciones()));
                                    }

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

    public void loadIngredients(String id){
        OkHttpClient httpClient = new OkHttpClient();

        String url = vars.URL_SERVER +"api/auth/get-ingredients-recipe/"+id;
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
                    dataIngredients = response.body().string();
                    if(response.isSuccessful()){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    Gson gson = new Gson();

                                    Ingrediente[] mcArray = gson.fromJson(dataIngredients, Ingrediente[].class);
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
                                                        ingrediente.getUrl_imagen(),
                                                        ingrediente.getCantidad()
                                                )
                                        );
                                    }
                                    adapterIngredient = new IngredienteSuggestAdapter(RecetaActivity.this, dataIngredientes);
                                    gv_ingredients_recipe_suggest.setAdapter(adapterIngredient);

                                    Log.e(vars.TAG, dataIngredients);
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
