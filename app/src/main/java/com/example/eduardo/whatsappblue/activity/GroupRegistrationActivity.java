package com.example.eduardo.whatsappblue.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.eduardo.whatsappblue.R;
import com.example.eduardo.whatsappblue.adapter.GroupSelectedAdapter;
import com.example.eduardo.whatsappblue.model.User;

import java.util.ArrayList;
import java.util.List;

public class GroupRegistrationActivity extends AppCompatActivity {
    private List<User> selectedMembersList = new ArrayList<>();
    private TextView textTotalParticipants;
    private GroupSelectedAdapter groupSelectedAdapter;
    private RecyclerView recyclerSelectedMembers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_registration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Novo grupo");
        toolbar.setSubtitle("Defina o nome");
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

        //Initial settings
        textTotalParticipants = findViewById(R.id.textTotalParticipants);
        recyclerSelectedMembers = findViewById(R.id.recyclerMembersGroup);

        //Retrieve past member list
        if (getIntent().getExtras() != null){
            List<User> members = (List<User>) getIntent().getExtras().getSerializable("members");
            selectedMembersList.addAll(members);

            textTotalParticipants.setText("Participantes: " + selectedMembersList.size());

        }

        //Configure adapter
        groupSelectedAdapter = new GroupSelectedAdapter(selectedMembersList, getApplicationContext());

        //Configure recyclerView
        RecyclerView.LayoutManager layoutManagerHorizontal = new LinearLayoutManager(
                getApplicationContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );
        recyclerSelectedMembers.setLayoutManager(layoutManagerHorizontal);
        recyclerSelectedMembers.setHasFixedSize(true);
        recyclerSelectedMembers.setAdapter(groupSelectedAdapter);
    }
}