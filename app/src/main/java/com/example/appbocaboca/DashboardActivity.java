package com.example.appbocaboca;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity<selectedListener> extends AppCompatActivity {

    //Firebase authentication
    FirebaseAuth firebaseAuth ;
    ActionBar actionBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Perfil");


        //Instanciando firebase autenticação
        firebaseAuth = FirebaseAuth.getInstance();
        // bottom navigation
        BottomNavigationView navigationView =findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);
        // Home fragment transaction(default on star )
        actionBar.setTitle("Home");
        HomeFragment fragment1 = new HomeFragment();
        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.content, fragment1,"");
        ft1.commit();

    }
    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener =
             new BottomNavigationView.OnNavigationItemSelectedListener() {
                 @Override
                 public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                         //handle item Clicks
                         switch (menuItem.getItemId()){
                             case R.id.home:
                                 // Home fragment transaction
                                 actionBar.setTitle("Home");
                                 HomeFragment fragment1 = new HomeFragment();
                                 FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                                 ft1.replace(R.id.content,fragment1,"");
                                 ft1.commit();

                                 return true;
                             case R.id.profile:
                                 // Profile fragment transaction
                                 actionBar.setTitle("Profile");
                                 ProfileFragment fragment2 = new ProfileFragment();
                                 FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                                 ft2.replace(R.id.content, fragment2,"");
                                 ft2.commit();
                                 return true;
                             case R.id.users:
                                 // Users fragment transaction
                                 actionBar.setTitle("Users");
                                 UsersFragment fragment3 = new UsersFragment();
                                 FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
                                 ft3.replace(R.id.content, fragment3,"");
                                 ft3.commit();
                                 return true;
                             case R.id.event:
                                 // Events fragment transaction
                                 actionBar.setTitle("Evento");
                                 EventFragment fragment4 = new EventFragment();
                                 FragmentTransaction ft4 = getSupportFragmentManager().beginTransaction();
                                 ft4.replace(R.id.content, fragment4,"");
                                 ft4.commit();
                                 return true;
                         }
                     return false;
                 }
             };

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

            //perfil.setText(user.getEmail());


        }else{
            //usuario nao conectado , ir para atividadeprincipal>Menu
            startActivity(new Intent(DashboardActivity.this, MainActivity.class));
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



