package com.example.sanast;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MenuInterno extends AppCompatActivity {

    private FirebaseAuth mAuth;
    Button medicacion, prox_cita, cita, llamarCentroSalud;
    TextView nombre;
    FloatingActionButton botonAudio;
    MediaPlayer mediaplayer;

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
        llamarCentroSalud = findViewById(R.id.llamada);

        //BOTON ?
        botonAudio.setOnClickListener(v -> reproducirAudio());

        //MENÚ
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

        //BOTÓN TELÉFONO
        llamarCentroSalud.setOnClickListener(view -> {
            String userId1 = mAuth.getCurrentUser().getUid(); // Obtén el ID del usuario loggeado
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("usuarios").child(userId1);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String nombreCentroSalud = dataSnapshot.child("centro_de_salud").getValue(String.class);
                        if (nombreCentroSalud != null) {
                            DatabaseReference centroSaludRef = FirebaseDatabase.getInstance().getReference().child("centros_de_salud");

                            Query query = centroSaludRef.orderByChild("nombre").equalTo(nombreCentroSalud);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        DataSnapshot centroSaludSnapshot = dataSnapshot.getChildren().iterator().next();
                                        String telefono = centroSaludSnapshot.child("telefono").getValue(String.class);
                                        if (telefono != null && !telefono.isEmpty()) {
                                            Uri number = Uri.parse("tel:" + telefono);
                                            Intent dial = new Intent(Intent.ACTION_DIAL, number);
                                            startActivity(dial);
                                        } else {
                                            Toast.makeText(MenuInterno.this, "El centro de salud no tiene un número de teléfono válido", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(MenuInterno.this, "El centro de salud no existe", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        } else {
                            Toast.makeText(MenuInterno.this, "El usuario no tiene asignado un centro de salud", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MenuInterno.this, "El usuario no existe", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
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
        mediaplayer.setOnCompletionListener(mp -> {
            // Habilitar nuevamente el botón de reproducción cuando el audio termine de reproducirse
            botonAudio.setEnabled(true);
        });
        mediaplayer.start();
    }

}