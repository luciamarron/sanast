package com.example.sanast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    Button entrar;
    EditText usuario, contraseña;
    FloatingActionButton botonAudio;
    MediaPlayer mediaplayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mediaplayer = MediaPlayer.create(this, R.raw.login);
        entrar = (Button) findViewById(R.id.entrar);
        usuario = (EditText) findViewById(R.id.usuario);
        contraseña = (EditText) findViewById(R.id.contraseña);
        botonAudio = findViewById(R.id.fab);

        //OCULTAR TECLADO
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        EditText editText = findViewById(R.id.contraseña);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 6) {
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                }
            }
        });

        //BOTON ?
        botonAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reproducirAudio();
            }
        });


        //BOTÓN ENTRAR
        entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(usuario.getText().toString(), contraseña.getText().toString());
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


    public void login(String correo, String contrasena) {
        if (TextUtils.isEmpty(correo) || TextUtils.isEmpty(contrasena)) {
            // Mostrar un mensaje de error si alguno de los campos está vacío
            mediaplayer = MediaPlayer.create(this, R.raw.campos);
            mediaplayer.start();
            Toast.makeText(Login.this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(correo, contrasena).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    String userEmail = user.getEmail();
                    if (userEmail.equals("admin@admin.com")) {
                        // Usuario es administrador, redirigir a la pantalla de medicación
                        Intent intent = new Intent(Login.this, MedicacionAdmin.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Usuario no es administrador, redirigir al menú interno
                        Intent intent = new Intent(Login.this, MenuInterno.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Toast.makeText(Login.this, "USUARIO O CONTRASEÑA INCORRECTOS",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void reproducirAudio() {
        // Desactivar el botón de reproducción
        botonAudio.setEnabled(false);

        // Iniciar la reproducción del audio
        mediaplayer = MediaPlayer.create(this, R.raw.login);
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
