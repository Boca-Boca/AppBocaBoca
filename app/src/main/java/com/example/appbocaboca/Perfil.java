package com.example.appbocaboca;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Perfil extends AppCompatActivity {

    //Firebase authentication
    FirebaseAuth firebaseAuth ;


    //Views/componentes
    TextView perfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Perfil");


        //Instanciando firebase autenticação
        firebaseAuth = FirebaseAuth.getInstance();

            //init views/instanciando/ referenciando componentes

    perfil =findViewById(R.id.Perfil);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private  void checkUserStatus(){
        //Buscando usuario atual
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user!= null){
            //usuário está conectado fique aqui!

            // mudando email de usuario logado
            perfil.setText(user.getEmail());

        }else{
            //usuario nao conectado , ir para atividadeprincipal>Menu
            startActivity(new Intent(Perfil.this, MainActivity.class));
            finish();
        }


    }

    @Override
    protected void onStart() {
        //Verificar no inicio do app < Funções de inicio
        checkUserStatus();
        super.onStart();
    }


    //Menu de opção inflte sla oq e issu

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        //inflating menu
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //get item id
        int id = item.getItemId();

        if(id ==R.id.action_logout){
            firebaseAuth.signOut();
                checkUserStatus();
        }

        return super.onOptionsItemSelected(item);
    }
}



