package com.example.sanast;

import static com.example.sanast.R.*;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class PantallaTemporal extends AppCompatActivity {

    TextView text;
    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_pantalla_temporal);

        //ANIMACIONES
        Animation animation1 = AnimationUtils.loadAnimation(this, anim.desplazamiento_arriba);
        Animation animation2 = AnimationUtils.loadAnimation(this, anim.desplazamiento_abajo);

        text = findViewById(R.id.sanast);
        logo = findViewById(R.id.logo);

        text.setAnimation(animation2);
        logo.setAnimation(animation1);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(PantallaTemporal.this, MenuPrincipal.class);
            startActivity(intent);
            finish();
        }, 4000);
    }
}