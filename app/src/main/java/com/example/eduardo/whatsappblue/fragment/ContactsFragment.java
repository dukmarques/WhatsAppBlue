package com.example.eduardo.whatsappblue.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.eduardo.whatsappblue.R;
import com.example.eduardo.whatsappblue.activity.ChatActivity;
import com.example.eduardo.whatsappblue.activity.GroupActivity;
import com.example.eduardo.whatsappblue.adapter.ContactsAdapter;
import com.example.eduardo.whatsappblue.config.ConfigurationFirebase;
import com.example.eduardo.whatsappblue.helper.RecyclerItemClickListener;
import com.example.eduardo.whatsappblue.helper.UserFirebase;
import com.example.eduardo.whatsappblue.model.User;
import com.google.firebase.auth.FirebaseUser;
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
    private FirebaseUser currentUser;

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
        currentUser = UserFirebase.getCurrentUser();

        //Configure adapter
        adapter = new ContactsAdapter(contactsList, getActivity());

        //Configure recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewListContacts.setLayoutManager(layoutManager);
        recyclerViewListContacts.setHasFixedSize(true);
        recyclerViewListContacts.setAdapter(adapter);

        //To set click event on recyclerview
        recyclerViewListContacts.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(),
                        recyclerViewListContacts,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                User selectedUser = contactsList.get(position);
                                boolean header = selectedUser.getEmail().isEmpty();

                                if(header){
                                    Intent i = new Intent(getActivity(), GroupActivity.class);
                                    startActivity(i);
                                }else{
                                    Intent i = new Intent(getActivity(), ChatActivity.class);
                                    i.putExtra("contactChat", selectedUser);
                                    startActivity(i);
                                }
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );

        headerGroup();

        return view;
    }

    public void headerGroup(){
        //Define user whit e-mail empty
        //In case of empty e-mail the user will be used as header, displaying a new group
        User itemGroup = new User();
        itemGroup.setName("Novo Grupo");
        itemGroup.setEmail("");

        contactsList.add(itemGroup);
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
                contactsList.clear(); //Clear contact list

                headerGroup();

                for (DataSnapshot dados : dataSnapshot.getChildren()){
                    User user = dados.getValue(User.class);
                    String currentUserEmail = currentUser.getEmail();

                    if (!currentUserEmail.equals(user.getEmail())) {
                        contactsList.add(user);
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
