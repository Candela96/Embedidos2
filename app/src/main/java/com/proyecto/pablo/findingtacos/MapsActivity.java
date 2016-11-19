package com.proyecto.pablo.findingtacos;


import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener{

    private GoogleMap mMap;
    Button botonBuscar;
    EditText textTaqueriaBuscada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (!verificaConexion(this)) {
            Toast.makeText(getBaseContext(),"Comprueba tu conexión a Internet. Saliendo ... ", Toast.LENGTH_LONG).show();
            this.finish();
        }else {

            botonBuscar = (Button) findViewById(R.id.btnBuscarTaqueria);
            textTaqueriaBuscada = (EditText) findViewById(R.id.buscarTaqueria);
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            botonBuscar.setOnClickListener(this);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        try{
            obtenerDatos prueba = new obtenerDatos();
            prueba.execute();
        }catch(Exception e){
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v){
            String nombre = textTaqueriaBuscada.getText().toString();
            obtenerDatosBusqueda pruebaBusqueda = new obtenerDatosBusqueda();
            pruebaBusqueda.execute(nombre);
    }


    public static boolean verificaConexion(Context ctx) {
        boolean bConectado = false;
        ConnectivityManager connec = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo[] redes = connec.getAllNetworkInfo();

        for (int i = 0; i < 2; i++) {

            if (redes[i].getState() == NetworkInfo.State.CONNECTED) {
                bConectado = true;
            }
        }
        return bConectado;
    }


    public class obtenerDatos extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            URL url = null;
            String linea = "";
            String  finalJsonObject;
            StringBuilder resul = null;
            int respuesta = 0;
            HttpURLConnection conection = null;

            try {
                //192.168.43.206
                url = new URL("http://192.168.0.2/WebService/Consulta.php");
                conection = (HttpURLConnection) url.openConnection();
                conection.setRequestProperty("User-Agent","Mozilla/5.0 (Linux; Android 1.5; es-Es) Ejemplo HTTP");
                respuesta = conection.getResponseCode();
                resul = new StringBuilder();

                if(respuesta == HttpURLConnection.HTTP_OK) {

                    InputStream in = new BufferedInputStream(conection.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                    while ((linea = reader.readLine()) != null) {
                        resul.append(linea);
                    }
                    finalJsonObject = resul.toString();
                    return finalJsonObject;
                }
            }catch (Exception e){
                return "error_1: "+ e.toString()+" "+ respuesta+" "+HttpURLConnection.HTTP_OK;
            }
            return "error_0: Fallo en la coneccion"+" "+ respuesta+" "+HttpURLConnection.HTTP_OK;
        }

        @Override
        protected void onPostExecute(String res){
            if(res.contains("error_1")) {

                //Madamos el mensaje de error que nos paso la funcion doInBackGroud();
                Toast.makeText(getApplicationContext(), res, Toast.LENGTH_LONG).show();
            }else if(res.contains("error_0")){

                //Madamos el mensaje de error que nos paso la funcion doInBackGroud();
                Toast.makeText(getApplicationContext(), res, Toast.LENGTH_LONG).show();
            }else {
                super.onPostExecute(res);

                    try {
                        //Hacemos un arreglo de Json con el string  que retorna la funcion doInBackground
                        JSONArray respuestaArray = new JSONArray(res);

                        for (int i = 0; i < respuestaArray.length(); i++) {
                            //Leemos cada uno de los objetos del array de jsons y obtenemos sus valores
                            JSONObject objeto = respuestaArray.getJSONObject(i);
                            int idTaque = objeto.getInt("id");
                            String nomTaque = objeto.getString("Nombre");
                            String telTaque = objeto.getString("Telefono");
                            double gra = objeto.getDouble("Grados");
                            double lat = objeto.getDouble("Latitud");

                            //Creamos los marcadores de google map y los ponemos en el mapa
                            LatLng marca = new LatLng(gra, lat);
                            mMap.addMarker(new MarkerOptions().position(marca).title(nomTaque).snippet(telTaque + "/" + idTaque));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(marca));

                        }// Fin del for

                    } catch (JSONException e) {
                        //Enviamos un mensaje al usuario con el error obtenido
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                    }

            }//Fin del if
        }
    }

    public class obtenerDatosBusqueda extends AsyncTask<String, Void , String> {

        @Override
        protected String doInBackground(String... params) {
            URL url = null;
            String linea = "";
            String  finalJsonObject;
            StringBuilder resul = null;
            int respuesta = 0;
            HttpURLConnection conection = null;
            try {

                url = new URL("http://192.168.0.2/WebService/ConsultaUnaTaqueria.php?nombre="+params[0]);
                conection = (HttpURLConnection) url.openConnection();
                conection.setRequestMethod("GET");
                conection.setRequestProperty("User-Agent","Mozilla/5.0 (Linux; Android 1.5; es-Es) Ejemplo HTTP");
                respuesta = conection.getResponseCode();
                resul = new StringBuilder();

                if(respuesta == HttpURLConnection.HTTP_OK) {

                    InputStream in = new BufferedInputStream(conection.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                    while ((linea = reader.readLine()) != null) {
                        resul.append(linea);
                    }
                    finalJsonObject = resul.toString();

                    return finalJsonObject;
                }
            }catch (Exception e){
                return "error_1: "+ e.toString()+" "+ respuesta+" "+HttpURLConnection.HTTP_OK;
            }
            return "error_0: Fallo en la coneccion"+" "+ respuesta+" "+HttpURLConnection.HTTP_OK;
        }

        @Override
        protected void onPostExecute(String res){
            if(res.contains("error_1")) {

                //Madamos el mensaje de error que nos paso la funcion doInBackGroud();
                Toast.makeText(getApplicationContext(), res, Toast.LENGTH_LONG).show();
            }else if(res.contains("error_0")){

                //Madamos el mensaje de error que nos paso la funcion doInBackGroud();
                Toast.makeText(getApplicationContext(), res, Toast.LENGTH_LONG).show();
            }else {
                super.onPostExecute(res);
                if(res.contains("error_1:")) {
                    Toast.makeText(getApplicationContext(), "No se encontro ninguna coincidencia", Toast.LENGTH_LONG).show();
                }else {
                    try {
                        //Hacemos un arreglo de Json con el string  que retorna la funcion doInBackground
                        JSONArray respuestaArray = new JSONArray(res);

                        for (int i = 0; i < respuestaArray.length(); i++) {
                            //Leemos cada uno de los objetos del array de jsons y obtenemos sus valores
                            JSONObject objeto = respuestaArray.getJSONObject(i);
                            int idTaque = objeto.getInt("id");
                            String nomTaque = objeto.getString("Nombre");
                            String telTaque = objeto.getString("Telefono");
                            double gra = objeto.getDouble("Grados");
                            double lat = objeto.getDouble("Latitud");

                            //Creamos los marcadores de google map y los ponemos en el mapa
                            LatLng marca = new LatLng(gra, lat);
                            mMap.addMarker(new MarkerOptions().position(marca).title(nomTaque).snippet(telTaque + "/" + idTaque));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(marca));

                        }// Fin del for

                    } catch (JSONException e) {
                        //Enviamos un mensaje al usuario con el error obtenido
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        }//Fin del if
    }
}// Fin de todo

