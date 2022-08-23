package com.example.appbocaboca;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
public class MainActivity extends AppCompatActivity {

        //views e/ou componentes
    Button btnLogin, btnCadastro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Iniciar/Referenciar componentes
        btnCadastro = findViewById(R.id.btnCadastro);
        btnLogin = findViewById(R.id.btnLogin);

        //Lidando com evento de click no botao Cadastro//
     btnCadastro.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    //Se clicado no botao iniciar atividade de Cadastro na classe cadastro
                    startActivity(new Intent(MainActivity.this, Cadastro.class));
                }


     });

     //Lidando com o click no botao Login

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                startActivity(new Intent(MainActivity.this, Login.class));

            }
        });

    }
}