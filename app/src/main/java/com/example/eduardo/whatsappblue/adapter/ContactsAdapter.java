package com.example.eduardo.whatsappblue.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.eduardo.whatsappblue.R;
import com.example.eduardo.whatsappblue.model.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> {
    private List<User> contacts;
    private Context context;

    public ContactsAdapter(List<User> contactsList, Context c) {
        this.contacts = contactsList;
        this.context = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemList = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_contacts, viewGroup, false);
        return new MyViewHolder(itemList);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        User user = contacts.get(i);
        boolean header = user.getEmail().isEmpty();

        myViewHolder.name.setText(user.getName());
        myViewHolder.email.setText(user.getEmail());

        if (user.getPhoto() != null){
            Uri uri = Uri.parse(user.getPhoto());
            Glide.with(context).load(uri).into(myViewHolder.photo);
        }else{
            if (header){
                myViewHolder.photo.setImageResource(R.drawable.icone_grupo);
                myViewHolder.email.setVisibility(View.GONE);
            }else {
                myViewHolder.photo.setImageResource(R.drawable.padrao);
            }
        }
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        CircleImageView photo;
        TextView name, email;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            photo = itemView.findViewById(R.id.imageViewPhotoContact);
            name = itemView.findViewById(R.id.textNameContact);
            email = itemView.findViewById(R.id.textEmailContact);
        }
    }
}