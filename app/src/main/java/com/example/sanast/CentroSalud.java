package com.example.sanast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CentroSalud extends AppCompatActivity {

    private ListView lista;
    private List<String> dataList = new ArrayList<>();
    private String eleccionCentroSalud;
    private static final int REQUEST_ELECCION_CENTRO_SALUD = 1;
    private Usuario usuario;
    FloatingActionButton botonAudio;
    MediaPlayer mediaplayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_centro_salud);

        botonAudio = findViewById(R.id.fab);
        mediaplayer = MediaPlayer.create(this, R.raw.centrosalud);
        lista = (ListView) findViewById(R.id.listView);

        //BOTON ?

        botonAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reproducirAudio();
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
        lista.setAdapter(adapter);

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("centros_de_salud");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataList.clear();

                for (DataSnapshot centroSnapshot : dataSnapshot.getChildren()) {
                    String data = centroSnapshot.child("nombre").getValue(String.class);
                    dataList.add(data);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String centroSaludSeleccionado = (String) parent.getItemAtPosition(position);
                eleccionCentroSalud = centroSaludSeleccionado;
                Toast.makeText(CentroSalud.this, "Se hizo clic en: " + centroSaludSeleccionado, Toast.LENGTH_SHORT).show();

                // Crear un intent para iniciar la actividad de registro
                Intent intent = new Intent(CentroSalud.this, Registro.class);
                intent.putExtra("centroSalud", centroSaludSeleccionado);
                startActivityForResult(intent, REQUEST_ELECCION_CENTRO_SALUD);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ELECCION_CENTRO_SALUD && resultCode == RESULT_OK) {
            String eleccionCentroSalud = data.getStringExtra("centroSalud");
            // Asignar el valor de eleccionCentroSalud a la instancia de Usuario
            usuario.setCentro_de_salud(eleccionCentroSalud);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaplayer != null) {
            mediaplayer.release();
            mediaplayer = null;
        }
    }

    private void reproducirAudio() {
        // Desactivar el botón de reproducción
        botonAudio.setEnabled(false);

        // Iniciar la reproducción del audio
        mediaplayer = MediaPlayer.create(this, R.raw.centrosalud);
        mediaplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // Habilitar nuevamente el botón de reproducción cuando el audio termine de reproducirse
                botonAudio.setEnabled(true);
            }
        });
        mediaplayer.start();
    }


}