package com.example.projeto2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class Login extends AppCompatActivity {

    private TextView txtForgotPassword, txtGoRegister;
    private EditText edtEmail, edtPassword;
    private Button btnLogin, btnGoogleLogin;
    private String[] messages = {"Preencha todos os campos"};
    private ProgressBar pgbLogin;
    private ImageView imgEyeLogin;
    private FirebaseAuth mAuth;
    GoogleSignInClient googleSignInClient;

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
                .requestIdToken("390320075381-hmf63bbb7cj2ohlnv4atjvlupna23kjs.apps.googleusercontent.com").requestEmail().build();

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

    }

        //Métodos login com google

    private void LoginGoogle() {
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent, 1);
    }

    private void loginWithGoogle(String token) {
        AuthCredential credential = GoogleAuthProvider.getCredential(token, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()){
                Toast.makeText(getApplicationContext(), "Login com Google efetuado com sucesso", Toast.LENGTH_SHORT);
                MainScreen();
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
            RegistrationScreen();
        }
    @Override

    public void updateDrawState(TextPaint ds) {
        ds.setColor(Color.WHITE);
        ds.setUnderlineText(true);
    }
    }

        //Autenticar Usuário e ir Login

    private void AuthenticateUser(View v) {
        String email = edtEmail.getText().toString();
        String senha = edtPassword.getText().toString();

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,senha).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
               pgbLogin.setVisibility(View.VISIBLE);

               new Handler().postDelayed(() -> {
                   Toast toast = Toast.makeText(getApplicationContext(), "Login efetuado com Sucesso.", Toast.LENGTH_SHORT);
                   toast.show();
                   MainScreen();

               },2000);



            }else {
                String error;
                try {
                    throw task.getException();

                }catch (FirebaseAuthInvalidCredentialsException e) {
                    error = "E-mail ou Senha inválido(s)";

                }catch (Exception e){
                    error = "Erro ao logar usuário";

                }
                Snackbar snackbar = Snackbar.make(v,error,Snackbar.LENGTH_SHORT);
                snackbar.setBackgroundTint(Color.WHITE);
                snackbar.setTextColor(Color.BLACK);
                snackbar.show();
            }
        });
    }

        //On Start

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null && Principal.voltou == 0) {
            MainScreen();
        }

        //Métodos Intent
    }
    private void MainScreen() {
        Intent Gomain = new Intent(Login.this, ViewPager.class);
        startActivity(Gomain);
        finish();
    }
    private void RegistrationScreen() {
        Intent GoRegister = new Intent(Login.this, Cadastro.class);
        startActivity(GoRegister);
        finish();
    }
}
