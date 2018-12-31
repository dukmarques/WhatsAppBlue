package com.example.eduardo.whatsappblue.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.eduardo.whatsappblue.R;
import com.example.eduardo.whatsappblue.model.User;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private TextView textViewNome;
    private CircleImageView circleImageViewPhoto;
    private User recipientUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //Config toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Initial settings
        textViewNome = findViewById(R.id.textViewNameChat);
        circleImageViewPhoto = findViewById(R.id.circleImagePhotoChat);

        //Recover user data from recipient
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            recipientUser = (User) bundle.getSerializable("contactChat");
            textViewNome.setText(recipientUser.getName());

            String photo = recipientUser.getPhoto();
            if (photo != null){
                Uri url = Uri.parse(recipientUser.getPhoto());
                Glide.with(ChatActivity.this)
                        .load(url)
                        .into(circleImageViewPhoto);
            }else{
                circleImageViewPhoto.setImageResource(R.drawable.padrao);
            }
        }
    }

}
