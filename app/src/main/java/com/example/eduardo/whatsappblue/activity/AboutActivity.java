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

        String description = "Este app foi feito com fins educacionais e não possui nenhuma relação, conexão ou " +
                "ligação com o WhatsApp." +
                "\nEste não é um aplicativo oficial!\n"+
                "\nEste app foi desenvolvido por Eduardo Marques, estudante de " +
                "Engenharia de Computação da UEFS em seu estudo de programação para Android."+
                "\n Nenhum de seus dados serão utilizados fora deste App (Infelizmente :D).";

        View aboutPage = new AboutPage(this)
                .setImage(R.drawable.logo_blue)
                .setDescription(description)

                .addGroup("Sobre Eduardo Marques")
                .addEmail("eduardomarques@outlook.com","Envie um e-mail")
                .addGitHub("dukmarques", "GitHub")
                .addFacebook("duduumarques", "Facebook")
                .addInstagram("dudu.ms", "Instagram")
                .addTwitter("duk_ms", "Twitter")
                .create();

        setContentView(aboutPage);
    }
}
