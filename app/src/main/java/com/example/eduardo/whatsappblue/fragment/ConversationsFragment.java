package com.example.eduardo.whatsappblue.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eduardo.whatsappblue.R;
import com.example.eduardo.whatsappblue.adapter.ConversationsAdapter;
import com.example.eduardo.whatsappblue.config.ConfigurationFirebase;
import com.example.eduardo.whatsappblue.helper.UserFirebase;
import com.example.eduardo.whatsappblue.model.Conversation;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConversationsFragment extends Fragment {
    private RecyclerView recyclerViewConversations;
    private List<Conversation> conversationsList = new ArrayList<>();
    private ConversationsAdapter adapter;
    private DatabaseReference database;
    private DatabaseReference conversationRef;
    private ChildEventListener childEventListenerConversations;

    public ConversationsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conversations, container, false);

        recyclerViewConversations = view.findViewById(R.id.recyclerListConversations);

        //Configure adapter
        adapter = new ConversationsAdapter(conversationsList, getActivity());

        //Configure recyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewConversations.setLayoutManager(layoutManager);
        recyclerViewConversations.setHasFixedSize(true);
        recyclerViewConversations.setAdapter(adapter);

        //Configure conversationsRef
        String idUser = UserFirebase.getIdUser();
        database = ConfigurationFirebase.getFirebaseDatabase();
        conversationRef = database.child("conversas")
                .child(idUser);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recoveringConversations();
    }

    @Override
    public void onStop() {
        super.onStop();
        conversationRef.removeEventListener(childEventListenerConversations);
    }

    public void recoveringConversations(){
        conversationsList.clear(); //Clear conversations list

        childEventListenerConversations = conversationRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //Recovering conversations
                Conversation conversation = dataSnapshot.getValue(Conversation.class);
                conversationsList.add(conversation);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
