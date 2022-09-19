package com.example.projeto2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

public class EsqueceuSenha extends AppCompatActivity {

   private Button btnSendRecover;
   private ImageView imgBackRecover;
   FirebaseAuth mauth;
   private EditText edtEmailRecover;
   private TextView txtRecover2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esqueceu_senha);

        btnSendRecover = findViewById(R.id.btnSendRecover);
        imgBackRecover = findViewById(R.id.imgBackRecover);
        edtEmailRecover = findViewById(R.id.edtEmailRecover);
        txtRecover2 = findViewById(R.id.txtRecover2);

        mauth = FirebaseAuth.getInstance();

        imgBackRecover.setOnClickListener(view -> BackToLoginScreen());

        btnSendRecover.setOnClickListener(view -> {
            RecoverPassword(view);
    });
    }
    private void BackToLoginScreen() {
        Intent LoginScreen = new Intent(EsqueceuSenha.this, Login.class);
        startActivity(LoginScreen);
    }
    private void RecoverPassword(View view){

        String email = edtEmailRecover.getText().toString();

        if (email.isEmpty()) {
            Snackbar snackbar = Snackbar.make(view, "Preencha o campo com seu E-mail.", Snackbar.LENGTH_SHORT);
            snackbar.setBackgroundTint(Color.WHITE);
            snackbar.setTextColor(Color.BLACK);
            snackbar.show();

        }else {
            sendEmail(view);
        }


    }
    private void sendEmail(View view) {

        String email = edtEmailRecover.getText().toString();

        mauth.sendPasswordResetEmail(email).addOnSuccessListener(unused -> {

            Snackbar snackbar = Snackbar.make(view, "Mensagem Enviada para seu Email.", Snackbar.LENGTH_SHORT);
            snackbar.setBackgroundTint(Color.WHITE);
            snackbar.setTextColor(Color.BLACK);
            snackbar.show();
            txtRecover2.setText("Obs: Verifique sua caixa de spam também!");

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Snackbar snackbar = Snackbar.make(view, "Erro ao enviar, verifique seu E-mail.", Snackbar.LENGTH_SHORT);
                snackbar.setBackgroundTint(Color.WHITE);
                snackbar.setTextColor(Color.BLACK);
                snackbar.show();
            }
        });
    }
}