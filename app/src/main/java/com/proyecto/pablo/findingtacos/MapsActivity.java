package com.proyecto.pablo.findingtacos;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback ,GoogleMap.OnMarkerClickListener{

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

       // mMap.setOnMapClickListener((GoogleMap.OnMapClickListener) this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        try{
            //int lat, lng;
            obtenerDatos prueba = new obtenerDatos();
            prueba.execute();
            //Toast.makeText(getApplicationContext(),,Toast.LENGTH_LONG).show();
            //lista = (List) cadena = resul.substring(2,resul.length()-2);
            //Cadenas = cadena.split(",");
            //lat = Integer.getInteger(Cadenas[3]);
            //lng = Integer.getInteger(Cadenas[4]);
            //Toast.makeText(getApplicationContext(),Cadenas[1]+" "+lat+" "+lng,Toast.LENGTH_LONG).show();
            // Add a marker in Sydney and move the camera
            LatLng sydney = new LatLng(-31, 100);
            mMap.addMarker(new MarkerOptions().position(sydney).title("Cadenas"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            mMap.setOnMarkerClickListener(this);
        }catch(Exception e){
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        Intent toy = new Intent(MapsActivity.this,Informacion.class);
        startActivity(toy);

        return false;
    }

    public class obtenerDatos extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            URL url = null;
            String linea = "";
            int respuesta = 0;
            StringBuilder resul = null;
            HttpURLConnection conection = null;
            try {
                url = new URL("http://192.168.0.2/WebService/salidaFormato.php");
                conection = (HttpURLConnection) url.openConnection();
                conection.setRequestProperty("User-Agent","Mozilla/5.0 (Linux; Android 1.5; es-Es) Ejemplo HTTP");
                //"User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.81 Safari/537.36"
                //"User-Agent","Mozilla/5.0 (Linux; Android 1.5; es-Es) Ejemplo HTTP"
                respuesta = conection.getResponseCode();
                resul = new StringBuilder();

                if(respuesta == HttpURLConnection.HTTP_OK) {
                    InputStream in = new BufferedInputStream(conection.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                    while ((linea = reader.readLine()) != null) {
                        resul.append(linea);
                    }

                    JSONObject respuestaJSON = new JSONObject(resul.toString());

                    return respuestaJSON.toString();

                }
               }catch (Exception e){
                return e.toString()+" "+ respuesta+" "+HttpURLConnection.HTTP_OK;
            }
            return "Fallo en la coneccion"+" "+ respuesta+" "+HttpURLConnection.HTTP_OK;
        }

        @Override
        protected void onPostExecute(String res){
            super.onPostExecute(res);
            Toast.makeText(getApplicationContext(),res,Toast.LENGTH_LONG).show();
        }
    }


}// Fin de todo

