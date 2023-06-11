package com.example.sanast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

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
        // Obtén la referencia de la base de datos
        DatabaseReference usuariosRef = FirebaseDatabase.getInstance().getReference("usuarios");

        // Obtén el ID del usuario actualmente autenticado
        String userId = mAuth.getCurrentUser().getUid();

        // Escucha los cambios en los datos del usuario actual
        usuariosRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Verifica si existen datos para el usuario actual
                if (dataSnapshot.exists()) {
                    // Obtiene el nombre del usuario desde la base de datos
                    String nombreUsuario = dataSnapshot.child("nombre").getValue(String.class).toUpperCase();

                    // Asigna el nombre del usuario al TextView
                    nombre.setText(nombreUsuario);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //RECUPERACION DE CITAS
        String userCita = mAuth.getCurrentUser().getUid();
        DatabaseReference citasRef = FirebaseDatabase.getInstance().getReference("citas").child(userCita);

        // Obtener la referencia al ListView en tu layout de actividad
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

                // Crear un ArrayAdapter para mostrar las citas en el ListView
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, citasList);
                listViewCitas.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProximasCitas.this, MenuInterno.class);
                startActivity(intent);
                finish();
            }
        });

    }
}