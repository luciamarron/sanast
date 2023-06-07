package com.example.sanast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthProvider;


public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    Button entrar;
    EditText usuario, contraseña;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        entrar = (Button) findViewById(R.id.entrar);
        usuario = (EditText) findViewById(R.id.usuario);
        contraseña = (EditText) findViewById(R.id.contraseña);

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
                if (s.length() == 8) {
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                }
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

    public void login(String correo, String contrasena){
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(correo, contrasena).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    Intent intent = new Intent(Login.this, MenuInterno.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(Login.this, "USUARIO O CONTRASEÑA INCORRECTOS",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}