package com.example.appbocaboca;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;
import java.util.HashMap;

public class AdicionarPost extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseDatabase userDb;
    DatabaseReference userDbRef;


        //CONSTANTES DE Permissao
    private static  final int CAMERA_REQUEST_CODE = 100;
    private static  final int STORAGE_REQUEST_CODE = 200;


    //Constantes de Escolha de imagem
    private  static  final  int IMAGE_PICK_CAMERA_CODE = 300;
    private  static  final  int IMAGE_PICK_GALLERY_CODE = 400;

    Uri  image_select = null;

    //Progress bar
    ProgressDialog pd;

    //LISTAS DE PERMISSAO
    String[] cameraPermissions;
    String[] storagePermissions;

    //views
    EditText titulop , descricaop;
    ImageView imagep;
    Button btnp;

    //User Info
    String name, email, uid, dp;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_post);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Adicionar um novo Post");

        //Ativando botao de voltar  na actionbar
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Iniciar lista de permissoes
    cameraPermissions = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
    storagePermissions  = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


    pd= new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUserStatus();


        actionBar.setSubtitle(email);

        //Pegar informações do usuario logado para incluir no post
        userDbRef = FirebaseDatabase.getInstance().getReference("Users");
            Query query = userDbRef.orderByChild("email").equalTo(email);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){

                    name = "" + ds.child("name").getValue();
                    email   = "" + ds.child("email").getValue();
                    dp = "" + ds.child("image").getValue();

                }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        //Instanciar views
        titulop = findViewById(R.id.pTitleEt);
        descricaop = findViewById(R.id.pDescricao);
        imagep = findViewById(R.id.pImage);
        btnp = findViewById(R.id.postbtn);

    //Pegar imagem da camera /Galeria caso clicado
    imagep.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //Mostrar Dialogo de escolha de imagem
            showImagePickDialog();


        }
    });




    //Listener Button click POST

        btnp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titulo = titulop.getText().toString().trim();
                String descricao = descricaop.getText().toString().trim();


              if(image_select ==null){
                  //Postar sem imagem
                 uploadData(titulo , descricao, "noImage");
              }else{
                  //Postar com imagem
                  uploadData(titulo , descricao, String.valueOf(image_select));

              }
            }
        });

    }

    private void uploadData(String titulo, String descricao, String uri) {

   pd.setMessage("Publicando post...");
   pd.show();

   //Para postar imagem, nome, post-id e hora de publicacao
        String timeStamp= String.valueOf(System.currentTimeMillis());

        String filePathAndName = "Posts/" + "post_" + timeStamp;

        if(!uri.equals("noImage")){
//Postar com imagem

            StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
            ref.putFile(Uri.parse(uri))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //IMAGEM ENVIADA AGORA PEGAR A URl
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful()){

                                String downloadUri = uriTask.getResult().toString();

                                if(uriTask.isSuccessful()){
                                    //url foi recebido
                                    HashMap<Object, String> hashMap = new HashMap<>();

                                    //colocar informações do post
                                    hashMap.put("uid",uid);
                                    hashMap.put("uName",name);
                                    hashMap.put("uEmail",email);
                                    hashMap.put("uDp",dp);
                                    hashMap.put("pID",timeStamp);
                                    hashMap.put("pTitle",titulo);
                                    hashMap.put("pDescri",descricao);
                                    hashMap.put("pImage",downloadUri);
                                    hashMap.put("pTime",timeStamp);

                                    //Local para armazenar post
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                                    //Colocar os dados nesta referencia
                                    ref.child(timeStamp).setValue(hashMap)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {

                                                    pd.dismiss();
                                                    Toast.makeText(AdicionarPost.this, "Post publicado", Toast.LENGTH_SHORT).show();

                                                    //resetar as views
                                                    titulop.setText("");
                                                    descricaop.setText("");
                                                    imagep.setImageURI(null);
                                                    image_select = null;

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    //falha ao adicionar o post no banco

                                                    pd.dismiss();
                                                    Toast.makeText(AdicionarPost.this, "Erro ao publicar "+e.getMessage(), Toast.LENGTH_SHORT).show();

                                                }
                                            });

                                }

                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //FALHA AO FAZER UPLOAD DA IMAGEM
                    pd.dismiss();
                            Toast.makeText(AdicionarPost.this, "Erro ao publicar "+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

        }else{
//Postar sem imagem

            HashMap<Object, String> hashMap = new HashMap<>();

            //colocar informações do post
            hashMap.put("uid",uid);
            hashMap.put("uName",name);
            hashMap.put("uEmail",email);
            hashMap.put("uDp",dp);
            hashMap.put("pID",timeStamp);
            hashMap.put("pTitle",titulo);
            hashMap.put("pDescri",descricao);
            hashMap.put("pImage","noImage");
            hashMap.put("pTime",timeStamp);

            //Local para armazenar post
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
            //Colocar os dados nesta referencia
            ref.child(timeStamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            pd.dismiss();
                            Toast.makeText(AdicionarPost.this, "Post publicado", Toast.LENGTH_SHORT).show();

                            //resetar as views
                            titulop.setText("");
                            descricaop.setText("");
                            imagep.setImageURI(null);
                            image_select = null;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //falha ao adicionar o post no banco

                            pd.dismiss();
                            Toast.makeText(AdicionarPost.this, "Erro ao publicar "+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });



        }


    }

    private void showImagePickDialog() {
    //Opcoes (Camera, Galeria) Para exibir no dialogo
        String[] options = {"Camera", "Galeria"};

        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Escolha uma imagem pela");
        //Colocar opcoes no dialogo
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
        //Lidar com o click da escolha Camera ou Galeria
         if(i==0){
        //Camera clicada
         //Checar permissoes primeiro

             if(!checkCameraPermissions()){
                 requestSCameraPermission();
             }else{
                 pickFromCamera();
             }
         }

         if (i==1){
        //Galeria clicada
             if(!checkStoragePermissions()){
                 requestStoragePermission();
             }else{
                 pickFromGallery();
             }

         }

            }
        });

        //Criar e mostrar dialogo
        builder.create().show();

    }

    private void pickFromGallery() {
    Intent intent = new Intent(Intent.ACTION_PICK);
    intent.setType("image/*");
    startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);

    }

    private void pickFromCamera() {
    //Pegar imagem pela camera
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE, "Temp Pick");
        cv.put(MediaStore.Images.Media.DESCRIPTION, "Temp Descr");
         image_select = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);



        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_select);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);

    }


    private  boolean checkStoragePermissions(){
    //Checar se a permissao para armazenamento esta ativa e retorna um true ou false

  boolean result = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==
          (PackageManager.PERMISSION_GRANTED);

  return  result;
    }


    private void requestStoragePermission(){

        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);

    }



    private  boolean checkCameraPermissions(){
        //Checar se a permissao para camera  e retorna um true ou false

        boolean result = ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)==
                (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==
                (PackageManager.PERMISSION_GRANTED);


        return  result && result1;
    }


    private void requestSCameraPermission(){

        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);

    }





    @Override
    protected void onStart() {
        super.onStart();
        checkUserStatus();
    }

    @Override
    protected void onResume() {
      checkUserStatus();
      super.onResume();
    }

    private void checkUserStatus() {
        //Buscando usuario atual
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            //usuário está conectado fique aqui!
       email= user.getEmail();
       uid  = user.getUid();
            // mudando email de usuario logado

            //perfil.setText(user.getEmail());


        } else {
            //usuario nao conectado , ir para atividadeprincipal>Menu
            startActivity(new Intent(this, MainActivity.class));
             finish();
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();//ir para atividade anterior
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main,menu);
        menu.findItem(R.id.action_add_post).setVisible(false);
        menu.findItem(R.id.action_search).setVisible(false);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
        public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if(id ==R.id.action_logout){
            firebaseAuth.signOut();
            checkUserStatus();
        }
            return super.onOptionsItemSelected(item);
        }


        //Lidando com os resultados da pesquisa
    //METODO ATIVADO QUANDO O USUARIO CLICA EM PERMITIR OU NEGAR O ACESSO DA CAMERA
        @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    switch (requestCode){
        case CAMERA_REQUEST_CODE:{
            if(grantResults.length>0){
                boolean cameraAceita = grantResults[0]== PackageManager.PERMISSION_GRANTED;
                boolean galeriaAceita = grantResults[1]== PackageManager.PERMISSION_GRANTED;
                    if(cameraAceita && galeriaAceita){
                            pickFromCamera();
                         }else{

                        Toast.makeText(this, "As permissões para a câmera e galeria são necessárias",Toast.LENGTH_SHORT).show();
                               }




            }else{
            }

        }break;

        case STORAGE_REQUEST_CODE:{

            if(grantResults.length>0){
                boolean galeriaAceita = grantResults[0]== PackageManager.PERMISSION_GRANTED;
                if(galeriaAceita ){
                     //Permissao da galeria aceita
                    pickFromGallery();
                }else{

                    Toast.makeText(this, "A permissão para a galeria é necessária",Toast.LENGTH_SHORT).show();
                }

            }else{
            }



        }break;
    }

    }


    //Method Chamado apos a escolha de uma imagem da camera ou galeria
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode ==IMAGE_PICK_GALLERY_CODE){
            image_select = data.getData();

            //Mudar o IMAGEVIEW

                imagep.setImageURI(image_select);

            }
            else if(requestCode ==IMAGE_PICK_CAMERA_CODE){
                imagep.setImageURI(image_select);

            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
