package com.example.eduardo.whatsappblue.activity;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.eduardo.whatsappblue.R;
import com.example.eduardo.whatsappblue.config.ConfigurationFirebase;
import com.example.eduardo.whatsappblue.fragment.ContactsFragment;
import com.example.eduardo.whatsappblue.fragment.ConversationsFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

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

        //Configuring flaps
        FragmentPagerItemAdapter adapter = new  FragmentPagerItemAdapter(
                getSupportFragmentManager(),
                FragmentPagerItems.with(this)
                .add("Conversas", ConversationsFragment.class)
                .add("Contatos", ContactsFragment.class)
                .create()
        );
        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = findViewById(R.id.viewPagerTab);
        viewPagerTab.setViewPager(viewPager);
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
            case R.id.menuConfigurations:
                openConfigurations();
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

    public void openConfigurations(){
        Intent intent = new Intent(MainActivity.this, ConfigActivity.class);
        startActivity(intent);
    }
}
