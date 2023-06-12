package com.example.sanast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.List;

public class Medicacion extends AppCompatActivity {

    private FirebaseAuth mAuth;
    TextView nombre;
    Button volver;
    ListView listViewCitas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicacion);

        nombre = findViewById(R.id.usuariomenu);
        volver = findViewById(R.id.volver);
        listViewCitas = findViewById(R.id.listMedicacion);

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

        // RECUPERACIÓN DE MEDICACIÓN
        DatabaseReference medicacionRef = FirebaseDatabase.getInstance().getReference("medicacion");

        medicacionRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> medicacionList = new ArrayList<>();

                for (DataSnapshot medicacionSnapshot : dataSnapshot.getChildren()) {
                    String medicacion = medicacionSnapshot.child("medicacion").getValue(String.class);
                    String dosis = medicacionSnapshot.child("dosis").getValue(String.class);
                    String medicacionUsu = medicacion + " - " + dosis;
                    medicacionList.add(medicacionUsu);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                        android.R.layout.simple_list_item_1, medicacionList);
                listViewCitas.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Maneja el error en caso de que ocurra
            }
        });

        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Medicacion.this, MenuInterno.class);
                startActivity(intent);
                finish();
            }
        });
    }
}