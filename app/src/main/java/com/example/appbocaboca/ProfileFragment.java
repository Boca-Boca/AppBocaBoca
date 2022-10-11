package com.example.appbocaboca;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {


    //FIREBASEAUTH
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    // Componentes views xml
    ImageView avatar;
    TextView nameT, emailT, telefoneT;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate( @Nullable Bundle savedInstanceState) {

        setHasOptionsMenu(true);//Mostrar Menu opções no fragmento
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        //iniciar Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth .getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        // Iniciar as views
        avatar = view.findViewById(R.id.avataricon);
        emailT = view.findViewById(R.id.emailTv);
        telefoneT = view.findViewById(R.id.telefoneTv);
        nameT = view.findViewById(R.id.nameTv);


        /*Nos temos que conseguir informações do usuario logado.
         * Conseguimos isso usando o email ou o uid, aqui nos usaremos o email
         * Usando a query orderByChild nos vamos mostrar os detalhes de um nó o
         * qual a chave chamada email tem valor igual ao email logado.
         * Vai procurar todos os nós , onde a chave combina combina vai pegar os detalhes*/

        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //checar até obter os dados necessários
                for(DataSnapshot ds: snapshot.getChildren()){
                    //get data
                    String name =""+ ds.child("nome").getValue();
                    String email =""+ ds.child("email").getValue();
                    String telefone =""+ ds.child("telefone").getValue();
                    String imagem =""+ ds.child("imagem").getValue();
                    //set data

                    nameT.setText("name");
                    emailT.setText("email");
                    telefoneT.setText("telefone");
                    try {
                        //Se a imagem for recebida entao mudar
                        Picasso.get().load(imagem).into(avatar);

                    }catch (Exception e){

                        //Se tiver uma exception enquanto conseguir a  imagem entao definir uma imagem default
                        Picasso.get().load(R.drawable.ic_add_image).into(avatar);

                    }


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }





    private void checkUserStatus() {
        //Buscando usuario atual
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            //usuário está conectado fique aqui!

            // mudando email de usuario logado

            //perfil.setText(user.getEmail());


        } else {
            //usuario nao conectado , ir para atividadeprincipal>Menu
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }


    }


    //Menu de opção inflte sla oq e issu

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        //inflating menu
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //get item id
        int id = item.getItemId();

        if(id ==R.id.action_logout){
            firebaseAuth.signOut();
            checkUserStatus();
        }if(id ==R.id.action_add_post){
         startActivity(new Intent(getActivity(), AdicionarPost.class));
        }

        return super.onOptionsItemSelected(item);
    }

}