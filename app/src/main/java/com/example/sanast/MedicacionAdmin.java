package com.example.sanast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MedicacionAdmin extends AppCompatActivity {

    ListView listaUsuarios;
    Button botonRegistrar;
    EditText campoMedicacion, campoDosis;
    private String eleccionUsuario;
    private List<String> dataList = new ArrayList<>();
    private static final int REQUEST_ELECCION_USUARIO = 1;
    private Usuario usuario;

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
                    String dni = usuarioSnapshot.child("dni").getValue(String.class);

                    // Verificar si el usuario actual no es el administrador
                    if (!usuarioSnapshot.getKey().equals("admin")) {
                        String usuarioString = nombre + " - DNI: " + dni;
                        dataList.add(usuarioString);
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar el error si es necesario
            }
        });

        listaUsuarios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String usuarioSeleccionado = (String) parent.getItemAtPosition(position);
                eleccionUsuario = usuarioSeleccionado;
                Toast.makeText(MedicacionAdmin.this, "Se hizo clic en: " + usuarioSeleccionado, Toast.LENGTH_SHORT).show();

                // Crear un intent para iniciar la actividad de registro
                Intent intent = new Intent(MedicacionAdmin.this, MedicacionAdmin2.class);
                intent.putExtra("usuarioMed", usuarioSeleccionado);
                startActivityForResult(intent, REQUEST_ELECCION_USUARIO);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ELECCION_USUARIO && resultCode == RESULT_OK) {
            String eleccionCentroSalud = data.getStringExtra("usuarioMed");
            // Asignar el valor de eleccionCentroSalud a la instancia de Usuario
            usuario.setCentro_de_salud(eleccionCentroSalud);
        }
    }

}