package com.example.sanast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MedicacionAdmin2 extends AppCompatActivity {

    Button asignar;
    EditText medicacion, dosis;
    public String eleccionUsuario;
    private FirebaseAuth mAuth;
    private DatabaseReference medicacionRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicacion_admin2);

        medicacion = findViewById(R.id.medicacion);
        dosis = findViewById(R.id.dosis);
        asignar = findViewById(R.id.asignar);

        eleccionUsuario = getIntent().getStringExtra("usuarioMed");

        //REGISTRAR MEDICACION
        mAuth = FirebaseAuth.getInstance();
        medicacionRef = FirebaseDatabase.getInstance().getReference("medicacion").child(eleccionUsuario);

        asignar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String medicacionValor = medicacion.getText().toString();
                String dosisValor = dosis.getText().toString();

                if (medicacionValor.isEmpty() || dosisValor.isEmpty()) {
                    Toast.makeText(MedicacionAdmin2.this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                } else {
                    guardarDatos(medicacionValor, dosisValor);
                }
            }
        });
    }

    private void guardarDatos(String medicacionValor, String dosisValor) {
        String medicacionId = medicacionRef.push().getKey();

        Dosis medic = new Dosis();
        medic.setMedicacion(medicacionValor);
        medic.setDosis(dosisValor);

        medicacionRef.child(medicacionId).setValue(medic);

        AlertDialog.Builder builder = new AlertDialog.Builder(MedicacionAdmin2.this);
        builder.setTitle("Medicación añadida correctamente");
        builder.setMessage("¿Deseas agregar más medicaciones?");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // Redirige al usuario a la pantalla para agregar más medicaciones
                // Por ejemplo, puedes usar un Intent para iniciar otra actividad
                Intent intent = new Intent(MedicacionAdmin2.this, MedicacionAdmin.class);
                intent.putExtra("usuarioMed", eleccionUsuario);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // Redirige al usuario a otra pantalla
                // Por ejemplo, puedes usar un Intent para iniciar otra actividad
                Intent intent = new Intent(MedicacionAdmin2.this, MenuPrincipal.class);
                startActivity(intent);
            }
        });
        builder.show();
    }
}

