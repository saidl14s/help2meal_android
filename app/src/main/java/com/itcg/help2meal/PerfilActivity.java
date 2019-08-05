package com.itcg.help2meal;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

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

public class PerfilActivity extends AppCompatActivity {

    public Vars vars = new Vars();
    String dataReceived = "";
    ProgressDialog progressdialog;

    EditText et_name, et_mail, et_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        StatusBarUtil.setColor(this,  getColor(R.color.colorVerde),0);

        progressdialog = new ProgressDialog(this);
        progressdialog.setCancelable(false);
        progressdialog.setTitle("Recuperando toda tú información...");
        progressdialog.show();

        et_name = (EditText) findViewById(R.id.et_name_user);
        et_mail = (EditText) findViewById(R.id.et_email_user);
        et_password = (EditText) findViewById(R.id.et_password_user);

        loadUserData();
    }

    public void updateUserData(View view){
        OkHttpClient httpClient = new OkHttpClient();

        String url = vars.URL_SERVER +"api/auth/user";
        String token_user = Hawk.get("access_token");

        RequestBody formBody = new FormBody.Builder()
                .add("name", et_name.getText().toString())
                .add("email", et_mail.getText().toString())
                .add("password", et_password.getText().toString())
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type","application/x-www-form-urlencoded")
                .addHeader("X-Requested-With","XMLHttpRequest")
                .addHeader("Authorization" , "Bearer " + token_user)
                .put(formBody)
                .build();
        progressdialog = new ProgressDialog(this);
        progressdialog.setCancelable(true); //change to false
        progressdialog.setTitle("Guardando información...");
        progressdialog.show();
    }

    public void checkLicence(View view){
        progressdialog = new ProgressDialog(this);
        progressdialog.setCancelable(true); //change to false
        progressdialog.setTitle("Validando licencia, espere un momento por favor...");
        progressdialog.show();
    }

    public void loadUserData(){
        OkHttpClient httpClient = new OkHttpClient();

        String url = vars.URL_SERVER +"api/auth/user";
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

                                    Usuario userData = gson.fromJson(dataReceived, Usuario.class);
                                    progressdialog.dismiss();
                                    et_name.setText(userData.getName());
                                    et_mail.setText(userData.getEmail());
                                    et_password.setText(userData.getPassword());

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
