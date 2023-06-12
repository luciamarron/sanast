package com.example.sanast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
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
import java.util.Calendar;
import java.util.Date;
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
        botonAudio.setOnClickListener(v -> reproducirAudio());

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

        // Guardar la cita en la base de datos
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
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
        });

        //BOTÓN RESERVAR
        Button botonReservar = findViewById(R.id.reservar);
        botonReservar.setOnClickListener(v -> {
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
                            Toast.makeText(getApplicationContext(), "Ya tiene una cita para este día", Toast.LENGTH_SHORT).show();
                        } else {
                            DatabaseReference nuevaCitaRef = citasRef.push();
                            nuevaCitaRef.child("fecha").setValue(fechaSeleccionada);
                            nuevaCitaRef.child("tipo").setValue(tipoCita);
                            mostrarDialogoNuevaCita();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
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
                .setPositiveButton("Sí", (dialog, which) -> {

                })
                .setNegativeButton("No", (dialog, which) -> {
                    Intent intent = new Intent(ReservaCita.this, MenuInterno.class);
                    startActivity(intent);
                    finish();
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
        mediaplayer.setOnCompletionListener(mp -> {
            // Habilitar nuevamente el botón de reproducción cuando el audio termine de reproducirse
            botonAudio.setEnabled(true);
        });
        mediaplayer.start();
    }

}