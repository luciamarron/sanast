package com.example.sanast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class CentroSalud extends AppCompatActivity {

    String nombre, localidad;
    FirebaseFirestore db;
    CollectionReference centrosSalud;
    ListView lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_centro_salud);

         db = FirebaseFirestore.getInstance();
         centrosSalud = db.collection("centros");
         lista = (ListView) findViewById(R.id.listView);


    }


}