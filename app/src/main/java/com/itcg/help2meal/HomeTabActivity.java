package com.itcg.help2meal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.jaeger.library.StatusBarUtil;
import com.orhanobut.hawk.Hawk;
import okhttp3.OkHttpClient;


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


    public void clickLoadRecipesComida(View v){
        Intent intent = new Intent(this, ResultadosActivity.class);
        intent.putExtra("type_recipe", "comida");
        startActivity(intent);
    }
    public void clickLoadRecipesCena(View v){
        Intent intent = new Intent(this, ResultadosActivity.class);
        intent.putExtra("type_recipe", "cena");
        startActivity(intent);
    }
    public void clickLoadRecipesDesayuno(View v){
        Intent intent = new Intent(this, ResultadosActivity.class);
        intent.putExtra("type_recipe", "desayuno");
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

}
