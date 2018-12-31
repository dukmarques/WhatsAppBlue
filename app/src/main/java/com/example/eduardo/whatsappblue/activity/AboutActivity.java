package com.example.eduardo.whatsappblue.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.eduardo.whatsappblue.R;

import mehdi.sakout.aboutpage.AboutPage;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_about);

        View aboutPage = new AboutPage(this)
                .setImage(R.drawable.logo_blue)
                .addGroup("Sobre o App")
                .create();
        
        setContentView(aboutPage);
    }
}
