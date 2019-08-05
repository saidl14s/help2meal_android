package com.itcg.help2meal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jaeger.library.StatusBarUtil;
import com.orhanobut.hawk.Hawk;
import com.tapadoo.alerter.Alerter;

import java.io.IOException;
import java.util.Properties;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    LinearLayout lyt_ctrol_login,
            lyt_ctrol_signup;

    TextView tv_main,
            tv_title,
            tv_subtitle;

    EditText et_email_login,
            et_password_login,
            et_name_signup,
            et_email_signup,
            et_password_signup;

    ImageView iv_logo;

    public Vars vars = new Vars();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StatusBarUtil.setTransparent(this);
        StatusBarUtil.setDarkMode(this);

        initConfiguration();
        activateButtons(); // accionar botones


    }

    public void initConfiguration(){
        Hawk.init(this).build();

        if(vars.DEBUG){
            Hawk.put("access_token", vars.TOKEN_FAKE);
            Intent intent = new Intent(MainActivity.this, HomeTabActivity.class);
            startActivity(intent);
            finish();
        }else{
            if(Hawk.contains("access_token")){
                Intent intent = new Intent(MainActivity.this, HomeTabActivity.class);
                startActivity(intent);
                String token = Hawk.get("access_token");
                finish();
            }
        }


    }

    /*public void showNotAcceptRegistration(View view){
        Alerter.create(MainActivity.this)
                .setTitle("Lo sentimos...")
                .setText("Por el momento no aceptamos nuevos usuarios, intentalo después.")
                .enableProgress(false)
                .setBackgroundColorRes(R.color.colorErrorMaterial)
                .show();
    }*/

    public void activateButtons(){
        lyt_ctrol_login = (LinearLayout) findViewById(R.id.lyt_ctrol_login);
        lyt_ctrol_signup = (LinearLayout) findViewById(R.id.lyt_ctrol_signup);

        // Login active, register dissabled
        lyt_ctrol_login.setVisibility(View.VISIBLE);
        lyt_ctrol_signup.setVisibility(View.GONE);

        tv_main = (TextView) findViewById(R.id.tv_main);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_subtitle = (TextView) findViewById(R.id.tv_subtitle);

        et_email_login = (EditText) findViewById(R.id.et_email_login);
        et_password_login = (EditText) findViewById(R.id.et_password_login);
        et_name_signup = (EditText) findViewById(R.id.et_name_signup);
        et_email_signup = (EditText) findViewById(R.id.et_email_signup);
        et_password_signup = (EditText) findViewById(R.id.et_password_signup);

        iv_logo = (ImageView) findViewById(R.id.iv_logo);


        final Button btn_activate_login = (Button) findViewById(R.id.btn_activate_login);
        btn_activate_login.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                lyt_ctrol_login.setVisibility(View.VISIBLE);
                lyt_ctrol_signup.setVisibility(View.GONE);
                tv_main.setText("Iniciar sesión");
                tv_title.setPadding(0,0,0,0);
                tv_title.setText("¡Bienvenido!");
                tv_subtitle.setText("Nos encanta verte de nuevo");
                tv_title.setTextColor( getResources().getColor(R.color.colorVerde));
                tv_subtitle.setTextColor( getResources().getColor(R.color.colorVerde));
                iv_logo.setVisibility(View.VISIBLE);
            }
        });

        Button btn_activate_signup = (Button) findViewById(R.id.btn_activate_signup);
        btn_activate_signup.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                lyt_ctrol_login.setVisibility(View.GONE);
                lyt_ctrol_signup.setVisibility(View.VISIBLE);
                tv_main.setText("Registro");
                tv_title.setPadding(0,50,0,0);
                tv_title.setText("Empecemos...");
                tv_subtitle.setText("Cuida tu salud mientras te alimentas rico");
                tv_title.setTextColor( getResources().getColor(R.color.colorAzul));
                tv_subtitle.setTextColor( getResources().getColor(R.color.colorAzul));
                iv_logo.setVisibility(View.GONE);

            }
        });

        ImageButton btn_login = (ImageButton) findViewById(R.id.btn_login);
        btn_login.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Alerter.create(MainActivity.this)
                        .setTitle("Conectando...")
                        .enableProgress(true)
                        .setBackgroundColorRes(R.color.colorVerde)
                        .show();
                // TODO Auto-generated method stub
                OkHttpClient httpClient = new OkHttpClient();
                String url = vars.URL_SERVER +"api/auth/login";

                RequestBody formBody = new FormBody.Builder()
                        .add("email", et_email_login.getText().toString())
                        .add("password", et_password_login.getText().toString())
                        .build();

                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("Content-Type","application/x-www-form-urlencoded")
                        .addHeader("X-Requested-With","XMLHttpRequest")
                        .post(formBody)
                        .build();


                httpClient.newCall(request).enqueue(new Callback() {
                    @Override public void onFailure(Call call, IOException e) {
                        Log.e(vars.TAG, e.getMessage());
                        Alerter.create(MainActivity.this)
                                .setTitle("Problema con el servidor.")
                                .setText(e.getMessage())
                                .setBackgroundColorRes(R.color.colorError)
                                .show();

                    }

                    @Override public void onResponse(Call call, Response response) {

                        if(response.isSuccessful() && response.message() != "Unauthorized"){
                            try{
                                String i = response.body().string();
                                Intent intent = new Intent(MainActivity.this, HomeTabActivity.class);
                                startActivity(intent);

                                Gson gson = new Gson();
                                Properties properties = gson.fromJson(i,Properties.class);
                                //Log.i(TAG, "TOKEN: "+properties.getProperty("access_token"));
                                Hawk.put("access_token", properties.getProperty("access_token"));

                                finish(); // delete this activity from stack activities
                            }catch (Exception e){
                                Log.e(vars.TAG, e.getMessage());
                            }
                        } else {
                            Alerter.create(MainActivity.this)
                                    .setTitle("Error")
                                    .setText("Ocurrio un problema con tu usuario y/o contraseña.")
                                    .setBackgroundColorRes(R.color.colorError)
                                    .show();
                        }

                    }
                });
            }
        });


        ImageButton btn_signup = (ImageButton) findViewById(R.id.btn_signup);
        btn_signup.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Alerter.create(MainActivity.this)
                        .setTitle("Conectando...")
                        .enableProgress(true)
                        .setBackgroundColorRes(R.color.colorVerde)
                        .show();
                // TODO Auto-generated method stub
                OkHttpClient httpClient = new OkHttpClient();
                String url = vars.URL_SERVER +"api/auth/signup";

                RequestBody formBody = new FormBody.Builder()
                        .add("name", et_name_signup.getText().toString())
                        .add("email", et_email_signup.getText().toString())
                        .add("password", et_password_signup.getText().toString())
                        .add("password_confirmation",et_password_signup.getText().toString())
                        .build();

                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("Content-Type","application/x-www-form-urlencoded")
                        .addHeader("X-Requested-With","XMLHttpRequest")
                        .post(formBody)
                        .build();


                httpClient.newCall(request).enqueue(new Callback() {
                    @Override public void onFailure(Call call, IOException e) {
                        Log.e(vars.TAG, e.getMessage());
                        Alerter.create(MainActivity.this)
                                .setTitle("Problema con el servidor.")
                                .setText(e.getMessage())
                                .setBackgroundColorRes(R.color.colorError)
                                .show();

                    }

                    @Override public void onResponse(Call call, Response response) {

                        if(response.isSuccessful()){
                            try{
                                String i = response.body().string();
                                Intent intent = new Intent(MainActivity.this, AlacenaActivity.class);
                                startActivity(intent);

                                Gson gson = new Gson();
                                Properties properties = gson.fromJson(i, Properties.class);
                                //Log.i(TAG, "TOKEN: "+properties.getProperty("access_token"));
                                Hawk.put("access_token", properties.getProperty("access_token"));

                                finish(); // delete this activity from stack activities
                            }catch (Exception e){
                                Log.e(vars.TAG, e.getMessage());
                            }
                            finish(); // delete this activity from stack activities
                        } else {
                            Alerter.create(MainActivity.this)
                                    .setTitle("Error")
                                    .setText("Intentalo de nuevo más tarde.")
                                    .setBackgroundColorRes(R.color.colorError)
                                    .show();
                            Log.e(vars.TAG, response.message()+" "+response.body().toString() );
                        }

                    }
                });
            }
        });

    }

}
