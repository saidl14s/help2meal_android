package com.itcg.help2meal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.jaeger.library.StatusBarUtil;
import com.orhanobut.hawk.Hawk;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.IndicatorView.draw.controller.DrawController;
import com.smarteist.autoimageslider.SliderView;
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

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class HomeTabActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    BottomNavigationView navigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_tab);
        StatusBarUtil.setLightMode(this);

        OkHttpClient okHttpClient = new OkHttpClient();
        final ImagePipelineConfig config = OkHttpImagePipelineConfigFactory
                .newBuilder(this, okHttpClient)
                .build();
        Fresco.initialize(this,config);

        Hawk.init(this).build();

        loadFragment(new HomeFragment());

        navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        //loadLastRecipes();
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_tabs, fragment)
                    .commit();
            return true;
        }
        return false;
    }


    public void clickLoadRecipe(View v){
        loadRecipe("Comida");
    }

    public void loadRecipe(String type){
        /*Intent intent = new Intent(this, RecetaActivity.class);
        intent.putExtra("type", type);
        startActivity(intent);*/
        Intent intent = new Intent(this, ResultadosActivity.class);
        startActivity(intent);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        Intent intent;

        switch (item.getItemId()) {
            case R.id.nav_home:
                fragment = new HomeFragment();
                break;

            case R.id.nav_recipes:
                intent = new Intent(this, HistorialActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_cupboard:
                intent = new Intent(this, AlacenaActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_preferences:
                intent = new Intent(this, GustosActivity.class);
                startActivity(intent);
                break;


            case R.id.nav_profile:
                intent = new Intent(this, PerfilActivity.class);
                startActivity(intent);
                break;
        }

        return loadFragment(fragment);
    }

    public void resetAll(View view){
        Hawk.deleteAll();
    }



}
