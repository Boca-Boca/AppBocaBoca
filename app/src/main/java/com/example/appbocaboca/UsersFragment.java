package com.example.appbocaboca;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import okhttp3.internal.cache.DiskLruCache;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UsersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UsersFragment extends Fragment {

    RecyclerView recyclerView;
    AdapterUser adapterUser;
    List<ModelUsers>usersList;

    //Firebase authentication
    FirebaseAuth firebaseAuth;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UsersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UsersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UsersFragment newInstance(String param1, String param2) {
        UsersFragment fragment = new UsersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate( @Nullable Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);//Mostrar Menu opções no fragmento

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        //Instanciando firebase autenticação
        firebaseAuth = FirebaseAuth.getInstance();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        //iniciar o recyclerview
        recyclerView = view.findViewById(R.id.users_recyclerView);

        //defina suas propriedades
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));



        //Instanciar lista de usuarios userlis*
        usersList = new ArrayList<>();

        //Pegar todos os usuarios
        getAllUsers();


        return view;
    }

    private void getAllUsers() {
        //Pegar usuario autenticado
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();

        //Pegar o caminho do banco chamado "Usuarios - Users" contendo as informações dos usuarios

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");

        //Pegar todos os dados do caminho
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    ModelUsers modelUser = ds.getValue(ModelUsers.class);

                    //Pegar todos os usuarios exeto o usuario logado
                    if(!modelUser.getUid().equals(fUser.getUid())){
                        usersList.add(modelUser);
                    }

                    //Adapter
                    adapterUser = new AdapterUser(getActivity(),usersList);

                    //mudar adapter

                    recyclerView.setAdapter(adapterUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    private void searchUsers(String query) {
        //Pegar usuario autenticado
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();

        //Pegar o caminho do banco chamado "Usuarios - Users" contendo as informações dos usuarios

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");

        //Pegar todos os dados do caminho
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    ModelUsers modelUser = ds.getValue(ModelUsers.class);

                    //Pegar todos os usuarios pesquisados exeto o usuario logado
                    if(!modelUser.getUid().equals(fUser.getUid())){


                        if(modelUser.getNome().toLowerCase().contains(query.toLowerCase())|| modelUser.getEmail().toLowerCase()
                                .contains(query.toLowerCase())){

                            usersList.add(modelUser);


                        }


                    }

                    //Adapter
                    adapterUser = new AdapterUser(getActivity(),usersList);

                    //Atualizar adapter
                    adapterUser.notifyDataSetChanged();


                    //mudar adapter

                    recyclerView.setAdapter(adapterUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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


menu.findItem(R.id.action_add_post).setVisible(false);


        inflater.inflate(R.menu.menu_main, menu);

        //View de Pesquisa  SearchView
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        super.onCreateOptionsMenu(menu, inflater);


        //search listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //chamado assim que o usuario pressionar uma letra
                // se a barra de pesquisa não estiver vazia então pesquisar
                if(!TextUtils.isEmpty(query.trim())){
                    //a barra contem texto então pesquisa-lo

                    searchUsers(query);
                }else{
                    //Se tiver vazio pegar todos os usuarios
                    getAllUsers();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                //chamado assim que o usuario pressionar uma letra

                // se a barra de pesquisa não estiver vazia então pesquisar
                if(!TextUtils.isEmpty(query.trim())){
                    //a barra contem texto então pesquisa-lo

                    searchUsers(query);
                }else{
                    //Se tiver vazio pegar todos os usuarios
                    getAllUsers();
                }
                return false;
            }
        });

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


