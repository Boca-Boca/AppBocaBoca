package com.example.appbocaboca;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
//Sempre declara o componente primeiro depois referencia/intancia
    //Views/Componentes
    EditText Email, Senha;
    TextView naotemconta;
     Button loginbtn;
     //Declarando instancia do Firebase
    private FirebaseAuth mAuth;


    //Progress dialog pra mostrar mensagens
ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle("Entrar");


        //Inicializando uma instancia do Firebase
        mAuth = FirebaseAuth.getInstance();

        //Habilitar botao de voltar
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        Email = findViewById(R.id.emailEt);
        Senha = findViewById(R.id.senhaEt);
        naotemconta= findViewById((R.id.nao_tem_conta));
        loginbtn = findViewById(R.id.btnlogin);


        //Clique do botao Login
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //input Data

                String email = Email.getText().toString();
                String senha = Senha.getText().toString().trim();
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    // Lançar erro email inválido
                    Email.setError("Email inválido");
                    Email.setFocusable(true);
                }
                else{
                    //Email valido tudo certo
                    loginUser (email,senha);
                }
            }
        });

        //Nao tem uma conta textview click
        naotemconta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            startActivity(new Intent(Login.this,Cadastro.class));
            }
        });
    }

    private void loginUser(String email, String senha) {
    //Mostrar progress dialog
        mAuth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            //Fechar barra de progesso
                            pd.dismiss();


                            //Logar o usuário foi um sucesso Atualizar o UI- User interface com as informções do usuário
                            FirebaseUser user = mAuth.getCurrentUser();

                            //Usuario logado então iniciar atividade
                            startActivity(new Intent(Login.this,Perfil.class));
                            finish();

                        }else{
                            //Fechar barra de progesso
                             pd.dismiss();

                             //Se falhar mandar uma mensagem para o usuário
                            Toast.makeText(Login.this, "Autenticação falhou",
                                    Toast.LENGTH_SHORT).show();

                        }


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Fechar barra de progesso
                        pd.dismiss();
                        //Se der algum erro  usar um get e mostrar mensagem de erro
                        Toast.makeText(Login.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });


        //Iniciar/Instanciar Progress dialog
   pd= new ProgressDialog(this);
   pd.setMessage("Entrando...");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); //Ir para atividade anterior
        return super.onSupportNavigateUp();
    }


}

