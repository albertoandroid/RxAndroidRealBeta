package com.androiddesdecero.rxusoreal.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.androiddesdecero.rxusoreal.R;

import androidx.appcompat.app.AppCompatActivity;

/*
Ventajas de la programación reactiva cuando leemos datos de una
base de datos local o de un web service:
1.- La programación Reactiva nos ayuda a trabajar con flujos asycronos de
datos y las llamadas a una base de datos local o web service, son llamadas
asyncronas y por lo general nos devuelven datos. Por ello casa tan bien
RX con Retrofit. Podemos tener control total de en que hilo se ejecuta cada
operación y en que hilo queremos que se muestren los datos, a través del
oberver y observador.
2.- La programación Funcional Reactiva nos permite Filtar, Transformar (map)
y reducir como vimos con los operadores de RX esos Stremas de datos asyncronos y
convertirlos en otros tipos de datos que nos sirvan mejor a nuestra aplicación.
Es decir la llamada a un webservice nos puede dar uno datos, pero nosotros podemos
filtrarlos, o transformalos o reducirlos tal cual sean necesarios para nuestra app.
3.-Nos evita el infierno de los callback anidados, lo que se conoce como
callback hell.

 */

public class MainActivity extends AppCompatActivity {

    private Button btRxRetrofit;
    private Button btRxRetrofitAnidado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpView();
    }

    private void setUpView(){
        btRxRetrofit = findViewById(R.id.btRxRetrofit);
        btRxRetrofit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RxRetrofitActivity.class));
            }
        });

        btRxRetrofitAnidado = findViewById(R.id.btRxRetrofitAnidado);
        btRxRetrofitAnidado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RxRetrofitAnidadoActivity.class));
            }
        });
    }


}
