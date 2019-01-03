package com.example.eduardo.whatsappblue.model;

import com.example.eduardo.whatsappblue.config.ConfigurationFirebase;
import com.google.firebase.database.DatabaseReference;

public class Conversation {
    private String idSender;
    private String idRecipient;
    private String lastMessage;
    private User userExhibition;
    private String isGroup;
    private Group group;

    public Conversation() {
        this.setIsGroup("false");
    }

    public void save(){
        DatabaseReference database = ConfigurationFirebase.getFirebaseDatabase();
        DatabaseReference conversationRef = database.child("conversas");

        conversationRef.child(this.getIdSender())
                .child(this.getIdRecipient())
                .setValue(this);
    }

    public String getIdSender() {
        return idSender;
    }

    public void setIdSender(String idSender) {
        this.idSender = idSender;
    }

    public String getIdRecipient() {
        return idRecipient;
    }

    public void setIdRecipient(String idRecipient) {
        this.idRecipient = idRecipient;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public User getUserExhibition() {
        return userExhibition;
    }

    public void setUserExhibition(User userExhibition) {
        this.userExhibition = userExhibition;
    }

    public String getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(String isGroup) {
        this.isGroup = isGroup;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }
}