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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Login extends AppCompatActivity {
//Sempre declara o componente primeiro depois referencia/intancia
    //Views/Componentes
    EditText Email, Senha;
    TextView naotemconta;
     Button loginbtn;
    SignInButton mGoogleLoginBtn;


    private static final int RC_SIGN_IN = 100 ;
    GoogleSignInClient mGoogleSignInClient;



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



            // Configuração do google sign in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        //Inicializando uma instancia do Firebase
        mAuth = FirebaseAuth.getInstance();

        //Habilitar botao de voltar
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        Email = findViewById(R.id.emailEt);
        Senha = findViewById(R.id.senhaEt);
        naotemconta= findViewById((R.id.nao_tem_conta));
        loginbtn = findViewById(R.id.btnlogin);
        mGoogleLoginBtn = findViewById(R.id.googleLoginBtn);



        // Lidando com o botão do google
        mGoogleLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent,RC_SIGN_IN);
            }
        });


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


    @Override
    public void onActivityResult(int requestCode, int resultCode , Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode==RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
            try{
                GoogleSignInAccount conta = task.getResult(ApiException.class);
                firebaseAuthWithGoogle (conta);

            }catch (ApiException exception){
                Toast.makeText(Login.this, "Nenhum usuário Google logado no aparelho.",
                        Toast.LENGTH_SHORT).show();

            }
        }

    }




    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        //here the acct.getIdToken() is null

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {

                            FirebaseUser user = mAuth.getCurrentUser();
                            String email = user.getEmail();

                            String uid = user.getUid();

                            //Quando o usuario e registrado armazenar as info no firebase realtime database tambem
                            //Usando Hashmap
                            HashMap<Object, String> hashMap= new HashMap<>();
                            //Colocando informacoes no hashmap
                            hashMap.put("email", email);
                            hashMap.put("uid", uid);
                            hashMap.put("nome","" );   //vai adicionar depois
                            hashMap.put("telefone", "");//vai adicionar depois
                            hashMap.put("imagem", "");//vai adicionar depois


                            //Intancia do Firebase database

                            FirebaseDatabase database = FirebaseDatabase.getInstance();

                            //Caminho pro armazenamento de dados do usuario chamado "Users"

                            DatabaseReference reference = database.getReference("Users");


                            //colocar os dados no hashmap e no banco(database)
                            reference.child(uid).setValue(hashMap);




                            Toast.makeText(Login.this, "Cadastrado" + user.getEmail(), Toast.LENGTH_SHORT).show();
                            // Vai para o perfil ativo depois de logar
                            startActivity(new Intent(Login.this, Perfil.class));
                            finish();
                            // updateUI(user);
                        } else {
                            Toast.makeText(Login.this, "Erro no Login", Toast.LENGTH_SHORT).show();
                        }


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Mostrar erro de mensagem
                        Toast.makeText(Login.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

