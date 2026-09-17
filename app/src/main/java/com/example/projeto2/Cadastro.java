package com.example.projeto2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Cadastro extends AppCompatActivity {

    private EditText edtPasswordRegister, edtNameRegister, edtConfirmPassword, edtEmailRegister;
    private Button btnRegister;
    private String[] messages = {"Preencha todos os campos.", "Cadastro realizado com sucesso.", "As senhas não correspondem.", "Nome muito longo."};
    private String userID;
    private ImageView imgBack;
    private ProgressBar pgbRegister;
    private ImageView imgEyeRegister, ImgEyeRegister2;
    private CircleImageView imgProfileRegister;
    StorageReference storageReference;

    private Uri imgUri;
    byte[] imageByte;

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
        imgProfileRegister = findViewById(R.id.imgProfileRegister);
        storageReference = FirebaseStorage.getInstance().getReference();

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

        imgProfileRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(intent, 1000);
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

                }else if (name.length() > 50) {
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
                            if (imageByte != null) {
                            uploadImageToFirebase(imageByte);
                            }else {

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Snackbar snackbar = Snackbar.make(view,messages[1],Snackbar.LENGTH_SHORT);
                                    snackbar.setBackgroundTint(Color.WHITE);
                                    snackbar.setTextColor(Color.BLACK);
                                    snackbar.show();

                                    GoLoginScreen();
                                }
                            }, 3000);
                        }
                        }

                    },0);
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
        users.put("emailUsuario", edtEmailRegister.getText().toString());



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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000){
            if (resultCode == Activity.RESULT_OK){
                imgUri = data.getData();
                try {
                    Bitmap original = MediaStore.Images.Media.getBitmap(getContentResolver(),imgUri);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    original.compress(Bitmap.CompressFormat.JPEG, 30,stream);

                    imgProfileRegister.setBackground(null);
                    imgProfileRegister.setImageBitmap(original);
                    imageByte = stream.toByteArray();


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void uploadImageToFirebase(byte[] imageByte) {
    StorageReference fileRef = storageReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()+"/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+".png");
    fileRef.putBytes(imageByte).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            Toast.makeText(getApplicationContext(), "Imagem enviada com sucesso", Toast.LENGTH_SHORT);
        }
    }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            Toast.makeText(getApplicationContext(), "Erro ao enviar imagem", Toast.LENGTH_SHORT);
        }
    });
    }
}