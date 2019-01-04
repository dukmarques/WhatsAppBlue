package com.example.eduardo.whatsappblue.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.eduardo.whatsappblue.R;
import com.example.eduardo.whatsappblue.config.ConfigurationFirebase;
import com.example.eduardo.whatsappblue.helper.Base64Custom;
import com.example.eduardo.whatsappblue.helper.Permissions;
import com.example.eduardo.whatsappblue.helper.UserFirebase;
import com.example.eduardo.whatsappblue.model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConfigActivity extends AppCompatActivity {
    private String[] requeridPermissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    private ImageButton imageButtonCamera, imageButtonGallery;
    private static final int SELECAO_CAMERA = 100;
    private static final int SELECAO_GALLERY = 200;
    private CircleImageView circleImageViewPerfil;
    private EditText editPerfilName;
    private ImageView imageAttName;
    private StorageReference storageReference;
    private String idUser;
    private User userLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        //Inital configs
        storageReference = ConfigurationFirebase.getFirebaseStorage();
        idUser = UserFirebase.getIdUser();
        userLogado = UserFirebase.getDataUserLogado();

        //Validate Permissions
        Permissions.validatePermissions(requeridPermissions, this, 1);

        imageButtonCamera = findViewById(R.id.imageButtonCamera);
        imageButtonGallery = findViewById(R.id.imageButtonGallery);
        circleImageViewPerfil = findViewById(R.id.circleImageViewPhotoPerfil);
        editPerfilName = findViewById(R.id.editPerfilName);
        imageAttName = findViewById(R.id.imageAttName);

        Toolbar toolbar = findViewById(R.id.toolbarMain);
        toolbar.setTitle("Perfil");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Add button back

        //Recovegin data user
        FirebaseUser user = UserFirebase.getCurrentUser();
        Uri url = user.getPhotoUrl();

        if (url != null){
            Glide.with(ConfigActivity.this)
                    .load(url)
                    .into(circleImageViewPerfil);
        }else{
            circleImageViewPerfil.setImageResource(R.drawable.padrao);
        }

        editPerfilName.setText(user.getDisplayName());

        imageButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, SELECAO_CAMERA);
                }
            }
        });

        imageButtonGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, SELECAO_GALLERY);
                }
            }
        });

        imageAttName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editPerfilName.getText().toString();
                boolean retorno = UserFirebase.attNomeUser(name);
                if (retorno){
                    userLogado.setName(name);
                    userLogado.att();

                    Toast.makeText(ConfigActivity.this, "Nome alterado com sucesso", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ConfigActivity.this, "Não foi possível alterar o nome", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            // Exibe AlertDialog
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getApplicationContext());
            alertDialogBuilder.setMessage("Criando grupo");
            alertDialogBuilder.setCancelable(false);

            final AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            Bitmap image = null;

            try{
                switch (requestCode){
                    case SELECAO_CAMERA:
                        image = (Bitmap) data.getExtras().get("data");
                        break;
                    case SELECAO_GALLERY:
                        Uri locateImageSelected = data.getData();
                        image = MediaStore.Images.Media.getBitmap(getContentResolver(), locateImageSelected);
                        break;
                }

                if (image != null){
                    circleImageViewPerfil.setImageBitmap(image);

                    //Recovering data image
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dataImage = baos.toByteArray();

                    //Save image firebase
                    final StorageReference imageRef = storageReference
                            .child("imagens")
                            .child("perfil")
                            //.child(idUser)
                            .child(idUser + ".jpeg");

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
                                Toast.makeText(ConfigActivity.this, "Sucesso ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                                Uri url = task.getResult();
                                attPhotoUser(url);

                                alertDialog.dismiss(); //End alertDialog
                            } else {
                                Toast.makeText(ConfigActivity.this,"Erro ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss(); //End alertDialog
                            }
                        }
                    });
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void attPhotoUser(Uri url){
        boolean retorno =  UserFirebase.attPhotoUser(url);
        if (retorno) {
            userLogado.setPhoto(url.toString());
            userLogado.att();
            Toast.makeText(this, "Sua foto foi alterada", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissionResult : grantResults){
            if (permissionResult == PackageManager.PERMISSION_DENIED){
                alertValidatePermission();
            }
        }
    }

    private void alertValidatePermission(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}