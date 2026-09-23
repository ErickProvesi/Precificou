package com.example.projeto2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import com.example.projeto2.ui.LoginComposeHost;
import java.util.function.BiConsumer;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    private BiConsumer<String, Boolean> composeError;
    private TextView txtForgotPassword, txtGoRegister;
    private EditText edtEmail, edtPassword;
    private Button btnLogin, btnGoogleLogin;
    private String[] messages = {"Preencha todos os campos"};
    private ProgressBar pgbLogin;
    private ImageView imgEyeLogin;
    private FirebaseAuth mAuth;
    GoogleSignInClient googleSignInClient;
    public static int GoogleLogin = 0;
    String personName;
    String personGivenName;
    String personFamilyName;
    String personEmail;
    String personId;
    Uri personPhoto;
    StorageReference storageReference;
    byte[] imageByte;
    @Nullable
    Intent data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);


        txtForgotPassword = findViewById(R.id.txtForgotPassword);
        txtGoRegister = findViewById(R.id.txtGoRegister);
        btnLogin = findViewById(R.id.btnLogin);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        pgbLogin = findViewById(R.id.pgbLogin);
        btnGoogleLogin = findViewById(R.id.btnGoogleLogin);
        imgEyeLogin = findViewById(R.id.imgEyeLogin);
        storageReference = FirebaseStorage.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();

        SpannableString ss = new SpannableString("Esqueceu a Senha?");
        SpannableString ss2 = new SpannableString("Não possui conta? Clique aqui");

        ss.setSpan(new CustomClickableSpan(), 0, 17, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss2.setSpan(new CustomClickableSpan2(), 18, 29, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        txtForgotPassword.setText(ss);
        txtForgotPassword.setMovementMethod(LinkMovementMethod.getInstance());

        txtGoRegister.setText(ss2);
        txtGoRegister.setMovementMethod(LinkMovementMethod.getInstance());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_web_client_id)).requestEmail().build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);



        btnGoogleLogin.setOnClickListener(view -> LoginGoogle());

        //Ver/Esconder Senha

        imgEyeLogin.setImageResource(R.drawable.vetorolhofechado);
        imgEyeLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtPassword.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    //Se for visivel, vai esconder
                    edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    //Mudar icone
                    imgEyeLogin.setImageResource(R.drawable.vetorolhofechado);
                }else {
                    edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imgEyeLogin.setImageResource(R.drawable.vetorolhosenha);
                }
            }
        });

        //Ação botão Login

        btnLogin.setOnClickListener(view -> {

            String email = edtEmail.getText().toString();
            String senha = edtPassword.getText().toString();

            if (email.isEmpty() || senha.isEmpty()) {
                Snackbar snackbar = Snackbar.make(view, messages[0], Snackbar.LENGTH_SHORT);
                snackbar.setBackgroundTint(Color.WHITE);
                snackbar.setTextColor(Color.BLACK);
                snackbar.show();
            }else {
                AuthenticateUser(view);
            }
        });

        // Exibe a nova interface do login em Jetpack Compose
        composeError = LoginComposeHost.show(this);

    }

    // Abre o cadastro pela interface Compose
    public void openRegistrationFromCompose() {
        registrationScreen();
    }

    // Abre a recuperação de senha pela interface Compose
    public void openForgotPasswordFromCompose() {
        Intent intent = new Intent(
                Login.this,
                EsqueceuSenha.class
        );

        startActivity(intent);
    }

        //Métodos login com google

    private void LoginGoogle() {
        Intent intent = googleSignInClient.getSignInIntent();
        //startActivityForResult(intent, 1);
        openActivity.launch(intent);
    }

    // Abre o Google Sign-In pela interface Compose
    public void openGoogleFromCompose() {
        LoginGoogle();
    }

    ActivityResultLauncher<Intent> openActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {

                Log.d(
                        "PrecificouGoogle",
                        "Google Sign-In retornou. resultCode: "
                                + result.getResultCode()
                );

                Intent intent = result.getData();

                Task<GoogleSignInAccount> task =
                        GoogleSignIn.getSignedInAccountFromIntent(intent);

                try {

                    GoogleSignInAccount account =
                            task.getResult(ApiException.class);

                    if (account == null) {
                        Log.e(
                                "PrecificouGoogle",
                                "Google retornou uma conta nula"
                        );
                        return;
                    }

                    if (account.getIdToken() == null) {
                        Log.e(
                                "PrecificouGoogle",
                                "Google retornou conta sem ID Token"
                        );
                        return;
                    }

                    Log.d(
                            "PrecificouGoogle",
                            "Google Sign-In retornou conta e ID Token"
                    );

                    loginWithGoogle(account.getIdToken());

                } catch (ApiException exception) {

                    Log.e(
                            "PrecificouGoogle",
                            "Falha Google Sign-In. Código: "
                                    + exception.getStatusCode()
                                    + " | Mensagem: "
                                    + exception.getMessage(),
                            exception
                    );
                }
            }
    );

    private void loginWithGoogle(String token) {
        AuthCredential credential = GoogleAuthProvider.getCredential(token, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()){
                Toast.makeText(getApplicationContext(), "Login com Google efetuado com sucesso", Toast.LENGTH_SHORT);

                GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
                if (acct != null) {
                    personName = acct.getDisplayName();
                    personGivenName = acct.getGivenName();
                    personFamilyName = acct.getFamilyName();
                    personEmail = acct.getEmail();
                    personId = acct.getId();
                    personPhoto = acct.getPhotoUrl();
                    SaveUserDataGoogle();

                    System.out.println(personName);
                    System.out.println(personGivenName);
                    System.out.println(personFamilyName);
                    System.out.println(personEmail);
                    System.out.println(personId);
                    System.out.println(personPhoto);

                    GoogleLogin = 1;

                }else {

                }

            }else {
                Toast.makeText(getApplicationContext(), "Erro ao efetuar login com Google", Toast.LENGTH_SHORT);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == 1) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                loginWithGoogle(account.getIdToken());
            }catch (ApiException exception){
                Log.d("Erro", exception.toString());
            }
        }
    }

        //Botão escrita para ir Esqueceu Senha

    class CustomClickableSpan extends ClickableSpan {

                public void onClick(View view) {
                    Intent GoForgotPassword = new Intent(Login.this, EsqueceuSenha.class);
                    startActivity(GoForgotPassword);
                }
        @Override

        public void updateDrawState(TextPaint ds) {
            ds.setColor(Color.WHITE);
            ds.setUnderlineText(true);
        }
    }

        //Botão escrita para ir Cadastro

    class CustomClickableSpan2 extends ClickableSpan {

        public void onClick(View view) {
            registrationScreen();
        }
    @Override

    public void updateDrawState(TextPaint ds) {
        ds.setColor(Color.WHITE);
        ds.setUnderlineText(true);
    }
    }

        //Autenticar Usuário e ir Login

    private void AuthenticateUser(View v) {
        AuthenticateUser(
                v,
                edtEmail.getText().toString(),
                edtPassword.getText().toString()
        );
    }

    // Chamado pela nova interface Compose
    public void authenticateFromCompose(String email, String senha) {
        AuthenticateUser(null, email.trim(), senha);
    }

    // Autenticação compartilhada entre XML e Compose
    private void AuthenticateUser(
            @Nullable View v,
            String email,
            String senha
    ) {

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(
                    this,
                    "Preencha todos os campos",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {

                        Runnable finalizarLogin = () -> {

                            Toast.makeText(
                                    getApplicationContext(),
                                    "Login efetuado com sucesso.",
                                    Toast.LENGTH_SHORT
                            ).show();

                            FirebaseUser user = mAuth.getCurrentUser();

                            if (user != null) {

                                String strProvider = user.getProviderData()
                                        .get(user.getProviderData().size() - 1)
                                        .getProviderId();

                                if (strProvider.equals("google.com")) {
                                    Login.GoogleLogin = 1;
                                }
                            }

                            mainScreen();
                        };

                        if (v == null) {

                            // Compose: navega sem a espera artificial
                            finalizarLogin.run();

                        } else {

                            // Comportamento temporário do XML antigo
                            pgbLogin.setVisibility(View.VISIBLE);

                            new Handler().postDelayed(
                                    finalizarLogin,
                                    2000
                            );
                        }

                    } else {

                        Exception exception = task.getException();

                        String error =
                                exception instanceof FirebaseAuthInvalidCredentialsException
                                        ? "E-mail ou senha inválido(s)"
                                        : "Erro ao logar usuário";

                        if (v == null) {

                            boolean invalidCredentials =
                                    exception instanceof FirebaseAuthInvalidCredentialsException;

                            if (composeError != null) {

                                composeError.accept(
                                        invalidCredentials
                                                ? "Confira o e-mail e a senha e tente novamente."
                                                : "Não foi possível entrar. Tente novamente.",
                                        invalidCredentials
                                );
                            }

                        } else {

                            // Feedback da interface XML antiga
                            Snackbar snackbar = Snackbar.make(
                                    v,
                                    error,
                                    Snackbar.LENGTH_SHORT
                            );

                            snackbar.setBackgroundTint(Color.WHITE);
                            snackbar.setTextColor(Color.BLACK);
                            snackbar.show();
                        }
                    }
                });
    }

        //On Start

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String strProvider = currentUser.getProviderData().get(currentUser.getProviderData().size() - 1).getProviderId();
            if (strProvider.equals("google.com")) {
                Login.GoogleLogin = 1;
                mainScreen();
            }else {
                mainScreen();
            }
            }

        //Métodos Intent
    }

    private void mainScreen() {
        Intent Gomain = new Intent(Login.this, ViewPager.class);
        startActivity(Gomain);
        finish();
    }

    private void registrationScreen() {
        Intent GoRegister = new Intent(Login.this, Cadastro.class);
        startActivity(GoRegister);
        finish();
    }

    private void SaveUserDataGoogle() {

        FirebaseUser currentUser =
                FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {

            Log.e("Login", "Usuário Google não autenticado");

            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String userID = currentUser.getUid();

        DocumentReference documentReference =
                db.collection("Usuario").document(userID);

        documentReference.get()
                .addOnSuccessListener(documentSnapshot -> {

                    // Usuário já possui cadastro no Firestore.
                    // Preserva nome, foto e demais informações existentes.

                    if (documentSnapshot.exists()) {

                        Log.d(
                                "Login",
                                "Cadastro Google já existente"
                        );

                        GoogleLogin = 1;

                        mainScreen();

                        return;
                    }

                    // Primeiro acesso: cria o perfil.

                    Map<String, Object> users = new HashMap<>();

                    users.put("nomeUsuario", personName);
                    users.put("idUsuario", userID);
                    users.put("fotoUsuario", "");

                    documentReference.set(users)
                            .addOnSuccessListener(unused -> {

                                Log.d(
                                        "Login",
                                        "Cadastro Google criado com sucesso"
                                );

                                GoogleLogin = 1;

                                mainScreen();

                            })
                            .addOnFailureListener(e -> {

                                Log.e(
                                        "Login",
                                        "Erro ao cadastrar usuário Google",
                                        e
                                );

                            });

                })
                .addOnFailureListener(e -> {

                    Log.e(
                            "Login",
                            "Erro ao consultar cadastro Google",
                            e
                    );

                });
    }
}
