package com.erickprovesi.precificou;

import android.content.Intent;
import android.os.Bundle;
import com.erickprovesi.precificou.ui.LoginComposeHost;
import java.util.function.BiConsumer;
import android.util.Log;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    private BiConsumer<String, Boolean> composeError;
    private FirebaseAuth mAuth;
    GoogleSignInClient googleSignInClient;
    public static int GoogleLogin = 0;
    private String personName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Configuração do Google Sign-In
        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(
                        GoogleSignInOptions.DEFAULT_SIGN_IN
                )
                        .requestIdToken(
                                getString(R.string.google_web_client_id)
                        )
                        .requestEmail()
                        .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Interface principal em Jetpack Compose
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
        AuthCredential credential =
                GoogleAuthProvider.getCredential(token, null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {

                        GoogleSignInAccount acct =
                                GoogleSignIn.getLastSignedInAccount(this);

                        if (acct != null) {

                            personName = acct.getDisplayName();

                            GoogleLogin = 1;

                            SaveUserDataGoogle();

                        } else {
                            Log.e(
                                    "PrecificouGoogle",
                                    "Autenticação concluída, mas a conta Google não foi recuperada"
                            );
                        }

                    } else {

                        Log.e(
                                "PrecificouGoogle",
                                "Erro ao autenticar com Firebase",
                                task.getException()
                        );

                        Toast.makeText(
                                this,
                                "Não foi possível entrar com Google. Tente novamente.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    // Chamado pela interface Compose
    public void authenticateFromCompose(String email, String senha) {
        authenticateUser(email.trim(), senha);
    }

    // Autenticação com e-mail e senha
    private void authenticateUser(String email, String senha) {

        if (email.isEmpty() || senha.isEmpty()) {

            if (composeError != null) {
                composeError.accept(
                        "Preencha o e-mail e a senha para continuar.",
                        true
                );
            }

            return;
        }

        mAuth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {

                        FirebaseUser user = mAuth.getCurrentUser();

                        if (user != null) {

                            String provider = user.getProviderData()
                                    .get(user.getProviderData().size() - 1)
                                    .getProviderId();

                            if ("google.com".equals(provider)) {
                                Login.GoogleLogin = 1;
                            }
                        }

                        mainScreen();

                    } else {

                        Exception exception = task.getException();

                        boolean invalidCredentials =
                                exception instanceof FirebaseAuthInvalidCredentialsException;

                        String errorMessage = invalidCredentials
                                ? "Confira o e-mail e a senha e tente novamente."
                                : "Não foi possível entrar. Tente novamente.";

                        Log.e(
                                "PrecificouLogin",
                                "Falha na autenticação por e-mail",
                                exception
                        );

                        if (composeError != null) {
                            composeError.accept(
                                    errorMessage,
                                    invalidCredentials
                            );
                        }
                    }
                });
    }

        //On Start

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            return;
        }

        String provider = currentUser.getProviderData()
                .get(currentUser.getProviderData().size() - 1)
                .getProviderId();

        if ("google.com".equals(provider)) {
            GoogleLogin = 1;
        }

        mainScreen();
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
