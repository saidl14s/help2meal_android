package com.itcg.help2meal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.jaeger.library.StatusBarUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StatusBarUtil.setTransparent(this);
    }

    public void testActivity(View view)
    {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
    public void testPerfil(View view)
    {
        Intent intent = new Intent(this, PerfilActivity.class);
        startActivity(intent);
    }
}
