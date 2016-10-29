package com.proyecto.pablo.findingtacos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Informacion extends AppCompatActivity {
    Button botonPromos;
    Button botonNuevoComen;
    int idTaque = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion);
        botonPromos = (Button)findViewById(R.id.btnPromos);
        botonNuevoComen = (Button)findViewById(R.id.nuevoComentario);

        botonPromos.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent hola = new Intent(Informacion.this,promosTaqueria.class);
                Bundle pasar = new Bundle();
                pasar.putInt("idTaqueria",idTaque);
                startActivity(hola);
            }

        });

        botonNuevoComen.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent hola = new Intent(Informacion.this,agregarComentario.class);
                Bundle pasar = new Bundle();
                pasar.putInt("idTaqueria",idTaque);
                startActivity(hola);
            }

        });
    }
}
