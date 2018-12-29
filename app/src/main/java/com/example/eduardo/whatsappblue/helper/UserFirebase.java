package com.example.eduardo.whatsappblue.helper;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.eduardo.whatsappblue.config.ConfigurationFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UserFirebase {

    public static String getIdUser(){
        FirebaseAuth user = ConfigurationFirebase.getFirebaseAuth();
        String email =  user.getCurrentUser().getEmail();
        String idUser = Base64Custom.encodingBase64(email);

        return idUser;
    }

    public static FirebaseUser getCurrentUser(){
        FirebaseAuth user = ConfigurationFirebase.getFirebaseAuth();
        return user.getCurrentUser();
    }

    public static boolean attNomeUser(String name){
        try{
            FirebaseUser user = getCurrentUser();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build();

            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()){
                        Log.d("Perfil", "Erro ao atualizar nome de perfil");
                    }
                }
            });
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static boolean attPhotoUser(Uri url){
        try{
            FirebaseUser user = getCurrentUser();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(url)
                    .build();

            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()){
                        Log.d("Perfil", "Erro ao atualizar foto");
                    }
                }
            });
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}