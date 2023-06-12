package com.example.sanast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;

import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MenuInterno extends AppCompatActivity {

    private FirebaseAuth mAuth;
    Button medicacion, prox_cita, cita, llamarCentroSalud;
    TextView nombre;
    FloatingActionButton botonAudio;
    MediaPlayer mediaplayer;
    private static final int REQUEST_PHONE_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_interno);

        cita = findViewById(R.id.cita);
        medicacion = findViewById(R.id.medicacion);
        prox_cita = findViewById(R.id.prox_citas);
        nombre = findViewById(R.id.usuariomenu);
        botonAudio = findViewById(R.id.fab);
        mediaplayer = MediaPlayer.create(this, R.raw.menu);

        //BOTON ?
        botonAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reproducirAudio();
            }
        });

        //MENU
        cita.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), ReservaCita.class);
            startActivity(intent);
        });

        medicacion.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Medicacion.class);
            startActivity(intent);
        });

        prox_cita.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), ProximasCitas.class);
            startActivity(intent);
        });

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

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaplayer != null) {
            mediaplayer.release();
            mediaplayer = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaplayer != null && mediaplayer.isPlaying()) {
            mediaplayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        botonAudio.setEnabled(true);
    }

    private void reproducirAudio() {
        // Desactivar el botón de reproducción
        botonAudio.setEnabled(false);

        // Iniciar la reproducción del audio
        mediaplayer = MediaPlayer.create(this, R.raw.menu);
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