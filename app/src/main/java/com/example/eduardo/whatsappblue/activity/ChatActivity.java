package com.example.eduardo.whatsappblue.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.eduardo.whatsappblue.R;
import com.example.eduardo.whatsappblue.adapter.MessagesAdapter;
import com.example.eduardo.whatsappblue.config.ConfigurationFirebase;
import com.example.eduardo.whatsappblue.helper.Base64Custom;
import com.example.eduardo.whatsappblue.helper.UserFirebase;
import com.example.eduardo.whatsappblue.model.Conversation;
import com.example.eduardo.whatsappblue.model.Message;
import com.example.eduardo.whatsappblue.model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private TextView textViewNome;
    private CircleImageView circleImageViewPhoto;
    private EditText editMessage;
    private ImageView imageCamera;
    private ImageView imagePhoto;
    private User recipientUser;
    private DatabaseReference database;
    private StorageReference storage;
    private DatabaseReference messagesRef;
    private ChildEventListener childEventListenerMessages;

    //Sender and recipient user identifier
    private String idUserRecipient;
    private String idUserSender;

    private RecyclerView recyclerMessages;
    private MessagesAdapter adapter;
    private List<Message> messagesList = new ArrayList<>();

    //Constants
    private static final int SELECAO_CAMERA = 100;
    private static final int SELECAO_GALLERY = 200;

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
        recyclerMessages = findViewById(R.id.recyclerMessages);
        imageCamera = findViewById(R.id.imageCamera);
        imagePhoto = findViewById(R.id.imagePhoto);

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

        //Configuring adapter
        adapter = new MessagesAdapter(messagesList, getApplicationContext());

        //Configuring recyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerMessages.setLayoutManager(layoutManager);
        recyclerMessages.setHasFixedSize(true);
        recyclerMessages.setAdapter(adapter);

        database = ConfigurationFirebase.getFirebaseDatabase();
        storage = ConfigurationFirebase.getFirebaseStorage();
        messagesRef = database.child("mensagens")
                .child(idUserSender)
                .child(idUserRecipient);

        //Click event on the camera
        imageCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, SELECAO_CAMERA);
                }
            }
        });

        //Click event on the photo gallery
        imagePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, SELECAO_GALLERY);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bitmap image = null;

            try {
                switch (requestCode) {
                    case SELECAO_CAMERA:
                        image = (Bitmap) data.getExtras().get("data");
                        break;
                    case SELECAO_GALLERY:
                        Uri locateImageSelected = data.getData();
                        image = MediaStore.Images.Media.getBitmap(getContentResolver(), locateImageSelected);
                        break;
                }

                if (image != null){

                    //Recovering data image
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dataImage = baos.toByteArray();

                    //Create image name
                    String nameImage = UUID.randomUUID().toString();

                    //Configuring firebase reference
                    final StorageReference imageRef = storage.child("imagens")
                            .child("fotos")
                            .child(idUserSender)
                            .child(nameImage);

                    UploadTask uploadTask = imageRef.putBytes(dataImage);
                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            // Continue with the task to get the download URL
                            return imageRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri url = task.getResult();
                                String downloadUrl = url.toString();

                                Message message = new Message();
                                message.setIdUser(idUserSender);
                                message.setMessage("imagem.jpeg");
                                message.setImage(downloadUrl);

                                //Save message to sender
                                saveMessage(idUserSender, idUserRecipient, message);
                                //Save message to recipient
                                saveMessage(idUserRecipient, idUserSender, message);

                                Toast.makeText(ChatActivity.this, "Sucesso ao enviar imagem", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(ChatActivity.this,"Erro ao enviar imagem", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }catch (Exception e){
                e.printStackTrace();
            }
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

            //save message from recipient
            saveMessage(idUserRecipient, idUserSender, message);

            //Save conversation
            saveConversation(message);
        }else{
            Toast.makeText(this, "Digite uma mensagem para enviar", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveConversation(Message msg){
        Conversation conversationSender = new Conversation();
        conversationSender.setIdSender(idUserSender);
        conversationSender.setIdRecipient(idUserSender);
        conversationSender.setLastMessage(msg.getMessage());
        conversationSender.setUserExhibition(recipientUser);

        conversationSender.save();
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

    @Override
    protected void onStart() {
        super.onStart();
        recoverMessages();
    }

    @Override
    protected void onStop() {
        super.onStop();
        messagesRef.removeEventListener(childEventListenerMessages);
    }

    private void recoverMessages(){
        //clear messages list
        messagesList.clear();

        childEventListenerMessages = messagesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Message message = dataSnapshot.getValue(Message.class);
                messagesList.add(message);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
