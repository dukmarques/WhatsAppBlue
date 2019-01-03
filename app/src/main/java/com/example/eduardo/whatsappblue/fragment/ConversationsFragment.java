package com.example.eduardo.whatsappblue.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.eduardo.whatsappblue.R;
import com.example.eduardo.whatsappblue.activity.ChatActivity;
import com.example.eduardo.whatsappblue.adapter.ConversationsAdapter;
import com.example.eduardo.whatsappblue.config.ConfigurationFirebase;
import com.example.eduardo.whatsappblue.helper.RecyclerItemClickListener;
import com.example.eduardo.whatsappblue.helper.UserFirebase;
import com.example.eduardo.whatsappblue.model.Conversation;
import com.example.eduardo.whatsappblue.model.User;
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

        //Configure event click
        recyclerViewConversations.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(),
                        recyclerViewConversations,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                List<Conversation> conversationsListAtt = adapter.getConversations();
                                Conversation selectedConversation = conversationsListAtt.get(position);

                                if (selectedConversation.getIsGroup().equals("true")){
                                    Intent i = new Intent(getActivity(), ChatActivity.class);
                                    i.putExtra("groupChat", selectedConversation.getGroup());
                                    startActivity(i);
                                }else{
                                    Intent i = new Intent(getActivity(), ChatActivity.class);
                                    i.putExtra("contactChat", selectedConversation.getUserExhibition());
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

    public void searchConversations(String text){
        List<Conversation> listConversationsSearch = new ArrayList<>();

        for (Conversation conversation : conversationsList){
            if (conversation.getUserExhibition() != null){
                String name = conversation.getUserExhibition().getName().toLowerCase();
                String lastMessage = conversation.getLastMessage().toLowerCase();

                if (name.contains(text)|| lastMessage.contains(text)){
                    listConversationsSearch.add(conversation);
                }
            }else{
                String name = conversation.getGroup().getName().toLowerCase();
                String lastMessage = conversation.getLastMessage().toLowerCase();

                if (name.contains(text)|| lastMessage.contains(text)){
                    listConversationsSearch.add(conversation);
                }
            }
        }
        adapter = new ConversationsAdapter(listConversationsSearch, getActivity());
        recyclerViewConversations.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void reloadConversations(){
        adapter = new ConversationsAdapter(conversationsList, getActivity());
        recyclerViewConversations.setAdapter(adapter);
        adapter.notifyDataSetChanged();
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
