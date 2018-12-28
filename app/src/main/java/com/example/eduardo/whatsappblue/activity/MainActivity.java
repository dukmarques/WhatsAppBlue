package com.example.eduardo.whatsappblue.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.eduardo.whatsappblue.R;
import com.example.eduardo.whatsappblue.config.ConfigurationFirebase;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = ConfigurationFirebase.getFirebaseAuth();

        Toolbar toolbar = findViewById(R.id.toolbarMain);
        toolbar.setTitle("WhatsApp Blue");
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuExit:
                signOutUser();
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void signOutUser(){
        try{
            auth.signOut();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
