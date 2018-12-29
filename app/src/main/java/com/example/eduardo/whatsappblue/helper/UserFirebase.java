package com.example.eduardo.whatsappblue.helper;

import com.example.eduardo.whatsappblue.config.ConfigurationFirebase;
import com.google.firebase.auth.FirebaseAuth;

public class UserFirebase {

    public static String getIdUser(){
        FirebaseAuth user = ConfigurationFirebase.getFirebaseAuth();
        String email =  user.getCurrentUser().getEmail();
        String idUser = Base64Custom.encodingBase64(email);

        return idUser;
    }
}