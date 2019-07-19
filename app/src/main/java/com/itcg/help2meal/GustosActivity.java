package com.itcg.help2meal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class GustosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gustos);
    }
}
