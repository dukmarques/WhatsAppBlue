package com.example.eduardo.whatsappblue.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.eduardo.whatsappblue.R;
import com.example.eduardo.whatsappblue.adapter.ContactsAdapter;
import com.example.eduardo.whatsappblue.config.ConfigurationFirebase;
import com.example.eduardo.whatsappblue.helper.UserFirebase;
import com.example.eduardo.whatsappblue.model.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GroupActivity extends AppCompatActivity {
    private RecyclerView recyclerSelectedMembers, recyclerMembers;
    private ContactsAdapter contactsAdapter;
    private List<User> membersList = new ArrayList<>();
    private ValueEventListener valueEventListenerMembers;
    private DatabaseReference usersRef;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Initial Settings
        recyclerMembers = findViewById(R.id.recyclerMembers);
        recyclerSelectedMembers = findViewById(R.id.recyclerSelectedMembers);
        usersRef = ConfigurationFirebase.getFirebaseDatabase().child("usuarios");
        currentUser = UserFirebase.getCurrentUser();

        //Configure adapter
        contactsAdapter = new ContactsAdapter(membersList, getApplicationContext());

        //Configure recyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerMembers.setLayoutManager(layoutManager);
        recyclerMembers.setHasFixedSize(true);
        recyclerMembers.setAdapter(contactsAdapter);
    }

    public void recoveringContacts(){
        valueEventListenerMembers = usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                membersList.clear(); //Clear contact list

                for (DataSnapshot dados : dataSnapshot.getChildren()){
                    User user = dados.getValue(User.class);
                    String currentUserEmail = currentUser.getEmail();

                    if (!currentUserEmail.equals(user.getEmail())) {
                        membersList.add(user);
                    }
                }

                contactsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        recoveringContacts();
    }

    @Override
    public void onStop() {
        super.onStop();
        usersRef.removeEventListener(valueEventListenerMembers);
    }
}