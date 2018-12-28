package com.example.eduardo.whatsappblue.model;

import com.example.eduardo.whatsappblue.config.ConfigurationFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

public class User {
    private String idUser;
    private String name;
    private String email;
    private String password;

    public User() {
    }

    public void save(){
        DatabaseReference firebaseRaf = ConfigurationFirebase.getFirebaseDatabase();
        DatabaseReference user = firebaseRaf.child("usuarios").child(getIdUser());

        user.setValue(this);
    }

    @Exclude
    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}