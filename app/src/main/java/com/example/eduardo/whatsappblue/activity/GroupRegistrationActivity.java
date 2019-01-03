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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eduardo.whatsappblue.R;
import com.example.eduardo.whatsappblue.adapter.GroupSelectedAdapter;
import com.example.eduardo.whatsappblue.config.ConfigurationFirebase;
import com.example.eduardo.whatsappblue.helper.UserFirebase;
import com.example.eduardo.whatsappblue.model.Group;
import com.example.eduardo.whatsappblue.model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupRegistrationActivity extends AppCompatActivity {
    private static final int SELECAO_GALLERY = 200;
    private List<User> selectedMembersList = new ArrayList<>();
    private TextView textTotalParticipants;
    private GroupSelectedAdapter groupSelectedAdapter;
    private RecyclerView recyclerSelectedMembers;
    private CircleImageView imageGroup;
    private StorageReference storageReference;
    private Group group;
    private FloatingActionButton fabSaveGroup;
    private EditText editNameGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_registration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Novo grupo");
        toolbar.setSubtitle("Defina o nome");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Initial settings
        textTotalParticipants = findViewById(R.id.textTotalParticipants);
        recyclerSelectedMembers = findViewById(R.id.recyclerMembersGroup);
        imageGroup = findViewById(R.id.imageGroup);
        storageReference = ConfigurationFirebase.getFirebaseStorage();
        fabSaveGroup = findViewById(R.id.fabSaveGroup);
        editNameGroup = findViewById(R.id.editNameGroup);
        group = new Group();

        //Configure event click
        imageGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, SELECAO_GALLERY);
                }
            }
        });

        //Retrieve past member list
        if (getIntent().getExtras() != null){
            List<User> members = (List<User>) getIntent().getExtras().getSerializable("members");
            selectedMembersList.addAll(members);

            textTotalParticipants.setText("Participantes: " + selectedMembersList.size());

        }

        //Configure adapter
        groupSelectedAdapter = new GroupSelectedAdapter(selectedMembersList, getApplicationContext());

        //Configure recyclerView
        RecyclerView.LayoutManager layoutManagerHorizontal = new LinearLayoutManager(
                getApplicationContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );
        recyclerSelectedMembers.setLayoutManager(layoutManagerHorizontal);
        recyclerSelectedMembers.setHasFixedSize(true);
        recyclerSelectedMembers.setAdapter(groupSelectedAdapter);

        //Configure floating action button
        fabSaveGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameGroup = editNameGroup.getText().toString();

                //Adds the user list that is logged in
                selectedMembersList.add(UserFirebase.getDataUserLogado());
                group.setMembers(selectedMembersList);

                group.setName(nameGroup);
                group.save();

                Intent i = new Intent(GroupRegistrationActivity.this, ChatActivity.class);
                i.putExtra("groupChat", group);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            Bitmap image = null;

            try{
                Uri locateImageSelected = data.getData();
                image = MediaStore.Images.Media.getBitmap(getContentResolver(), locateImageSelected);

                if (image != null){
                    imageGroup.setImageBitmap(image);

                    //Recovering data image
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dataImage = baos.toByteArray();

                    //Save image firebase
                    final StorageReference imageRef = storageReference
                            .child("imagens")
                            .child("grupos")
                            .child(group.getId() + ".jpeg");

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
                                Toast.makeText(GroupRegistrationActivity.this, "Sucesso ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                                String url = task.getResult().toString();
                                group.setPhoto(url);
                            } else {
                                Toast.makeText(GroupRegistrationActivity.this,"Erro ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}