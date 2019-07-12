package com.itcg.help2meal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.jaeger.library.StatusBarUtil;

public class PerfilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorVerde));
    }
}
