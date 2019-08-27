package com.itcg.help2meal;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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

public class IngredientePersonalizadoActivity extends AppCompatActivity {

    Spinner sp_unidades, sp_clasificaciones;
    ArrayList<CategoriaSpinner> dataCategoria;
    ProgressDialog progressdialog;
    EditText et_nombre, et_caducidad, et_img;
    public Vars vars = new Vars();
    String clasificacion_txt;
    String superResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingrediente_personalizado);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorAzul),0);
        final Activity context = IngredientePersonalizadoActivity.this;


        loadClasificacionesDB();

        et_nombre = (EditText) findViewById(R.id.et_nombre_ingredient_p);
        et_caducidad = (EditText) findViewById(R.id.et_caducidad_ingredient_p);
        et_img = (EditText) findViewById(R.id.et_urlimg_ingredient_p);

        sp_unidades = (Spinner) findViewById(R.id.spinnerUnidad);
        String[] cats = {"Centimetro","Litro","Mililitro","Kilogramo","Gramo","Miligramo","Taza","Cucharada","Pieza","Racimo"};
        sp_unidades.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cats));

        sp_clasificaciones = (Spinner) findViewById(R.id.spinnerClasificacion);


    }

    public void actionAddIngredient (View view){
        progressdialog = new ProgressDialog(this);
        progressdialog.setCancelable(true); //change to false
        progressdialog.setTitle("Guardando información...");
        progressdialog.show();

        OkHttpClient httpClient = new OkHttpClient();

        String url = vars.URL_SERVER +"api/auth/ingredientes";
        String token_user = Hawk.get("access_token");

        RequestBody formBody = new FormBody.Builder()
                .add("nombre", et_nombre.getText().toString())
                .add("unidad", sp_unidades.getSelectedItem().toString().toLowerCase())
                .add("caducidad", et_caducidad.getText().toString())
                .add("clasificacion", clasificacion_txt)
                .add("url_imagen", et_img.getText().toString())
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
                progressdialog.dismiss();
                // error
                Alerter.create(IngredientePersonalizadoActivity.this)
                        .setTitle("Ocurrio un error")
                        .setText(""+e.getMessage())
                        .setIcon(R.drawable.alerter_ic_notifications)
                        .setBackgroundColorRes(R.color.colorErrorMaterial)
                        .enableSwipeToDismiss()
                        .show();
            }

            @Override
            public void onResponse(Call call, Response response) {

                progressdialog.dismiss();
                Alerter.create(IngredientePersonalizadoActivity.this)
                        .setTitle("Ingrediente agregado")
                        .setText("Ahora puedes regresar a tu alacena y registrar su cantidad en tú alacena.")
                        .setIcon(R.drawable.icon_check)
                        .setBackgroundColorRes(R.color.colorAqua)
                        .enableSwipeToDismiss()
                        .show();

            }
        });
        //finish();
    }

    public void loadClasificacionesDB(){
        dataCategoria = new ArrayList<>();

        OkHttpClient okHttpClient = new OkHttpClient();

        String urlClasificacion = vars.URL_SERVER +"api/auth/clasificaciones-ingredientes";
        String token_user = Hawk.get("access_token");

        Request requestClasificaciones = new Request.Builder()
                .url(urlClasificacion)
                .addHeader("Content-Type","application/x-www-form-urlencoded")
                .addHeader("X-Requested-With","XMLHttpRequest")
                .addHeader("Authorization" , "Bearer " + token_user)
                .build();

        okHttpClient.newCall(requestClasificaciones).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                Log.e(vars.TAG, e.getMessage());
                // error
            }

            @Override public void onResponse(Call call, Response response) {
                Log.i(vars.TAG, response.message()+" "+response.body().toString() );

                try {
                    superResponse = response.body().string();
                    if(response.isSuccessful()){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    Gson gson = new Gson();

                                    CategoriaSpinner[] mcArray = gson.fromJson(superResponse, CategoriaSpinner[].class);
                                    List<CategoriaSpinner> prepareListClasificacion = Arrays.asList(mcArray);
                                    final ArrayList<CategoriaSpinner> arrayList1 = new ArrayList<>();

                                    for (CategoriaSpinner categoria : prepareListClasificacion) {
                                        arrayList1.add(new CategoriaSpinner(categoria.getId(),categoria.getNombre()));
                                    }

                                    //


                                    CategoriaSpinnerAdapter adapter = new CategoriaSpinnerAdapter(getApplicationContext(), R.layout.list_categoria_spiner, arrayList1
                                    );
                                    sp_clasificaciones.setAdapter(adapter);

                                    sp_clasificaciones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long arg3)
                                        {
                                            clasificacion_txt = arrayList1.get(position).getNombre();
                                            Log.e(vars.TAG, clasificacion_txt);
                                        }

                                        public void onNothingSelected(AdapterView<?> arg0)
                                        {
                                            // TODO Auto-generated method stub
                                        }
                                    });
                                    //

                                    //adapterCategoria = new CategoriaSpinnerAdapter(context, dataCategoria, AlacenaActivity.this );
                                    //gv_categories.setAdapter(adapterCategoria);

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
    }
}
