package com.example.eduardo.whatsappblue.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.eduardo.whatsappblue.R;
import com.example.eduardo.whatsappblue.config.ConfigurationFirebase;
import com.example.eduardo.whatsappblue.helper.Base64Custom;
import com.example.eduardo.whatsappblue.helper.UserFirebase;
import com.example.eduardo.whatsappblue.model.Message;
import com.example.eduardo.whatsappblue.model.User;
import com.google.firebase.database.DatabaseReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private TextView textViewNome;
    private CircleImageView circleImageViewPhoto;
    private EditText editMessage;
    private User recipientUser;

    //Sender and recipient user identifier
    private String idUserRecipient;
    private String idUserSender;

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
        editMessage = findViewById(R.id.editMessage);

        //Recover user data from sender
        idUserSender = UserFirebase.getIdUser();

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

            //recover user data from recipient
            idUserRecipient = Base64Custom.encodingBase64(recipientUser.getEmail());
        }
    }

    public void sendMessage(View view){
        String textMessage = editMessage.getText().toString();
        if (!textMessage.isEmpty()){
            Message message = new Message();
            message.setIdUser(idUserSender);
            message.setMessage(textMessage);

            //save message from sender
            saveMessage(idUserSender, idUserRecipient, message);
        }else{
            Toast.makeText(this, "Digite uma mensagem para enviar", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveMessage(String idSender, String idRecipient, Message msg){
        DatabaseReference database = ConfigurationFirebase.getFirebaseDatabase();
        DatabaseReference messageRef = database.child("mensagens");

        messageRef.child(idSender)
                .child(idRecipient)
                .push()
                .setValue(msg);

        //clear text
        editMessage.setText("");
    }
}
