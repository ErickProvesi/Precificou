package com.example.projeto2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Cadastro extends AppCompatActivity {

    private EditText edtPasswordRegister, edtNameRegister, edtConfirmPassword, edtEmailRegister;
    private Button btnRegister;
    private String[] messages = {"Preencha todos os campos.", "Cadastro realizado com sucesso.", "As senhas não correspondem.", "Nome muito longo."};
    private String userID;
    private ImageView imgBack;
    private ProgressBar pgbRegister;
    private ImageView imgEyeRegister, ImgEyeRegister2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        edtPasswordRegister = findViewById(R.id.edtPasswordRegister);
        edtNameRegister = findViewById(R.id.edtNameRegister);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        edtEmailRegister = findViewById(R.id.edtEmailRegister);
        btnRegister = findViewById(R.id.btnRegister);
        imgBack = findViewById(R.id.imgBack);
        pgbRegister = findViewById(R.id.pgbRegister);
        imgEyeRegister = findViewById(R.id.imgEyeRegister);
        ImgEyeRegister2 = findViewById(R.id.imgEyeRegister2);

        imgEyeRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtPasswordRegister.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    //Se for visivel, vai esconder
                    edtPasswordRegister.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    //Mudar icone
                    imgEyeRegister.setImageResource(R.drawable.vetorolhofechado);
                }else {
                    edtPasswordRegister.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imgEyeRegister.setImageResource(R.drawable.vetorolhosenha);
                }
            }
        });

        ImgEyeRegister2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtConfirmPassword.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                    edtConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

                    ImgEyeRegister2.setImageResource(R.drawable.vetorolhofechado);
                }else {
                    edtConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    ImgEyeRegister2.setImageResource(R.drawable.vetorolhosenha);
                }
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoLoginScreen();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = edtNameRegister.getText().toString();
                String password = edtPasswordRegister.getText().toString();
                String confirm = edtConfirmPassword.getText().toString();
                String email = edtEmailRegister.getText().toString();

                if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                    Snackbar snackbar = Snackbar.make(view, messages[0], Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();

                }else if(!password.equals(confirm)) {
                    Snackbar snackbar = Snackbar.make(view, messages[2], Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();

                }else if (name.length() > 25) {
                    Snackbar snackbar = Snackbar.make(view, messages[3], Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                    }
                else {
                    UserRegister(view);
                }
                }
        });

    }
    private void UserRegister(View view) {

        String senha = edtPasswordRegister.getText().toString();
        String email = edtEmailRegister.getText().toString();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    pgbRegister.setVisibility(View.VISIBLE);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            SaveUserData();
                            Snackbar snackbar = Snackbar.make(view,messages[1],Snackbar.LENGTH_SHORT);
                            snackbar.setBackgroundTint(Color.WHITE);
                            snackbar.setTextColor(Color.BLACK);
                            snackbar.show();
                            GoLoginScreen();


                        }
                    },1000);

                }else {
                    String erro;
                    try {
                        throw task.getException();

                    }catch (FirebaseAuthWeakPasswordException e) {
                        erro = "Digite uma senha com no mínimo 6 caracteres";

                    }catch (FirebaseAuthUserCollisionException e) {
                        erro = "Esta conta já foi cadastrada";

                    }catch (FirebaseAuthInvalidCredentialsException e) {
                        erro = "E-mail inválido";

                    }catch(Exception e){
                        erro = "Erro ao cadastrar usuário";

                    }

                    Snackbar snackbar = Snackbar.make(view,erro,Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();

                }
            }
        });
    }
    private void SaveUserData() {
        String nome = edtNameRegister.getText().toString();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> users = new HashMap<>();
        users.put("nomeUsuario", nome);
        users.put("idUsuario", userID);
        users.put("fotoUsuario", "");
        users.put("senhaUsuario", edtPasswordRegister.getText().toString());



        DocumentReference documentReference = db.collection("Usuario").document(userID);
        documentReference.set(users).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("db", "Sucesso ao salvar os dados");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    Log.d("db_error" ,"Erro ao salvar os dados" + e.toString());
                    }
                });
    }
    private void GoLoginScreen() {
        Intent GoLogin = new Intent(Cadastro.this, Login.class);
        startActivity(GoLogin);
        finish();
    }
}