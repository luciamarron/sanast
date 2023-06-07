package com.example.sanast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Medicacion extends AppCompatActivity {

    private FirebaseAuth mAuth;
    TextView nombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicacion);

        nombre = findViewById(R.id.usuariomenu);

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
    }
}