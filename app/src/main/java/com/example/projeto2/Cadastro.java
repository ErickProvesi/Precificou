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
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;

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
import com.google.firebase.auth.FirebaseUser;
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

    private boolean registrationInProgress = false;
    private boolean authCreatedHere = false;

    private Uri imgUri;
    byte[] imageByte;

    private final ActivityResultLauncher<PickVisualMediaRequest> pickImageLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.PickVisualMedia(),
                    uri -> {
                        if (uri == null) {
                            return;
                        }

                        imgUri = uri;

                        try {
                            Bitmap original = MediaStore.Images.Media.getBitmap(
                                    getContentResolver(),
                                    imgUri
                            );

                            ByteArrayOutputStream stream =
                                    new ByteArrayOutputStream();

                            original.compress(
                                    Bitmap.CompressFormat.JPEG,
                                    30,
                                    stream
                            );

                            imgProfileRegister.setBackground(null);
                            imgProfileRegister.setImageBitmap(original);

                            imageByte = stream.toByteArray();

                        } catch (IOException | SecurityException e) {
                            e.printStackTrace();

                            Toast.makeText(
                                    Cadastro.this,
                                    "Não foi possível carregar a imagem",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
            );

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

        imgProfileRegister.setOnClickListener(view -> {

            pickImageLauncher.launch(
                    new PickVisualMediaRequest.Builder()
                            .setMediaType(
                                    ActivityResultContracts.PickVisualMedia
                                            .ImageOnly.INSTANCE
                            )
                            .build()
            );

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

        if (registrationInProgress) {
            return;
        }

        registrationInProgress = true;

        pgbRegister.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);

        // Se a conta já foi criada nesta tela, mas houve
        // falha ao salvar o perfil, tentamos novamente
        // somente a gravação dos dados.

        if (authCreatedHere
                && FirebaseAuth.getInstance().getCurrentUser() != null) {

            SaveUserData();
            return;
        }

        String senha = edtPasswordRegister.getText().toString();
        String email = edtEmailRegister.getText().toString();

        FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        authCreatedHere = true;

                        SaveUserData();

                    } else {

                        registrationInProgress = false;

                        pgbRegister.setVisibility(View.GONE);
                        btnRegister.setEnabled(true);

                        Exception exception = task.getException();

                        String erro;

                        if (exception instanceof FirebaseAuthWeakPasswordException) {

                            erro = "Digite uma senha com no mínimo 6 caracteres";

                        } else if (exception instanceof FirebaseAuthUserCollisionException) {

                            erro = "Esta conta já foi cadastrada";

                        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {

                            erro = "E-mail inválido";

                        } else {

                            erro = "Erro ao cadastrar usuário";

                        }

                        Snackbar snackbar = Snackbar.make(
                                view,
                                erro,
                                Snackbar.LENGTH_SHORT
                        );

                        snackbar.setBackgroundTint(Color.WHITE);
                        snackbar.setTextColor(Color.BLACK);

                        snackbar.show();

                        Log.e(
                                "Cadastro",
                                "Erro no Authentication",
                                exception
                        );
                    }
                });
    }
    private void SaveUserData() {

        FirebaseUser currentUser =
                FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {

            registrationInProgress = false;

            pgbRegister.setVisibility(View.GONE);
            btnRegister.setEnabled(true);

            Toast.makeText(
                    this,
                    "Sessão expirada. Faça login novamente.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        String nome = edtNameRegister.getText().toString();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        userID = currentUser.getUid();

        Map<String, Object> users = new HashMap<>();

        users.put("nomeUsuario", nome);
        users.put("idUsuario", userID);
        users.put("fotoUsuario", "");
        users.put("emailUsuario", edtEmailRegister.getText().toString());

        DocumentReference documentReference =
                db.collection("Usuario").document(userID);

        documentReference.set(users)

                .addOnSuccessListener(unused -> {

                    Log.d(
                            "Cadastro",
                            "Perfil salvo no Firestore"
                    );

                    if (imageByte != null) {

                        uploadImageToFirebase(imageByte);

                    } else {

                        finishRegistration();
                    }

                })

                .addOnFailureListener(e -> {

                    Log.e(
                            "Cadastro",
                            "Erro ao salvar perfil no Firestore",
                            e
                    );

                    registrationInProgress = false;

                    pgbRegister.setVisibility(View.GONE);
                    btnRegister.setEnabled(true);

                    Toast.makeText(
                            this,
                            "Conta criada, mas houve erro ao salvar o perfil. Toque em cadastrar para tentar novamente.",
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
    private void GoLoginScreen() {
        Intent GoLogin = new Intent(Cadastro.this, Login.class);
        startActivity(GoLogin);
        finish();
    }

    private void uploadImageToFirebase(byte[] imageByte) {

        FirebaseUser currentUser =
                FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {

            registrationInProgress = false;

            pgbRegister.setVisibility(View.GONE);
            btnRegister.setEnabled(true);

            return;
        }

        String uid = currentUser.getUid();

        StorageReference fileRef =
                storageReference.child(
                        uid + "/" + uid + ".png"
                );

        fileRef.putBytes(imageByte)

                .addOnSuccessListener(taskSnapshot -> {

                    Log.d(
                            "Cadastro",
                            "Imagem enviada com sucesso"
                    );

                    finishRegistration();

                })

                .addOnFailureListener(e -> {

                    Log.e(
                            "Cadastro",
                            "Erro ao enviar foto de perfil",
                            e
                    );

                    // A foto é opcional. A conta e o perfil
                    // já foram criados com sucesso.

                    Toast.makeText(
                            this,
                            "Conta criada, mas não foi possível enviar a foto. Você poderá adicioná-la no perfil.",
                            Toast.LENGTH_LONG
                    ).show();

                    finishRegistration();
                });
    }

    private void finishRegistration() {

        registrationInProgress = false;

        pgbRegister.setVisibility(View.GONE);

        btnRegister.setEnabled(true);

        Toast.makeText(
                this,
                "Cadastro realizado com sucesso!",
                Toast.LENGTH_SHORT
        ).show();

        FirebaseAuth.getInstance().signOut();

        GoLoginScreen();
    }
}