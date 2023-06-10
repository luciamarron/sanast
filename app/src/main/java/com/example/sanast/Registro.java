package com.example.sanast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registro extends AppCompatActivity {

    EditText nombre, dni, correo, contraseña;
    private FirebaseAuth mAuth;
    Usuario usuario = new Usuario();
    private String eleccionCentroSalud;
    FloatingActionButton botonAudio;
    MediaPlayer mediaplayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        nombre = (EditText) findViewById(R.id.nombre);
        dni = (EditText) findViewById(R.id.dni);
        correo = (EditText) findViewById(R.id.correo);
        contraseña = (EditText) findViewById(R.id.contr);
        mediaplayer = MediaPlayer.create(this, R.raw.registro);

        botonAudio = findViewById(R.id.fab);

        eleccionCentroSalud = getIntent().getStringExtra("centroSalud");

        //BOTON ?
        botonAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reproducirAudio();
            }
        });


        //REGISTRO
        mAuth = FirebaseAuth.getInstance();

        // Implementar el evento de clic en el botón "Registrar"
        Button registrarButton = findViewById(R.id.registrar);
        registrarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener los valores de los campos
                String nombreValor = nombre.getText().toString();
                String dniValor = dni.getText().toString();
                String correoValor = correo.getText().toString();
                String contraseñaValor = contraseña.getText().toString();

                // Verificar que los campos no estén vacíos
                if (nombreValor.isEmpty() || dniValor.isEmpty() ||
                        correoValor.isEmpty() || contraseñaValor.isEmpty()) {
                    mediaplayer = MediaPlayer.create(Registro.this, R.raw.campos);
                    mediaplayer.start();
                    Toast.makeText(Registro.this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    //ASIGNACIÓN DE VALORES DE USUARIO
                    usuario.setNombre(nombreValor);
                    usuario.setDni(dniValor);
                    usuario.setCorreo(correoValor);
                    usuario.setContraseña(contraseñaValor);
                    usuario.setCentro_de_salud(eleccionCentroSalud);
                    // Crear el usuario en Firebase Authentication
                    String correo = correoValor;
                    String password = contraseñaValor;

                    mAuth.createUserWithEmailAndPassword(correo, password)
                            .addOnCompleteListener(Registro.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Registro exitoso
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        // Guardar los datos adicionales del usuario en Firebase Realtime Database
                                        guardarDatosUsuario(user.getUid());
                                        Intent intent = new Intent(Registro.this, Login.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // El registro falló, mostrar mensaje de error
                                        if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().getEmail().equals(correo)) {
                                            Toast.makeText(Registro.this, "El usuario ya está registrado", Toast.LENGTH_SHORT).show();
                                        } else {
                                            if (password.length() < 6) {
                                                Toast.makeText(Registro.this, "La contraseña debe tener 6 caracteres como mínimo", Toast.LENGTH_SHORT).show();
                                            }
                                            if (!correoValido(correo)) {
                                                Toast.makeText(Registro.this, "Email inválido", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(Registro.this, "Error al registrar usuario", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                }

                            });
                }
            }
        });
    }

    private void guardarDatosUsuario(String userId) {
        DatabaseReference usuariosRef = FirebaseDatabase.getInstance().getReference("usuarios");
        usuariosRef.child(userId).setValue(usuario);
    }

    //MÉTODO QUE COMPRUEBA QUE EL CORREO TIENE UN PATRÓN VÁLIDO
    public static boolean correoValido(String correo) {
        Pattern pattern = Pattern.compile("([a-z0-9]+(\\.?[a-z0-9])*)+@(([a-z]+)\\.([a-z]+))+");
        Matcher marcher = pattern.matcher(correo);
        return marcher.matches();
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
        mediaplayer = MediaPlayer.create(this, R.raw.registro);
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