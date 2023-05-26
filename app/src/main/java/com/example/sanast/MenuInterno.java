package com.example.sanast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MenuInterno extends AppCompatActivity {

    Button medicacion, prox_cita, cita;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_interno);

        cita = (Button) findViewById(R.id.cita);
        medicacion= (Button) findViewById(R.id.medicacion);
        prox_cita = (Button) findViewById(R.id.prox_citas);

        cita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ReservaCita.class);
                startActivity(intent);
            }
        });

        medicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Medicacion.class);
                startActivity(intent);
            }
        });

        prox_cita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ProximasCitas.class);
                startActivity(intent);
            }
        });
    }
}