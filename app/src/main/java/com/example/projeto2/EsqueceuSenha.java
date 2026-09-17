package com.example.projeto2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

public class EsqueceuSenha extends AppCompatActivity {

   private Button btnSendRecover;
   private ImageView imgBackRecover;
   FirebaseAuth mauth;
   private EditText edtEmailRecover;
   private TextView txtRecover2;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esqueceu_senha);

        btnSendRecover = findViewById(R.id.btnSendRecover);
        imgBackRecover = findViewById(R.id.imgBackRecover);
        edtEmailRecover = findViewById(R.id.edtEmailRecover);
        txtRecover2 = findViewById(R.id.txtRecover2);



        mauth = FirebaseAuth.getInstance();

        imgBackRecover.setOnClickListener(view -> backToLoginScreen());


        btnSendRecover.setOnClickListener(view -> {

            buscarEmail(edtEmailRecover.getText().toString(), view);


    });
    }
    private void backToLoginScreen() {
        Intent LoginScreen = new Intent(EsqueceuSenha.this, Login.class);
        startActivity(LoginScreen);
    }
    private void recoverPassword(View view){

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

    private void buscarEmail (String emailDigitado, View view){

        db.collection("Usuario").whereEqualTo("emailUsuario",emailDigitado).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (!task.getResult().isEmpty()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        Log.d("Ablublé",document.getId()+" => "+document.getData());
                        sendEmail(view);
                    }
                }else {
                    Toast.makeText(getApplicationContext(),"Email não cadastrado",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}