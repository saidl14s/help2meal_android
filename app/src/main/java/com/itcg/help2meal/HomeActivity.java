package com.itcg.help2meal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.orhanobut.hawk.Hawk;

public class HomeActivity extends AppCompatActivity {

    public Vars vars = new Vars();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Hawk.init(this).build();
    }

    public void showAlacena(View view){
        Intent intent = new Intent(this, AlacenaActivity.class);
        startActivity(intent);
    }

    public void resetAll(View view){
        Hawk.deleteAll();
    }
}
