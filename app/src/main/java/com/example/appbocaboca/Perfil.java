package com.example.appbocaboca;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Perfil extends AppCompatActivity {

    //Firebase authentication
    FirebaseAuth firebaseAuth ;
    ActionBar actionBar;

    //Views/componentes
    TextView perfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

         actionBar = getSupportActionBar();
        actionBar.setTitle("Perfil");


        //Instanciando firebase autenticação
        firebaseAuth = FirebaseAuth.getInstance();

            //init views/instanciando/ referenciando componentes

        //botao de navegacao
        BottomNavigationView navegationView = findViewById(R.id.navigation);
            navegationView.setOnNavigationItemSelectedListener(selectedListener);


            //Transação do Botao HOME como DEFAULT
        actionBar.setTitle("Home");
        HomeFragment fragment1 = new HomeFragment();
        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.content,fragment1,"");
        ft1.commit();

    }

private  BottomNavigationView.OnNavigationItemSelectedListener selectedListener  = new BottomNavigationView.OnNavigationItemSelectedListener() {
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        //Lidando com o click do item no menu

        switch (menuItem.getItemId()){
            case R.id.home:
                actionBar.setTitle("Home");
                HomeFragment fragment1 = new HomeFragment();
                FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                ft1.replace(R.id.content,fragment1,"");
                ft1.commit();
                return true;
            case R.id.users:
                actionBar.setTitle("Amigos");
                UsersFragment fragment3 = new UsersFragment();
                FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
                ft3.replace(R.id.content,fragment3,"");
                ft3.commit();
                return true;

            case R.id.profile:
                actionBar.setTitle("Perfil");
                ProfileFragment fragment2 = new ProfileFragment();
                FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                ft2.replace(R.id.content,fragment2,"");
                ft2.commit();
                return true;

            case R.id.event:
                actionBar.setTitle("Eventos");
                EventFragment fragment4 = new EventFragment();
                FragmentTransaction ft4 = getSupportFragmentManager().beginTransaction();
                ft4.replace(R.id.content,fragment4,"");
                ft4.commit();
                return true;
        }


        return false;
    }
};


    private  void checkUserStatus(){
        //Buscando usuario atual
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user!= null){
            //usuário está conectado fique aqui!

            // mudando email de usuario logado

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



