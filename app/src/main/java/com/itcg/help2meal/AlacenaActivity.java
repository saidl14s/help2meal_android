package com.itcg.help2meal;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.google.gson.Gson;
import com.orhanobut.hawk.Hawk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AlacenaActivity extends AppCompatActivity {

    public Vars vars = new Vars();
    GridView lv_ingredientes;
    ArrayList<Ingrediente> dataIngredientes;
    private static IngredienteAdapter adapter;
    String i = "";
    ProgressDialog progressdialog;
    int desertNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alacena);


        final Activity context = AlacenaActivity.this;
        progressdialog = new ProgressDialog(context);
        progressdialog.show();

        OkHttpClient httpClient = new OkHttpClient();
        lv_ingredientes = (GridView) findViewById(R.id.lv_ingredientes);
        dataIngredientes = new ArrayList<>();
        OkHttpClient okHttpClient = new OkHttpClient();
        ImagePipelineConfig config = OkHttpImagePipelineConfigFactory
                .newBuilder(context, okHttpClient)
                .build();
        Fresco.initialize(context,config );


        String url = vars.URL_SERVER +"api/auth/ingredientes";
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
                        runOnUiThread(new Runnable() {
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
                                    adapter = new IngredienteAdapter(context, dataIngredientes);
                                    lv_ingredientes.setAdapter(adapter);
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

    }

    public void decrementoIngrediente(View view) {

        LinearLayout parentRow = (LinearLayout) view.getParent();

        EditText quantityView = (EditText) parentRow.findViewById(R.id.et_cantidad);
        String quantityString = quantityView.getText().toString();
        desertNumber = Integer.parseInt(quantityString);
        desertNumber -= 1;

        if (desertNumber < 0) {
            desertNumber = 0;
            Toast.makeText(AlacenaActivity.this, "Can not be less than 0",
                    Toast.LENGTH_SHORT).show();}
        quantityView.setText(String.valueOf(desertNumber));
    }

    public void incrementoIngrediente(View view) {

        LinearLayout parentRow = (LinearLayout) view.getParent();

        EditText quantityView = (EditText) parentRow.findViewById(R.id.et_cantidad);
        String quantityString = quantityView.getText().toString();
        desertNumber = Integer.parseInt(quantityString);
        desertNumber += 1;
        quantityView.setText(String.valueOf(desertNumber));
    }

}
