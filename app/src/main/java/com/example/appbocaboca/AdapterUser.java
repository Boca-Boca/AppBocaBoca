package com.example.appbocaboca;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterUser extends RecyclerView.Adapter<AdapterUser.MyHolder>{
    Context context;
    List<ModelUsers>usersList;

    //construtor


    public AdapterUser(Context context, List<ModelUsers> usersList) {
        this.context = context;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        //inflate menu layout (row_user.xml)
        View view = LayoutInflater.from(context).inflate(R.layout.row_users,parent,false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int i) {
// Conseguir informações

        String userImage = usersList.get(i).getImagem();
        String userName = usersList.get(i).getNome();
        String userEmail = usersList.get(i).getEmail();

// definir dados
    holder.NomeTv.setText(userName);
    holder.EmailTv.setText(userEmail);
try {

    Picasso.get().load(userImage).
            placeholder(R.drawable.ic_default_img).
            into(holder.Avatartv);

}  catch (Exception e){


}


//Lidando com o click do item
     holder.itemView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {


             Toast.makeText(context,""+userEmail,Toast.LENGTH_SHORT).show();
         }
     }) ;


    }





    @Override
    public int getItemCount() {
        return usersList.size();
    }


    //view holder class BASICAMENTE E AQUI QUE E FEITA A LOGICA DE UMA
    //Listagem de de usuarios em uma lista que e exibida
    //No caso essa lista é a lista de usuarios
 //Se quiserem ver mais https://www.youtube.com/watch?v=4-hK6qZv56U&ab_channel=ProgrammingwithProfessorSluiter

    class  MyHolder extends RecyclerView.ViewHolder{


        ImageView Avatartv;
        TextView NomeTv , EmailTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);


            //instanciar as Views
        Avatartv = itemView.findViewById(R.id.avatarIv);
        NomeTv = itemView.findViewById(R.id.nameT);
        EmailTv = itemView.findViewById(R.id.emailT);



        }
    }


}
