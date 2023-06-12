package com.example.sanast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class MedicacionAdmin extends AppCompatActivity {

    ListView listaUsuarios;
    private String eleccionUsuario;
    private static final int REQUEST_ELECCION_USUARIO = 1;
    private Usuario usuario;
    private FirebaseAuth mAuth;
    private DatabaseReference medicacionRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicacion_admin);

        listaUsuarios = findViewById(R.id.listaUsuarios);

        //RECOGER USUARIOS BBDD
        ListView listaUsuarios = findViewById(R.id.listaUsuarios);
        ArrayList<String> dataList = new ArrayList<>();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
        listaUsuarios.setAdapter(adapter);

        DatabaseReference usuariosRef = FirebaseDatabase.getInstance().getReference("usuarios");

        usuariosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataList.clear();

                for (DataSnapshot usuarioSnapshot : dataSnapshot.getChildren()) {
                    String nombre = usuarioSnapshot.child("nombre").getValue(String.class);
                    String userId = usuarioSnapshot.getKey();

                    // Verificar si el usuario actual no es el administrador
                    if (!usuarioSnapshot.getKey().equals("admin")) {
                        String usuarioString = nombre + " - ID: " + userId;
                        dataList.add(usuarioString);
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        listaUsuarios.setOnItemClickListener((parent, view, position, id) -> {
            String usuarioSeleccionado = (String) parent.getItemAtPosition(position);
            eleccionUsuario = usuarioSeleccionado;

            String userId = obtenerIdUsuario(usuarioSeleccionado);

            // Crear un intent para iniciar la actividad de registro
            Intent intent = new Intent(MedicacionAdmin.this, MedicacionAdmin2.class);
            intent.putExtra("usuarioMed", userId);
            startActivityForResult(intent, REQUEST_ELECCION_USUARIO);
        });

        mAuth = FirebaseAuth.getInstance();
        medicacionRef = FirebaseDatabase.getInstance().getReference("medicacion");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ELECCION_USUARIO && resultCode == RESULT_OK) {
            String eleccionCentroSalud = data.getStringExtra("usuarioMed");
            // Asignar el valor de eleccionCentroSalud a la instancia de Usuario
            usuario.setCentro_de_salud(eleccionCentroSalud);

            // Obtener el identificador del usuario autenticado
            if (mAuth.getCurrentUser() != null) {
                String userId = mAuth.getCurrentUser().getUid();
                DatabaseReference medicacionRef = FirebaseDatabase.getInstance().getReference("medicacion");
                DatabaseReference usuarioMedicacionRef = medicacionRef.child(userId);

                usuarioMedicacionRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<String> medicacionList = new ArrayList<>();
                        for (DataSnapshot medicacionSnapshot : dataSnapshot.getChildren()) {
                            String medicacion = medicacionSnapshot.child("medicacion").getValue(String.class);
                            String dosis = medicacionSnapshot.child("dosis").getValue(String.class);
                            String medicacionUsu = medicacion + " - " + dosis;
                            medicacionList.add(medicacionUsu);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    private String obtenerIdUsuario(String usuarioSeleccionado) {
        String[] partes = usuarioSeleccionado.split(" - ID: ");
        return partes[1];
    }


}