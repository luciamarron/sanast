package com.example.sanast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReservaCita extends AppCompatActivity {

    private FirebaseAuth mAuth;
    TextView nombre;
    CalendarView calendarView;
    String fechaSeleccionada = null;
    FloatingActionButton botonAudio;
    MediaPlayer mediaplayer;
    RadioButton presencial, telefonica;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserva_cita);

        nombre = findViewById(R.id.usuariomenu);
        calendarView = findViewById(R.id.calendarView);
        mediaplayer = MediaPlayer.create(this, R.raw.citas);
        botonAudio = findViewById(R.id.fab);
        presencial = findViewById(R.id.consultaPresencial);
        telefonica = findViewById(R.id.consultaTelefonica);

        //BOTON ?
        botonAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reproducirAudio();
            }
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



        // Guardar la cita en la base de datos

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // El mes comienza en 0, por lo que se debe sumar 1 al mes
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, month, dayOfMonth);

                // Obtener la fecha actual
                Calendar currentDate = Calendar.getInstance();

                // Verificar si la fecha seleccionada es posterior a la fecha actual
                if (selectedDate.after(currentDate)) {
                    fechaSeleccionada = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year);
                } else {
                    Toast.makeText(getApplicationContext(), "Por favor, seleccione una fecha posterior a hoy", Toast.LENGTH_SHORT).show();
                    fechaSeleccionada = null;
                }
            }
        });

        Button botonReservar = findViewById(R.id.reservar);
        botonReservar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fechaSeleccionada == null) {
                    Toast.makeText(getApplicationContext(), "Por favor, seleccione una fecha", Toast.LENGTH_SHORT).show();
                    return;
                }else if (!presencial.isChecked() && !telefonica.isChecked()){
                    Toast.makeText(getApplicationContext(), "Por favor, seleccione el tipo de consulta", Toast.LENGTH_SHORT).show();
                } else {
                    String tipoCita = obtenerTipoCitaDelRadioButton();

                    // Guardar la cita en la base de datos
                    String userCita = mAuth.getCurrentUser().getUid();
                    DatabaseReference citasRef = FirebaseDatabase.getInstance().getReference("citas").child(userCita);

                    // Verificar si ya existe una cita para la fecha seleccionada
                    citasRef.orderByChild("fecha").equalTo(fechaSeleccionada).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // Mostrar un mensaje al usuario indicando que ya tiene una cita para ese día
                                Toast.makeText(getApplicationContext(), "Ya tiene una cita para este día", Toast.LENGTH_SHORT).show();
                            } else {
                                // No existe una cita para el día seleccionado, guardar la cita en la base de datos
                                DatabaseReference nuevaCitaRef = citasRef.push();
                                nuevaCitaRef.child("fecha").setValue(fechaSeleccionada);
                                nuevaCitaRef.child("tipo").setValue(tipoCita);
                                mostrarDialogoNuevaCita();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Maneja el error en caso de que ocurra
                        }
                    });
                }
            }
        });
    }

    private String obtenerFechaSeleccionadaDelCalendarView() {
        CalendarView calendarView = findViewById(R.id.calendarView);
        long fechaSeleccionadaMillis = calendarView.getDate();
        Date fechaSeleccionada = new Date(fechaSeleccionadaMillis);

        Date fechaActual = new Date();

        if (fechaSeleccionada.after(fechaActual)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return dateFormat.format(fechaSeleccionada);
        } else {
            Toast.makeText(getApplicationContext(), "Por favor, seleccione una fecha posterior a hoy", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private String obtenerTipoCitaDelRadioButton() {
        RadioButton radioButtonConsulta = findViewById(R.id.consultaTelefonica);
        RadioButton radioButtonPresencial = findViewById(R.id.consultaPresencial);

        if (radioButtonConsulta.isChecked()) {
            return "Consulta telefónica";
        } else if (radioButtonPresencial.isChecked()) {
            return "Consulta presencial";
        }

        return "";
    }

    private void mostrarDialogoNuevaCita() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("CITA RESERVADA CON ÉXITO \n¿Quieres solicitar una nueva cita?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Permanecer en la pantalla actual (no se requieren acciones adicionales)
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Ir al menú interno
                        Intent intent = new Intent(ReservaCita.this, MenuInterno.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
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
        mediaplayer = MediaPlayer.create(this, R.raw.citas);
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