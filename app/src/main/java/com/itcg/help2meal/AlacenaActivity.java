package com.itcg.help2meal;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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

public class AlacenaActivity extends AppCompatActivity implements CategoriaAdapter.OnClickInAdapter {

    Button btn_save_ingredientes;
    public Vars vars = new Vars();
    GridView lv_ingredientes, gv_categories;
    ArrayList<Ingrediente> dataIngredientes;
    ArrayList<Categoria> dataCategoria;
    private static IngredienteAdapter adapter;
    private static CategoriaAdapter adapterCategoria;
    String i = "", x = "";
    ProgressDialog progressdialog;
    EditText et_input;

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(vars.TAG, "resume");
        loadIngredientsDB();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alacena);
        StatusBarUtil.setLightMode(this);


        final Activity context = AlacenaActivity.this;
        progressdialog = new ProgressDialog(context);
        progressdialog.setCancelable(false);
        progressdialog.setTitle("Recuperando lista de ingredientes...");
        progressdialog.show();

        et_input = (EditText) findViewById(R.id.input_dialog);


        OkHttpClient httpClientClasificacion = new OkHttpClient();
        lv_ingredientes = (GridView) findViewById(R.id.gv_ingredientes);
        gv_categories = (GridView) findViewById(R.id.gv_categories);


        dataCategoria = new ArrayList<>();

        OkHttpClient okHttpClient = new OkHttpClient();
        final ImagePipelineConfig config = OkHttpImagePipelineConfigFactory
                .newBuilder(context, okHttpClient)
                .build();
        Fresco.initialize(context,config );

        lv_ingredientes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                /*Alerter.create(AlacenaActivity.this)
                        //.setTitle(""+dataIngredientes.get(position).getNombre())
                        .setText("")
                        .setIcon(R.drawable.icon_information)
                        .setBackgroundColorRes(R.color.colorAzul)
                        .enableSwipeToDismiss()
                        .show();*/
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(AlacenaActivity.this);
                alertDialog.setTitle(""+dataIngredientes.get(position).getNombre() );
                alertDialog.setMessage("Establecer cantidad manualmente");

                final EditText input = new EditText(AlacenaActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                lp.setMargins(70,0,70,0);

                input.setLayoutParams(lp);
                input.setText(""+dataIngredientes.get(position).getCantidad());
                input.setInputType( InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

                alertDialog.setView(input);
                alertDialog.setPositiveButton("Establecer",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Log.e(vars.TAG, ""+dataIngredientes.get(position).getNombre() + " "+input.getText().toString());
                                dataIngredientes.get(position).setCantidad(Integer.parseInt(input.getText().toString()));
                                uploadInventario();
                                loadIngredientsDB();
                            }
                        });

                alertDialog.setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alertDialog.show();
                //return true;
            }
        });
        lv_ingredientes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,final int position, long id) {
                // TODO Auto-generated method stub
                /*Alerter.create(AlacenaActivity.this)
                        .setTitle(""+dataIngredientes.get(position).getNombre())
                        .setText(" longClick")
                        .setIcon(R.drawable.icon_information)
                        .setBackgroundColorRes(R.color.colorWarningMaterial)
                        .enableSwipeToDismiss()
                        .show();
                        */

                /*MaterialDialog.Builder dialogBuilder = new MaterialDialog.Builder(AlacenaActivity.this);
                dialogBuilder.setTitle(dataIngredientes.get(position).getNombre());
                dialogBuilder.setMessage("Establecer cantidad manualmente");
                //dialogBuilder.setView(edittext);
                dialogBuilder.setView(R.layout.input_dialog);



                dialogBuilder.setPositiveButton("Establecer",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.e(vars.TAG, ""+dataIngredientes.get(position).getNombre() + et_input.getText().toString());

                    }
                });
                dialogBuilder.setNegativeButton("Cancelar",null);
                MaterialDialog dialog = dialogBuilder.create();
                dialog.show();*/

                /*AlertDialog.Builder alertDialog = new AlertDialog.Builder(AlacenaActivity.this);
                alertDialog.setTitle(""+dataIngredientes.get(position).getNombre() );
                alertDialog.setMessage("Establecer cantidad manualmente");

                final EditText input = new EditText(AlacenaActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                lp.setMargins(70,0,70,0);

                input.setLayoutParams(lp);
                input.setText(""+dataIngredientes.get(position).getCantidad());
                input.setInputType( InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

                alertDialog.setView(input);
                alertDialog.setPositiveButton("Establecer",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Log.e(vars.TAG, ""+dataIngredientes.get(position).getNombre() + " "+input.getText().toString());
                                dataIngredientes.get(position).setCantidad(Integer.parseInt(input.getText().toString()));
                                uploadInventario();
                                loadIngredientsDB();
                            }
                        });

                alertDialog.setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alertDialog.show();
                return true;*/
                return true;
            }
        });


        loadIngredientsDB();



        String urlClasificacion = vars.URL_SERVER +"api/auth/clasificaciones-ingredientes";
        String token_user = Hawk.get("access_token");

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
                        runOnUiThread(new Runnable() {
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
                                    adapterCategoria = new CategoriaAdapter(context, dataCategoria, AlacenaActivity.this );
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


        btn_save_ingredientes = (Button) findViewById(R.id.btn_save_ingredientes);
        btn_save_ingredientes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String url = vars.URL_SERVER +"api/auth/user-ingredientes-save";
                String token_user = Hawk.get("access_token");

                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("Content-Type","application/x-www-form-urlencoded")
                        .addHeader("X-Requested-With","XMLHttpRequest")
                        .addHeader("Authorization" , "Bearer " + token_user)
                        .build();
                uploadInventario();
                Alerter.create(AlacenaActivity.this)
                        .setTitle("Almacenado correctamente")
                        .setText("")
                        .setIcon(R.drawable.icon_check)
                        .setBackgroundColorRes(R.color.colorAqua)
                        .enableSwipeToDismiss()
                        .show();
            }
        });
    }

    public void loadIngredientsDB(){
        String url = vars.URL_SERVER +"api/auth/ingredientes-get";
        OkHttpClient httpClient = new OkHttpClient();
        String token_user = Hawk.get("access_token");
        dataIngredientes = new ArrayList<>();

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
                                                        ingrediente.getUrl_imagen(),
                                                        ingrediente.getCantidad()
                                                )
                                        );
                                    }
                                    adapter = new IngredienteAdapter(AlacenaActivity.this, dataIngredientes);
                                    lv_ingredientes.setAdapter(adapter);
                                    progressdialog.dismiss();

                                    Alerter.create(AlacenaActivity.this)
                                            .setTitle("")
                                            .setText("Puedes cambiar las cantidades de forma manual haciendo click en el ingrediente y colocando el número deseado.")
                                            .setIcon(R.drawable.icon_information)
                                            .setBackgroundColorRes(R.color.colorWarningMaterial)
                                            .enableSwipeToDismiss()
                                            .show();

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

    public void uploadInventario(){

        OkHttpClient httpClient = new OkHttpClient();

        String url = vars.URL_SERVER +"api/auth/user-ingredientes-save";
        String token_user = Hawk.get("access_token");


        for(int x = 0; x < dataIngredientes.size(); x++) {
            Log.e(vars.TAG,dataIngredientes.get(x).getNombre() +" "+dataIngredientes.get(x).getId() + " "+dataIngredientes.get(x).getCantidad());
            if(dataIngredientes.get(x).getCantidad() > 0){
                RequestBody formBody = new FormBody.Builder()
                        .add("ingrediente_id", ""+dataIngredientes.get(x).getId())
                        .add("cantidad", ""+dataIngredientes.get(x).getCantidad())
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

    }


    public void showNuevoIngredienteActivity(View view){
        Intent intent = new Intent(AlacenaActivity.this, IngredientePersonalizadoActivity.class);
        startActivity(intent);
    }



    @Override
    public void onClickInAdapter(final String content) {
        //Log.e(vars.TAG,"in activity: "+content);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                uploadInventario(); // se sube lo que actualmente haya registrado
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
                                        ingrediente.getUrl_imagen(),
                                        ingrediente.getCantidad()
                                )
                        );
                    }
                    adapter = new IngredienteAdapter(AlacenaActivity.this, dataIngredientes);
                    lv_ingredientes.setAdapter(adapter);
                    //progressdialog.dismiss();
                }catch (Exception e){
                    Log.e(vars.TAG, e.getMessage());
                }

            }
        });
    }
}
