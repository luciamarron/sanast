package com.example.sanast;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MenuPrincipal extends AppCompatActivity {

    Button sesion, registro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        sesion = findViewById(R.id.sesion);
        registro = findViewById(R.id.registro);

        sesion.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        });

        registro.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), CentroSalud.class);
            startActivity(intent);
        });
    }
}