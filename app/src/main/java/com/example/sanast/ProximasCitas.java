package com.example.sanast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProximasCitas extends AppCompatActivity {

    private FirebaseAuth mAuth;
    TextView nombre;
    Button volver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proximas_citas);

        nombre = findViewById(R.id.usuariomenu);
        volver = findViewById(R.id.volver);


        //RECUPERACIÓN DE NOMBRE DE USUARIO
        mAuth = FirebaseAuth.getInstance();
        DatabaseReference usuariosRef = FirebaseDatabase.getInstance().getReference("usuarios");
        String userId = mAuth.getCurrentUser().getUid();

        usuariosRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    String nombreUsuario = dataSnapshot.child("nombre").getValue(String.class).toUpperCase();

                    // Asignar el nombre del usuario al TextView
                    nombre.setText(nombreUsuario);

                    // Comprobar la longitud del nombre
                    if (nombreUsuario.length() > 25) {
                        nombre.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                        nombre.setSingleLine(false);
                        nombre.setMaxLines(2);
                        nombre.setGravity(Gravity.END);
                    } else {
                        nombre.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                        nombre.setSingleLine(true);
                        nombre.setGravity(Gravity.START);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //RECUPERACION DE CITAS
        String userCita = mAuth.getCurrentUser().getUid();
        DatabaseReference citasRef = FirebaseDatabase.getInstance().getReference("citas").child(userCita);

        // Obtener la referencia al ListView
        ListView listViewCitas = findViewById(R.id.listMedicacion);

        // Mostrar las citas del usuario
        citasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> citasList = new ArrayList<>();
                for (DataSnapshot citaSnapshot : dataSnapshot.getChildren()) {
                    String fecha = citaSnapshot.child("fecha").getValue(String.class);
                    String tipo = citaSnapshot.child("tipo").getValue(String.class);
                    String cita = fecha + " - " + tipo;
                    citasList.add(cita);
                }

                //ORDENAR LAS FECHAS
                Collections.sort(citasList, new Comparator<String>() {
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                    @Override
                    public int compare(String cita1, String cita2) {
                        try {
                            Date fecha1 = dateFormat.parse(cita1.split(" - ")[0]);
                            Date fecha2 = dateFormat.parse(cita2.split(" - ")[0]);
                            return fecha1.compareTo(fecha2);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return 0;
                    }
                });

                // Crear un ArrayAdapter para mostrar las citas en el ListView
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, citasList);
                listViewCitas.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        volver.setOnClickListener(v -> {
            Intent intent = new Intent(ProximasCitas.this, MenuInterno.class);
            startActivity(intent);
            finish();
        });

    }
}