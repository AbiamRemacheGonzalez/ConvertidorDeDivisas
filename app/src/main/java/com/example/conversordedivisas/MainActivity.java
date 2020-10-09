package com.example.conversordedivisas;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {
    final String[] datos = new String[]{"USD","EUR","GBP"};
    private Spinner monedaActualSP;
    private Spinner monedaCambioSP;
    private EditText valorCambioET;
    private TextView conversionET;
    private double valorCambio;

    final double euroDolar = 1.17;
    final double euroLibra = 0.91;
    final double dolarLibra = 0.78;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item,datos);
        monedaActualSP = (Spinner) findViewById(R.id.monedaActualSP);
        monedaActualSP.setAdapter(adaptador);
        monedaCambioSP = (Spinner) findViewById(R.id.monedaCambioSP);
        SharedPreferences preferencias = getSharedPreferences("MisPreferencias",Context.MODE_PRIVATE);
        String tmpMonedaActual = preferencias.getString("monedaActual","");
        String tmpMonedaCambio = preferencias.getString("monedaCambio","");

        if(!tmpMonedaActual.equals("")){
            int i = adaptador.getPosition(tmpMonedaActual);
            monedaActualSP.setSelection(i);
        }
        if(!tmpMonedaCambio.equals("")){
            int i = adaptador.getPosition(tmpMonedaCambio);
            monedaCambioSP.setSelection(i);
        }

    }
    //Accion del boton para convertir
    public void clickConvertir(View v) throws IOException {
        monedaActualSP = (Spinner) findViewById(R.id.monedaActualSP);
        monedaCambioSP = (Spinner) findViewById(R.id.monedaCambioSP);
        valorCambioET = (EditText) findViewById(R.id.valorCambioET);
        conversionET = (TextView) findViewById(R.id.conversionET);

        String monedaActual = monedaActualSP.getSelectedItem().toString();
        String monedaCambio = monedaCambioSP.getSelectedItem().toString();

        if(valorCambioET.getText().toString().equals("")){
            valorCambio =0;
        }else{
            valorCambio = Double.parseDouble(valorCambioET.getText().toString());
        }
        double resultado = procesarConversion(monedaActual,monedaCambio,valorCambio);
        if(resultado>0){
            conversionET.setText(String.format("Por %5.3f %s, usted recibirá %5.3f %s",valorCambio,monedaActual,resultado,monedaCambio));
            //valorCambioET.setText("");
            SharedPreferences preferencias = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferencias.edit();
            editor.putString("monedaActual",monedaActual);
            editor.putString("monedaCambio",monedaCambio);
            editor.commit();
        }else{
            conversionET.setText("Error");
            valorCambioET.setText("");
        }
    }
    public double procesarConversion(String ma, String mc, double vc){
        return vc*getValue(ma,mc);
    }

    public double getValue(String from, String to){
        if(from.equals(to)){
            return 1;
        }
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        double valor = 0;
        URL url = null;
        URLConnection con;

        try {
            url = new URL("https://free.currconv.com/api/v7/convert?apiKey=do-not-use-this-key&q=" +from +"_" + to +"&compact=y");
            con =url.openConnection();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line = in.readLine();
            Log.d("Salida",line);
            String v = line.substring(line.indexOf(to)+12,line.indexOf("}"));
            Log.d("Salida",v);
            valor = Double.parseDouble(v);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return valor;
    }

    public void getAcronimos(){

    }
}