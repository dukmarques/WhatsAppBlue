package com.example.eduardo.whatsappblue.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eduardo.whatsappblue.R;
import com.example.eduardo.whatsappblue.adapter.ContactsAdapter;
import com.example.eduardo.whatsappblue.config.ConfigurationFirebase;
import com.example.eduardo.whatsappblue.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {
    private RecyclerView recyclerViewListContacts;
    private ContactsAdapter adapter;
    private ArrayList<User> contactsList = new ArrayList<>();
    private DatabaseReference usersRef;
    private ValueEventListener valueEventListenerContacts;

    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        //Initial Settings
        recyclerViewListContacts = view.findViewById(R.id.recyclerViewListContacts);
        usersRef = ConfigurationFirebase.getFirebaseDatabase().child("usuarios");

        //Configure adapter
        adapter = new ContactsAdapter(contactsList, getActivity());

        //Configure recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewListContacts.setLayoutManager(layoutManager);
        recyclerViewListContacts.setHasFixedSize(true);
        recyclerViewListContacts.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recoveringContacts();
    }

    @Override
    public void onStop() {
        super.onStop();
        usersRef.removeEventListener(valueEventListenerContacts);
    }

    public void recoveringContacts(){
        valueEventListenerContacts = usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dados : dataSnapshot.getChildren()){
                    User user = dados.getValue(User.class);
                    contactsList.add(user);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
