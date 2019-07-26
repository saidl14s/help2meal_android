package com.itcg.help2meal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.jaeger.library.StatusBarUtil;

public class IngredientePersonalizadoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingrediente_personalizado);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorAzul));
    }

    public void actionAddIngredient (View view){
        finish();
    }
}
