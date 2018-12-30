package com.example.eduardo.whatsappblue.model;

import com.example.eduardo.whatsappblue.config.ConfigurationFirebase;
import com.example.eduardo.whatsappblue.helper.UserFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String idUser;
    private String name;
    private String email;
    private String password;
    private String photo;

    public User() {
    }

    public void save(){
        DatabaseReference firebaseRaf = ConfigurationFirebase.getFirebaseDatabase();
        DatabaseReference user = firebaseRaf.child("usuarios").child(getIdUser());

        user.setValue(this);
    }

    public void att(){
        String idUser = UserFirebase.getIdUser();
        DatabaseReference database = ConfigurationFirebase.getFirebaseDatabase();

        DatabaseReference userRef = database.child("usuarios")
                .child(idUser);

        Map<String, Object> valuesUser = converToMap();

        userRef.updateChildren(valuesUser);
    }

    @Exclude
    public Map<String, Object> converToMap(){
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("email", getEmail());
        userMap.put("name", getName());
        userMap.put("photo", getPhoto());

        return userMap;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
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