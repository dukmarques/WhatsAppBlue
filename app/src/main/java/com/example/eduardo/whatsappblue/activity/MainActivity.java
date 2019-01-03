package com.example.eduardo.whatsappblue.activity;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.eduardo.whatsappblue.R;
import com.example.eduardo.whatsappblue.config.ConfigurationFirebase;
import com.example.eduardo.whatsappblue.fragment.ContactsFragment;
import com.example.eduardo.whatsappblue.fragment.ConversationsFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import mehdi.sakout.aboutpage.AboutPage;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private MaterialSearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = ConfigurationFirebase.getFirebaseAuth();

        Toolbar toolbar = findViewById(R.id.toolbarMain);
        toolbar.setTitle("WhatsApp Blue");
        setSupportActionBar(toolbar);

        //Configuring flaps
        final FragmentPagerItemAdapter adapter = new  FragmentPagerItemAdapter(
                getSupportFragmentManager(),
                FragmentPagerItems.with(this)
                .add("Conversas", ConversationsFragment.class)
                .add("Contatos", ContactsFragment.class)
                .create()
        );
        final ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = findViewById(R.id.viewPagerTab);
        viewPagerTab.setViewPager(viewPager);

        //Configuration search View
        searchView = findViewById(R.id.materialSearchPrincipal);

        //Listener to search view
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
                ConversationsFragment fragment = (ConversationsFragment) adapter.getPage(0);
                fragment.reloadConversations();
            }
        });

        //Listener to text box
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //check if you're searching for conversations or contacts from the tab that is active
                switch (viewPager.getCurrentItem()){
                    case 0:
                        ConversationsFragment conversationsFragment = (ConversationsFragment) adapter.getPage(0);
                        if (newText != null && !newText.isEmpty()){
                            conversationsFragment.searchConversations(newText.toLowerCase());
                        }else{
                            conversationsFragment.reloadConversations();
                        }
                        break;
                    case 1:
                        ContactsFragment contactsFragment = (ContactsFragment) adapter.getPage(1);
                        if (newText != null && !newText.isEmpty()){
                            contactsFragment.searchContacts(newText.toLowerCase());
                        }else{
                            contactsFragment.reloadContacts();
                        }
                        break;
                }

                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        //Configure search button
        MenuItem item = menu.findItem(R.id.menuSearch);
        searchView.setMenuItem(item);

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

            case R.id.menuAbout:
                openScreamAbout();
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

    public void openScreamAbout(){
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }
}