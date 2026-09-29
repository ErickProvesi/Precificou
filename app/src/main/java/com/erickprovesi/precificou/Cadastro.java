package com.erickprovesi.precificou;

import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.erickprovesi.precificou.ui.CadastroComposeHost;
import com.erickprovesi.precificou.ui.CadastroUiStateHandle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Cadastro extends AppCompatActivity {

    private static final String TAG = "Cadastro";

    // Uma foto de perfil não precisa manter resolução de câmera.
    private static final int PROFILE_IMAGE_MAX_SIZE = 1024;
    private static final int PROFILE_IMAGE_JPEG_QUALITY = 80;

    private CadastroUiStateHandle composeUi;
    private StorageReference storageReference;

    private boolean registrationInProgress = false;
    private boolean authCreatedHere = false;

    private String registrationName = "";
    private String registrationEmail = "";
    private String registrationPassword = "";

    private byte[] imageByte;


    private final ActivityResultLauncher<PickVisualMediaRequest> pickImageLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.PickVisualMedia(),
                    uri -> {

                        if (uri == null) {
                            return;
                        }

                        try {

                            ImageDecoder.Source source =
                                    ImageDecoder.createSource(
                                            getContentResolver(),
                                            uri
                                    );

                            Bitmap image = ImageDecoder.decodeBitmap(
                                    source,
                                    (decoder, info, src) -> {

                                        decoder.setAllocator(
                                                ImageDecoder.ALLOCATOR_SOFTWARE
                                        );

                                        int width =
                                                info.getSize().getWidth();

                                        int height =
                                                info.getSize().getHeight();

                                        int largestSide =
                                                Math.max(width, height);

                                        if (largestSide > PROFILE_IMAGE_MAX_SIZE) {

                                            float scale =
                                                    (float) PROFILE_IMAGE_MAX_SIZE
                                                            / largestSide;

                                            int targetWidth =
                                                    Math.round(width * scale);

                                            int targetHeight =
                                                    Math.round(height * scale);

                                            decoder.setTargetSize(
                                                    targetWidth,
                                                    targetHeight
                                            );
                                        }
                                    }
                            );

                            ByteArrayOutputStream stream =
                                    new ByteArrayOutputStream();

                            image.compress(
                                    Bitmap.CompressFormat.JPEG,
                                    PROFILE_IMAGE_JPEG_QUALITY,
                                    stream
                            );

                            imageByte = stream.toByteArray();

                            if (composeUi != null) {
                                composeUi.setProfileImage(image);
                            }

                        } catch (IOException | SecurityException e) {

                            Log.e(
                                    TAG,
                                    "Erro ao carregar imagem de perfil",
                                    e
                            );

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

        storageReference =
                FirebaseStorage.getInstance().getReference();

        composeUi =
                CadastroComposeHost.show(this);
    }


    public void openLoginFromCompose() {
        finish();
    }


    public void openImagePickerFromCompose() {

        if (registrationInProgress) {
            return;
        }

        pickImageLauncher.launch(
                new PickVisualMediaRequest.Builder()
                        .setMediaType(
                                ActivityResultContracts.PickVisualMedia
                                        .ImageOnly.INSTANCE
                        )
                        .build()
        );
    }


    public void registerFromCompose(
            String name,
            String email,
            String password,
            String confirmPassword
    ) {

        if (registrationInProgress) {
            return;
        }

        String normalizedName =
                name == null ? "" : name.trim();

        String normalizedEmail =
                email == null ? "" : email.trim();

        if (normalizedName.isEmpty()
                || normalizedEmail.isEmpty()
                || password.isEmpty()
                || confirmPassword.isEmpty()) {

            showError(
                    "Preencha todos os campos.",
                    "all"
            );

            return;
        }

        if (normalizedName.length() > 50) {

            showError(
                    "O nome deve possuir no máximo 50 caracteres.",
                    "name"
            );

            return;
        }

        if (!Patterns.EMAIL_ADDRESS
                .matcher(normalizedEmail)
                .matches()) {

            showError(
                    "Digite um e-mail válido.",
                    "email"
            );

            return;
        }

        if (password.length() < 6) {

            showError(
                    "Digite uma senha com no mínimo 6 caracteres.",
                    "password"
            );

            return;
        }

        if (!password.equals(confirmPassword)) {

            showError(
                    "As senhas não correspondem.",
                    "confirm"
            );

            return;
        }

        registrationName = normalizedName;
        registrationEmail = normalizedEmail;
        registrationPassword = password;

        if (composeUi != null) {
            composeUi.clearError();
            composeUi.setLoading(true);
        }

        userRegister();
    }


    private void userRegister() {

        if (registrationInProgress) {
            return;
        }

        registrationInProgress = true;

        /*
         * Caso o Authentication tenha sido criado,
         * mas o Firestore tenha falhado, não tentamos
         * criar a mesma conta novamente.
         */
        if (authCreatedHere
                && FirebaseAuth.getInstance()
                .getCurrentUser() != null) {

            saveUserData();
            return;
        }

        FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(
                        registrationEmail,
                        registrationPassword
                )
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        authCreatedHere = true;

                        saveUserData();

                        return;
                    }

                    stopRegistration();

                    Exception exception =
                            task.getException();

                    String errorMessage;
                    String errorField;

                    if (exception
                            instanceof FirebaseAuthWeakPasswordException) {

                        errorMessage =
                                "Digite uma senha com no mínimo 6 caracteres.";

                        errorField = "password";

                    } else if (exception
                            instanceof FirebaseAuthUserCollisionException) {

                        errorMessage =
                                "Esta conta já foi cadastrada.";

                        errorField = "email";

                    } else if (exception
                            instanceof FirebaseAuthInvalidCredentialsException) {

                        errorMessage =
                                "E-mail inválido.";

                        errorField = "email";

                    } else {

                        errorMessage =
                                "Não foi possível realizar o cadastro. Tente novamente.";

                        errorField = null;
                    }

                    showError(
                            errorMessage,
                            errorField
                    );

                    Log.e(
                            TAG,
                            "Erro no Firebase Authentication",
                            exception
                    );
                });
    }


    private void saveUserData() {

        FirebaseUser currentUser =
                FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {

            stopRegistration();

            showError(
                    "Sua sessão expirou. Volte ao login e tente novamente.",
                    null
            );

            return;
        }

        String userID =
                currentUser.getUid();

        Map<String, Object> userData =
                new HashMap<>();

        userData.put(
                "nomeUsuario",
                registrationName
        );

        userData.put(
                "idUsuario",
                userID
        );

        userData.put(
                "fotoUsuario",
                ""
        );

        /*
         * Usa preferencialmente o e-mail realmente
         * associado ao Authentication.
         */
        String authenticatedEmail =
                currentUser.getEmail();

        userData.put(
                "emailUsuario",
                authenticatedEmail != null
                        ? authenticatedEmail
                        : registrationEmail
        );

        FirebaseFirestore db =
                FirebaseFirestore.getInstance();

        DocumentReference documentReference =
                db.collection("Usuario")
                        .document(userID);

        documentReference.set(userData)

                .addOnSuccessListener(unused -> {

                    Log.d(
                            TAG,
                            "Perfil salvo no Firestore"
                    );

                    if (imageByte != null) {

                        uploadImageToFirebase(
                                imageByte
                        );

                    } else {

                        finishRegistration();
                    }
                })

                .addOnFailureListener(e -> {

                    Log.e(
                            TAG,
                            "Erro ao salvar perfil no Firestore",
                            e
                    );

                    stopRegistration();

                    showError(
                            "Conta criada, mas não foi possível salvar o perfil. Toque em criar conta para tentar novamente.",
                            null
                    );
                });
    }


    private void uploadImageToFirebase(
            byte[] imageData
    ) {

        FirebaseUser currentUser =
                FirebaseAuth.getInstance().getCurrentUser();

        /*
         * A foto é opcional.
         * Se a sessão desaparecer neste ponto,
         * a conta e o perfil já foram criados.
         */
        if (currentUser == null) {

            Toast.makeText(
                    this,
                    "Conta criada, mas não foi possível enviar a foto.",
                    Toast.LENGTH_LONG
            ).show();

            finishRegistration();

            return;
        }

        String uid =
                currentUser.getUid();

        /*
         * Mantemos o mesmo caminho utilizado
         * anteriormente para não quebrar referências
         * existentes no projeto.
         */
        StorageReference fileReference =
                storageReference.child(
                        uid + "/" + uid + ".png"
                );

        fileReference.putBytes(imageData)

                .addOnSuccessListener(taskSnapshot -> {

                    Log.d(
                            TAG,
                            "Imagem enviada com sucesso"
                    );

                    finishRegistration();
                })

                .addOnFailureListener(e -> {

                    Log.e(
                            TAG,
                            "Erro ao enviar foto de perfil",
                            e
                    );

                    Toast.makeText(
                            this,
                            "Conta criada, mas não foi possível enviar a foto. Você poderá adicioná-la no perfil.",
                            Toast.LENGTH_LONG
                    ).show();

                    finishRegistration();
                });
    }


    private void showError(
            String message,
            String field
    ) {

        if (composeUi != null) {
            composeUi.showError(
                    message,
                    field
            );
        }
    }


    private void stopRegistration() {

        registrationInProgress = false;

        if (composeUi != null) {
            composeUi.setLoading(false);
        }
    }


    private void finishRegistration() {

        stopRegistration();

        Toast.makeText(
                this,
                "Cadastro realizado com sucesso!",
                Toast.LENGTH_SHORT
        ).show();

        FirebaseAuth.getInstance()
                .signOut();

        finish();
    }


    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(
                R.anim.login_enter,
                R.anim.register_exit
        );
    }
}