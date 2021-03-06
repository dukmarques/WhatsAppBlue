package com.example.eduardo.whatsappblue.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.eduardo.whatsappblue.R;
import com.example.eduardo.whatsappblue.helper.UserFirebase;
import com.example.eduardo.whatsappblue.model.Message;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MyViewHolder> {
    private List<Message> messages;
    private Context context;
    private static final int TIPO_SENDER = 0;
    private static final int TIPO_RECIPIENT = 1;

    public MessagesAdapter(List<Message> list, Context c) {
        this.messages = list;
        this.context = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View item = null;
        if (i == TIPO_SENDER){
            item = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_message_sender, viewGroup, false);
        }else if(i == TIPO_RECIPIENT){
            item = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_message_recipient, viewGroup, false);
        }

        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Message message = messages.get(i);
        String msg = message.getMessage();
        String image = message.getImage();

        if (image != null){
            Uri url = Uri.parse(image);
            Glide.with(context).load(url).into(myViewHolder.image);

            String name = message.getName();
            if (!name.isEmpty()){
                myViewHolder.name.setText(name);
            }else{
                myViewHolder.name.setVisibility(View.GONE);
            }

            //hide the text
            myViewHolder.menssage.setVisibility(View.GONE);
        }else{
            myViewHolder.menssage.setText(msg);

            String name = message.getName();
            if (!name.isEmpty()){
                myViewHolder.name.setText(name);
            }else{
                myViewHolder.name.setVisibility(View.GONE);
            }

            //hide the image
            myViewHolder.image.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        String idUser = UserFirebase.getIdUser();

        if (idUser.equals(message.getIdUser())){
            return TIPO_SENDER;
        }
        return TIPO_RECIPIENT;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView menssage;
        ImageView image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.textNameExhibition);
            menssage = itemView.findViewById(R.id.textMessageText);
            image = itemView.findViewById(R.id.imageMessagePhoto);
        }
    }
}