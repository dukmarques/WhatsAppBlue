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
import com.example.eduardo.whatsappblue.model.Conversation;
import com.example.eduardo.whatsappblue.model.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversationsAdapter extends RecyclerView.Adapter<ConversationsAdapter.MyViewHolder> {
    private List<Conversation> conversations;
    private Context context;

    public ConversationsAdapter(List<Conversation> list, Context c) {
        this.conversations = list;
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
        Conversation conversation = conversations.get(i);
        myViewHolder.lastMessage.setText(conversation.getLastMessage());

        User user = conversation.getUserExhibition();
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
        return conversations.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        CircleImageView photo;
        TextView name, lastMessage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            photo = itemView.findViewById(R.id.imageViewPhotoContact);
            name = itemView.findViewById(R.id.textNameContact);
            lastMessage = itemView.findViewById(R.id.textEmailContact);
        }
    }
}
