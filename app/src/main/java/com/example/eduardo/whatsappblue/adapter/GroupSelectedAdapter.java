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

public class GroupSelectedAdapter extends RecyclerView.Adapter<GroupSelectedAdapter.MyViewHolder> {
    private List<User> selectedContacts;
    private Context context;

    public GroupSelectedAdapter(List<User> contactsList, Context c) {
        this.selectedContacts = contactsList;
        this.context = c;
    }

    @NonNull
    @Override
    public GroupSelectedAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemList = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_group_selected, viewGroup, false);
        return new GroupSelectedAdapter.MyViewHolder(itemList);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupSelectedAdapter.MyViewHolder myViewHolder, int i) {
        User user = selectedContacts.get(i);

        myViewHolder.name.setText(user.getName());

        if (user.getPhoto() != null){
            Uri uri = Uri.parse(user.getPhoto());
            Glide.with(context).load(uri).into(myViewHolder.photo);
        }else{
            myViewHolder.photo.setImageResource(R.drawable.padrao);
        }
    }

    @Override
    public int getItemCount() {
        return selectedContacts.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        CircleImageView photo;
        TextView name;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            photo = itemView.findViewById(R.id.imageViewPhotoSelectedMember);
            name = itemView.findViewById(R.id.textNameSelectedMember);
        }
    }
}