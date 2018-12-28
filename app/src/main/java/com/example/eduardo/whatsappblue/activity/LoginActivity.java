package com.example.eduardo.whatsappblue.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.eduardo.whatsappblue.R;
import com.example.eduardo.whatsappblue.config.ConfigurationFirebase;
import com.example.eduardo.whatsappblue.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText campoEmail, campoPassword;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = ConfigurationFirebase.getFirebaseAuth();

        campoEmail = findViewById(R.id.editLoginEmail);
        campoPassword = findViewById(R.id.editLoginPassword);

    }

    public void logUpUser(User user){
        auth.signInWithEmailAndPassword(
                user.getEmail(),
                user.getPassword()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    openMainScreen();
                }else{
                    String exception = "";
                    try{
                        throw  task.getException();
                    }catch (FirebaseAuthInvalidUserException e){
                        exception = "Usuário não está cadastrado.";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        exception = "E-mail e senha não correspondem a um usuário.";
                    }catch (Exception e){
                        exception = "Erro ao cadastrar usuário: " + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(LoginActivity.this, exception, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void validateUserAuth(View view){
        String textEmail = campoEmail.getText().toString();
        String textPassword = campoPassword.getText().toString();

        if (!textEmail.isEmpty()){
            if (!textPassword.isEmpty()){
                User user = new User();
                user.setEmail(textEmail);
                user.setPassword(textPassword);

                logUpUser(user);
            }else{
                Toast.makeText(this, "Preencha a senha!", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Preencha o e-mail!", Toast.LENGTH_SHORT).show();
        }
    }

    public void openMainScreen(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void abrirTelaCadastro(View view){
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}
