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

public class Cadastro extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100 ;
    GoogleSignInClient mGoogleSignInClient;

    //Views/Componentes para apos Instancia-los
    EditText Email, Senha ;
    Button Cadastrar;
    TextView tem_conta;
    SignInButton mGoogleLoginBtn;

    //Mostrar Barra de progresso ao cadastrar usuario
      ProgressDialog progressDialog;

      private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        //ActionBar
        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle("Criar uma Conta");

        //Habilitar botao de voltar
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        // Antes do mAuth
        // Configuração do google sign in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
        mAuth = FirebaseAuth.getInstance();


        //Instanciando email, senha , botao cadastrar ,barra de progresso e Jatemconta
        Email = findViewById(R.id.emailEt);
        Senha = findViewById(R.id.senhaEt);
        Cadastrar = findViewById(R.id.btnCadastrar);
        tem_conta = findViewById(R.id.tem_conta);
        mGoogleLoginBtn = findViewById(R.id.googleLoginBtn);


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cadastrando usuário...");

        //Lidando com o click do botao cadastrar

        Cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // input email, senha
                String email = Email.getText().toString().trim();
                String senha = Senha.getText().toString().trim();
                // Validando
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    //Lança um erro e da um foco no input para verificar o email
                    Email.setError("Email inválido");
                    Email.setFocusable(true);
                }
                else if(senha.length()<6){
                    Senha.setError("A senha deve conter no minímo 6 chacarteres  ");
                    Senha.setFocusable(true);
                }else{
                    registerUser(email,senha);
                }
            }

        });
        //Lidando se o usuario ja tiver conta no cadastro
        tem_conta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(Cadastro.this,Login.class ));
               finish();
            }
        });
    // Lidando com o botão do google
        mGoogleLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent,RC_SIGN_IN);
            }
        });
    }

    private void registerUser( String email,  String senha) {

        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email,senha)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                         progressDialog.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();

                            //Pegar o email do usuario e o Uid pela autenticacao

                        String email = user.getEmail();

                        String uid = user.getUid();

                        //Quando o usuario e registrado armazenar as info no firebase realtime database tambem
                            //Usando Hashmap
                            HashMap<Object, String> hashMap= new HashMap<>();
                            //Colocando informacoes no hashmap
                            hashMap.put("email", email);
                            hashMap.put("uid", uid);
                            hashMap.put("nome","" );   //vai adicionar depois (e.g edit profile)
                            hashMap.put("telefone", "");//vai adicionar depois (e.g edit profile)
                            hashMap.put("imagem", "");//vai adicionar depois (e.g edit profile)

                            //Intancia do Firebase database

                            FirebaseDatabase database = FirebaseDatabase.getInstance();

                            //Caminho pro armazenamento de dados do usuario chamado "Users"

                            DatabaseReference reference = database.getReference("Users");

                            //colocar os dados no hashmap e no banco(database)
                            reference.child(uid).setValue(hashMap);



                            Toast.makeText(Cadastro.this, "Cadastrado...\n"+user.getEmail(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Cadastro.this, DashboardActivity.class));
                            finish();
                        } else{
                            progressDialog.dismiss();

                        Toast.makeText(Cadastro.this, "Authentication failed.",

                                Toast.LENGTH_SHORT).show();
                        }
                    }




                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(Cadastro.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });



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
                Toast.makeText(Cadastro.this, "Nenhum usuário Google logado no aparelho.",
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
                            Toast.makeText(Cadastro.this, "Cadastrado"+user.getEmail(), Toast.LENGTH_SHORT).show();
                            // Vai para o perfil ativo depois de logar
                            startActivity(new Intent(Cadastro.this, DashboardActivity.class));
                            finish();
                           // updateUI(user);
                        }else{
                            Toast.makeText(Cadastro.this, "Erro no Login", Toast.LENGTH_SHORT).show();
                        }


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Mostrar erro de mensagem
                        Toast.makeText(Cadastro.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
}


