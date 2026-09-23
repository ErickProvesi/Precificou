package com.erickprovesi.precificou;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageException;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import androidx.activity.result.PickVisualMediaRequest;

public class FragmentoPerfil extends Fragment {

    KeyListener listener;
    Dialog EditEmail, EditPassword, DeleteAccount, Warning;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    TextView txtEmailProfile;
    Button btnEditEmail, btnEditPassword, btnDeleteAccount, btnExitAccount;
    ImageView imgProfilePic, imgEditName, imgProfileCheck, imgEditPic;
    EditText edtProfileName;
    String email = mAuth.getCurrentUser().getEmail();
    StorageReference mStorage = FirebaseStorage.getInstance().getReference();
    Uri imgUri;
    byte[] imageByte;
    String personName;
    String personGivenName;
    String personFamilyName;
    String personEmail;
    String personId;
    Uri personPhoto;
    static int reload=0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_perfil, container, false);

        btnEditEmail = view.findViewById(R.id.btnEditEmail);
        btnEditPassword = view.findViewById(R.id.btnEditPassword);
        btnDeleteAccount = view.findViewById(R.id.btnDeleteAccount);
        btnExitAccount = view.findViewById(R.id.btnExitAccount);
        imgProfilePic = view.findViewById(R.id.imgProfilePic);
        imgEditName = view.findViewById(R.id.imgEditName);
        txtEmailProfile = view.findViewById(R.id.txtEmailProfile);
        edtProfileName = view.findViewById(R.id.edtProfileName);
        imgProfileCheck = view.findViewById(R.id.imgProfileCheck);
        imgEditPic = view.findViewById(R.id.imgEditPic);

        StorageReference googlePhotoReference;
        googlePhotoReference = mStorage.child(FragmentoProduto.userID+"/"+FragmentoProduto.userID+".png");
        final long ONE_MEGABYTE = 768 * 768;

        FirebaseUser usuarioAutenticado = mAuth.getCurrentUser();

        if (usuarioAutenticado == null) {
            return view;
        }

        db.collection("Usuario")
                .document(usuarioAutenticado.getUid())
                .get()
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        Log.e(
                                "FragmentoPerfil",
                                "Erro ao carregar dados do perfil",
                                task.getException()
                        );

                        return;
                    }

                    DocumentSnapshot document = task.getResult();

                    if (document != null && document.exists() && isAdded()) {

                        String nomeUsuario = document.getString("nomeUsuario");

                        if (nomeUsuario != null) {
                            edtProfileName.setText(nomeUsuario);
                        }
                    }
                });

        if (Login.GoogleLogin == 1) {
            imgProfilePic.setBackground(null);
            googlePhotoReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imgProfilePic.setImageBitmap(bmp);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());
                    if (acct != null) {
                        personName = acct.getDisplayName();
                        personGivenName = acct.getGivenName();
                        personFamilyName = acct.getFamilyName();
                        personEmail = acct.getEmail();
                        personId = acct.getId();
                        personPhoto = acct.getPhotoUrl();

                        Glide.with(FragmentoPerfil.this).load(personPhoto).into(imgProfilePic);

                    } else {

                    }
                }
            });
        }
            StorageReference photoReference = mStorage.child(FragmentoProduto.userID + "/" + FragmentoProduto.userID + ".png");
            System.out.println(photoReference);
            imgProfilePic.setBackground(null);
            photoReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imgProfilePic.setImageBitmap(bmp);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {

                }
            });

        imgEditPic.setOnClickListener(view2 -> {

            pickImageLauncher.launch(
                    new PickVisualMediaRequest.Builder()
                            .setMediaType(
                                    ActivityResultContracts.PickVisualMedia
                                            .ImageOnly.INSTANCE
                            )
                            .build()
            );

        });

        listener = edtProfileName.getKeyListener();

        edtProfileName.setKeyListener(null);

        EditEmail = new Dialog(getActivity());
        EditPassword = new Dialog(getActivity());
        DeleteAccount = new Dialog(getActivity());
        Warning = new Dialog(getActivity());

        txtEmailProfile.setText(mAuth.getCurrentUser().getEmail());


        btnExitAccount.setOnClickListener(view14 -> {
            Exit();
        });


        btnEditEmail.setOnClickListener(view13 -> {
            if (Login.GoogleLogin == 1) {
                Snackbar snackbar = Snackbar.make(view, "Não é possivel alterar email logando com o google.", Snackbar.LENGTH_SHORT);
                snackbar.setBackgroundTint(Color.WHITE);
                snackbar.setTextColor(Color.BLACK);
                snackbar.show();
            }else {
                showEmailPopup(view13);
            }
        });

        btnEditPassword.setOnClickListener(view12 -> {
            if (Login.GoogleLogin == 1) {
                Snackbar snackbar = Snackbar.make(view, "Não é possivel alterar senha logando com o google.", Snackbar.LENGTH_SHORT);
                snackbar.setBackgroundTint(Color.WHITE);
                snackbar.setTextColor(Color.BLACK);
                snackbar.show();
            }else {
                showPasswordPopup(view12);
            }
            });

        btnDeleteAccount.setOnClickListener(view1 -> {

            if (Login.GoogleLogin == 1) {
                showWarningPopup();
            }else {
                showDeletePopup(view1);
            }
        });

        imgEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtProfileName.setKeyListener(listener);
                imgEditName.setVisibility(View.INVISIBLE);
                imgProfileCheck.setVisibility(View.VISIBLE);
                edtProfileName.requestFocus();

                edtProfileName.requestFocus();
                edtProfileName.setFocusableInTouchMode(true);

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(edtProfileName, InputMethodManager.SHOW_FORCED);



                imgProfileCheck.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (edtProfileName.getText().toString().isEmpty()){
                            imgProfileCheck.setVisibility(View.INVISIBLE);
                            edtProfileName.setError("Não deixe em branco!");

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    edtProfileName.setError(null);
                                    imgProfileCheck.setVisibility(View.VISIBLE);


                                }
                            },1000);

                        }else {
                            db.collection("Usuario").document(FragmentoProduto.userID).update("nomeUsuario", edtProfileName.getText().toString());
                            imgProfileCheck.setVisibility(View.INVISIBLE);
                            imgEditName.setVisibility(View.VISIBLE);
                            edtProfileName.setKeyListener(null);
                        }
                    }
                });
            }
        });

        return view;

    }

    private final ActivityResultLauncher<Intent> googleReauthLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (result.getResultCode()
                                != Activity.RESULT_OK) {

                            showDeleteError(
                                    "Autenticação Google cancelada",
                                    null
                            );

                            return;
                        }

                        try {

                            GoogleSignInAccount account =
                                    GoogleSignIn
                                            .getSignedInAccountFromIntent(
                                                    result.getData()
                                            )
                                            .getResult(ApiException.class);

                            if (account == null
                                    || account.getIdToken() == null) {

                                showDeleteError(
                                        "Não foi possível obter a credencial Google",
                                        null
                                );

                                return;
                            }

                            FirebaseUser currentUser =
                                    FirebaseAuth.getInstance()
                                            .getCurrentUser();

                            if (currentUser == null) {

                                showDeleteError(
                                        "Usuário não autenticado",
                                        null
                                );

                                return;
                            }

                            AuthCredential credential =
                                    GoogleAuthProvider.getCredential(
                                            account.getIdToken(),
                                            null
                                    );

                            currentUser.reauthenticate(credential)
                                    .addOnCompleteListener(task -> {

                                        if (task.isSuccessful()) {

                                            deleteUserAccount();

                                        } else {

                                            showDeleteError(
                                                    "Falha na reautenticação Google",
                                                    task.getException()
                                            );
                                        }
                                    });

                        } catch (ApiException e) {

                            showDeleteError(
                                    "Erro na autenticação Google",
                                    e
                            );
                        }
                    }
            );

    private void reauthenticateGoogleForDeletion() {

        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(
                        GoogleSignInOptions.DEFAULT_SIGN_IN
                )
                        .requestIdToken(
                                getString(R.string.google_web_client_id)
                        )
                        .requestEmail()
                        .build();

        GoogleSignInClient googleClient =
                GoogleSignIn.getClient(
                        requireActivity(),
                        gso
                );

        // Encerra a sessão Google local para solicitar
        // novamente a escolha/autenticação da conta.

        googleClient.signOut()
                .addOnCompleteListener(task -> {

                    if (!isAdded()) {
                        return;
                    }

                    if (!task.isSuccessful()) {

                        showDeleteError(
                                "Não foi possível iniciar a autenticação Google",
                                task.getException()
                        );

                        return;
                    }

                    googleReauthLauncher.launch(
                            googleClient.getSignInIntent()
                    );
                });
    }

    private void deleteUserAccount() {

        FirebaseUser currentUser =
                FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            showDeleteError(
                    "Usuário não autenticado",
                    null
            );
            return;
        }

        String uid = currentUser.getUid();

        // Consulta somente documentos do usuário autenticado.

        Task<QuerySnapshot> produtosTask =
                db.collection("Produto")
                        .whereEqualTo("idUsuario", uid)
                        .get();

        Task<QuerySnapshot> ingredientesTask =
                db.collection("ListaIngrediente")
                        .whereEqualTo("idUsuario", uid)
                        .get();

        Task<QuerySnapshot> custosTask =
                db.collection("OutrosCustos")
                        .whereEqualTo("idUsuario", uid)
                        .get();

        // Aguarda a conclusão das três consultas.

        Tasks.whenAll(
                produtosTask,
                ingredientesTask,
                custosTask
        ).addOnCompleteListener(queryTask -> {

            if (!queryTask.isSuccessful()) {

                showDeleteError(
                        "Erro ao consultar os dados da conta",
                        queryTask.getException()
                );

                return;
            }

            QuerySnapshot produtos = produtosTask.getResult();
            QuerySnapshot ingredientes = ingredientesTask.getResult();
            QuerySnapshot custos = custosTask.getResult();

            if (produtos == null
                    || ingredientes == null
                    || custos == null) {

                showDeleteError(
                        "Não foi possível carregar os dados da conta",
                        null
                );

                return;
            }

            // Primeiro excluímos as imagens.

            ArrayList<Task<?>> imageTasks =
                    new ArrayList<>();

            imageTasks.add(
                    deleteImageIfExists(
                            uid + "/" + uid + ".png"
                    )
            );

            for (DocumentSnapshot produto : produtos.getDocuments()) {

                String produtoID =
                        produto.getString("idProduto");

                if (produtoID == null || produtoID.isEmpty()) {
                    produtoID = produto.getId();
                }

                imageTasks.add(
                        deleteImageIfExists(
                                uid + "/Produtos/" + produtoID + ".png"
                        )
                );
            }

            Tasks.whenAll(imageTasks)
                    .addOnCompleteListener(imagesTask -> {

                        if (!imagesTask.isSuccessful()) {

                            showDeleteError(
                                    "Erro ao excluir imagens da conta",
                                    imagesTask.getException()
                            );

                            return;
                        }

                        // Agora excluímos os documentos.

                        ArrayList<Task<?>> deleteTasks =
                                new ArrayList<>();

                        for (DocumentSnapshot produto
                                : produtos.getDocuments()) {

                            deleteTasks.add(
                                    produto.getReference().delete()
                            );
                        }

                        for (DocumentSnapshot ingrediente
                                : ingredientes.getDocuments()) {

                            deleteTasks.add(
                                    ingrediente.getReference().delete()
                            );
                        }

                        for (DocumentSnapshot custo
                                : custos.getDocuments()) {

                            deleteTasks.add(
                                    custo.getReference().delete()
                            );
                        }

                        Tasks.whenAll(deleteTasks)
                                .addOnCompleteListener(dataTask -> {

                                    if (!dataTask.isSuccessful()) {

                                        showDeleteError(
                                                "Erro ao excluir dados da conta",
                                                dataTask.getException()
                                        );

                                        return;
                                    }

                                    // Exclui o perfil depois dos
                                    // outros documentos.

                                    db.collection("Usuario")
                                            .document(uid)
                                            .delete()
                                            .addOnCompleteListener(profileTask -> {

                                                if (!profileTask.isSuccessful()) {

                                                    showDeleteError(
                                                            "Erro ao excluir perfil",
                                                            profileTask.getException()
                                                    );

                                                    return;
                                                }

                                                // Por último, exclui a
                                                // conta no Authentication.

                                                currentUser.delete()
                                                        .addOnCompleteListener(authTask -> {

                                                            if (!authTask.isSuccessful()) {

                                                                showDeleteError(
                                                                        "Erro ao excluir a conta do Firebase Authentication",
                                                                        authTask.getException()
                                                                );

                                                                return;
                                                            }

                                                            if (isAdded()) {

                                                                Warning.dismiss();

                                                                Toast.makeText(
                                                                        requireContext(),
                                                                        "Conta excluída com sucesso",
                                                                        Toast.LENGTH_SHORT
                                                                ).show();

                                                                exitDelete();
                                                            }
                                                        });
                                            });
                                });
                    });
        });
    }

    private void showDeleteError(
            String mensagem,
            Exception exception
    ) {

        Log.e(
                "FragmentoPerfil",
                mensagem,
                exception
        );

        if (!isAdded()) {
            return;
        }

        Button btnConfirm =
                Warning.findViewById(R.id.btnConfirm);

        if (btnConfirm != null) {
            btnConfirm.setEnabled(true);
        }

        Toast.makeText(
                requireContext(),
                mensagem + ". Tente novamente.",
                Toast.LENGTH_LONG
        ).show();
    }

    private Task<Void> deleteImageIfExists(String path) {

        return mStorage.child(path)
                .delete()
                .continueWith(task -> {

                    if (task.isSuccessful()) {
                        return null;
                    }

                    Exception exception = task.getException();

                    if (exception instanceof StorageException) {

                        StorageException storageException =
                                (StorageException) exception;

                        if (storageException.getErrorCode()
                                == StorageException.ERROR_OBJECT_NOT_FOUND) {

                            return null;
                        }
                    }

                    if (exception != null) {
                        throw exception;
                    }

                    throw new IllegalStateException(
                            "Falha ao excluir imagem"
                    );
                });
    }

    private final ActivityResultLauncher<PickVisualMediaRequest> pickImageLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.PickVisualMedia(),
                    uri -> {

                        if (uri == null || !isAdded()) {
                            return;
                        }

                        imgUri = uri;

                        try {
                            Bitmap original = MediaStore.Images.Media.getBitmap(
                                    requireContext().getContentResolver(),
                                    imgUri
                            );

                            ByteArrayOutputStream stream =
                                    new ByteArrayOutputStream();

                            original.compress(
                                    Bitmap.CompressFormat.JPEG,
                                    30,
                                    stream
                            );

                            imgProfilePic.setBackground(null);
                            imgProfilePic.setImageBitmap(original);

                            imageByte = stream.toByteArray();

                            uploadImageToFirebase(imageByte);

                        } catch (IOException | SecurityException e) {
                            e.printStackTrace();

                            Toast.makeText(
                                    requireContext(),
                                    "Não foi possível carregar a imagem",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
            );
    public void showEmailPopup(View view) {

        EditEmail.setContentView(R.layout.popup_alteraremail);

        EditText edtCurrentEmail =
                EditEmail.findViewById(R.id.edtCurrentEmail);

        EditText edtNewEmail =
                EditEmail.findViewById(R.id.edtNewEmail);

        EditText edtConfirmNewEmail =
                EditEmail.findViewById(R.id.edtConfirmNewEmail);

        EditText edtPassword =
                EditEmail.findViewById(R.id.edtPassword);

        Button btnModifyEmail =
                EditEmail.findViewById(R.id.btnModifyEmail);

        btnModifyEmail.setOnClickListener(v -> {

            String emailInformado =
                    edtCurrentEmail.getText().toString().trim();

            String novoEmail =
                    edtNewEmail.getText().toString().trim();

            String confirmacaoEmail =
                    edtConfirmNewEmail.getText().toString().trim();

            String senhaInformada =
                    edtPassword.getText().toString();

            FirebaseUser currentUser =
                    FirebaseAuth.getInstance().getCurrentUser();

            if (currentUser == null || currentUser.getEmail() == null) {

                Toast.makeText(
                        requireContext(),
                        "Usuário não autenticado",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            String emailAtual = currentUser.getEmail();

            if (emailInformado.isEmpty()
                    || novoEmail.isEmpty()
                    || confirmacaoEmail.isEmpty()
                    || senhaInformada.isEmpty()) {

                Toast.makeText(
                        requireContext(),
                        "Preencha todos os campos",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if (!emailInformado.equalsIgnoreCase(emailAtual)) {

                Toast.makeText(
                        requireContext(),
                        "O e-mail atual informado está incorreto",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if (!novoEmail.equalsIgnoreCase(confirmacaoEmail)) {

                Toast.makeText(
                        requireContext(),
                        "Os novos e-mails não coincidem",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if (novoEmail.equalsIgnoreCase(emailAtual)) {

                Toast.makeText(
                        requireContext(),
                        "Informe um e-mail diferente do atual",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            btnModifyEmail.setEnabled(false);

            AuthCredential credential =
                    EmailAuthProvider.getCredential(
                            emailAtual,
                            senhaInformada
                    );

            currentUser.reauthenticate(credential)
                    .addOnCompleteListener(authTask -> {

                        if (!authTask.isSuccessful()) {

                            btnModifyEmail.setEnabled(true);

                            if (isAdded()) {
                                Toast.makeText(
                                        requireContext(),
                                        "Não foi possível confirmar sua senha atual",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }

                            return;
                        }

                        currentUser.verifyBeforeUpdateEmail(novoEmail)
                                .addOnCompleteListener(emailTask -> {

                                    btnModifyEmail.setEnabled(true);

                                    if (!isAdded()) {
                                        return;
                                    }

                                    if (emailTask.isSuccessful()) {

                                        Toast.makeText(
                                                requireContext(),
                                                "Enviamos um link de confirmação para o novo e-mail",
                                                Toast.LENGTH_LONG
                                        ).show();

                                        EditEmail.dismiss();

                                    } else {

                                        Toast.makeText(
                                                requireContext(),
                                                "Não foi possível solicitar a alteração do e-mail",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        Log.e(
                                                "FragmentoPerfil",
                                                "Erro ao solicitar alteração de e-mail",
                                                emailTask.getException()
                                        );
                                    }
                                });
                    });
        });

        EditEmail.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT)
        );

        EditEmail.show();
    }

    public void showPasswordPopup(View view) {

        Button btnModifyPassword;
        EditText edtCurrentPassword;
        EditText edtNewPassword;
        EditText edtConfirmNewPassword;

        EditPassword.setContentView(R.layout.popup_alterarsenha);

        btnModifyPassword = EditPassword.findViewById(R.id.btnModifyPassword);
        edtCurrentPassword = EditPassword.findViewById(R.id.edtCurrentPassword);
        edtNewPassword = EditPassword.findViewById(R.id.edtNewPassword);
        edtConfirmNewPassword = EditPassword.findViewById(R.id.edtConfirmNewPassword);

        btnModifyPassword.setOnClickListener(v -> {

            String senhaInformada =
                    edtCurrentPassword.getText().toString();

            String novaSenha =
                    edtNewPassword.getText().toString();

            String confirmacaoSenha =
                    edtConfirmNewPassword.getText().toString();

            // Validação básica dos campos
            if (senhaInformada.isEmpty()
                    || novaSenha.isEmpty()
                    || confirmacaoSenha.isEmpty()) {

                Toast.makeText(
                        requireContext(),
                        "Preencha todos os campos",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            // Validação de confirmação
            if (!novaSenha.equals(confirmacaoSenha)) {

                Toast.makeText(
                        requireContext(),
                        "As novas senhas não coincidem",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            // Validação mínima; o Firebase pode exigir
            // uma política de senha mais restritiva.
            if (novaSenha.length() < 6) {

                Toast.makeText(
                        requireContext(),
                        "A nova senha deve ter pelo menos 6 caracteres",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            FirebaseUser currentUser =
                    FirebaseAuth.getInstance().getCurrentUser();

            if (currentUser == null || currentUser.getEmail() == null) {

                Toast.makeText(
                        requireContext(),
                        "Usuário não autenticado",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            btnModifyPassword.setEnabled(false);

            // Usa a senha digitada pelo próprio usuário,
            // não uma senha recuperada do Firestore.
            AuthCredential credential =
                    EmailAuthProvider.getCredential(
                            currentUser.getEmail(),
                            senhaInformada
                    );

            currentUser.reauthenticate(credential)
                    .addOnCompleteListener(authTask -> {

                        if (!authTask.isSuccessful()) {

                            btnModifyPassword.setEnabled(true);

                            if (isAdded()) {
                                Toast.makeText(
                                        requireContext(),
                                        "Não foi possível confirmar sua senha atual",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }

                            return;
                        }

                        // Só altera a senha depois da
                        // reautenticação bem-sucedida.
                        currentUser.updatePassword(novaSenha)
                                .addOnCompleteListener(updateTask -> {

                                    btnModifyPassword.setEnabled(true);

                                    if (!isAdded()) {
                                        return;
                                    }

                                    if (updateTask.isSuccessful()) {

                                        Toast.makeText(
                                                requireContext(),
                                                "Senha alterada com sucesso!",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        EditPassword.dismiss();

                                    } else {

                                        Toast.makeText(
                                                requireContext(),
                                                "Não foi possível alterar a senha",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        Log.e(
                                                "FragmentoPerfil",
                                                "Erro ao atualizar senha",
                                                updateTask.getException()
                                        );
                                    }
                                });
                    });
        });

        EditPassword.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT)
        );

        EditPassword.show();
    }

    public void showDeletePopup(View view) {

        DeleteAccount.setContentView(R.layout.popup_excluirconta);

        EditText edtEmail =
                DeleteAccount.findViewById(R.id.edtEmail);

        EditText edtPassword =
                DeleteAccount.findViewById(R.id.edtPassword);

        EditText edtConfirmPassword =
                DeleteAccount.findViewById(R.id.edtConfirmPassword);

        Button btnDeleteAccountPopUp =
                DeleteAccount.findViewById(R.id.btnDeleteAccountPopUp);

        btnDeleteAccountPopUp.setOnClickListener(v -> {

            String emailInformado =
                    edtEmail.getText().toString().trim();

            String senhaInformada =
                    edtPassword.getText().toString();

            String confirmacaoSenha =
                    edtConfirmPassword.getText().toString();

            FirebaseUser currentUser =
                    FirebaseAuth.getInstance().getCurrentUser();

            if (currentUser == null || currentUser.getEmail() == null) {

                Toast.makeText(
                        requireContext(),
                        "Usuário não autenticado",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            String emailAtual = currentUser.getEmail();

            if (emailInformado.isEmpty()
                    || senhaInformada.isEmpty()
                    || confirmacaoSenha.isEmpty()) {

                Toast.makeText(
                        requireContext(),
                        "Preencha todos os campos",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if (!emailInformado.equalsIgnoreCase(emailAtual)) {

                Toast.makeText(
                        requireContext(),
                        "O e-mail informado está incorreto",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if (!senhaInformada.equals(confirmacaoSenha)) {

                Toast.makeText(
                        requireContext(),
                        "As senhas informadas não coincidem",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            btnDeleteAccountPopUp.setEnabled(false);

            AuthCredential credential =
                    EmailAuthProvider.getCredential(
                            emailAtual,
                            senhaInformada
                    );

            currentUser.reauthenticate(credential)
                    .addOnCompleteListener(task -> {

                        btnDeleteAccountPopUp.setEnabled(true);

                        if (!isAdded()) {
                            return;
                        }

                        if (task.isSuccessful()) {

                            DeleteAccount.dismiss();

                            showWarningPopup();

                        } else {

                            Toast.makeText(
                                    requireContext(),
                                    "Não foi possível confirmar suas credenciais",
                                    Toast.LENGTH_SHORT
                            ).show();

                            Log.e(
                                    "FragmentoPerfil",
                                    "Falha na reautenticação para exclusão",
                                    task.getException()
                            );
                        }
                    });
        });

        DeleteAccount.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT)
        );

        DeleteAccount.show();
    }
    public void showWarningPopup() {

        Warning.setContentView(R.layout.popup_aviso);

        Button btnReturn =
                Warning.findViewById(R.id.btnReturn);

        Button btnConfirm =
                Warning.findViewById(R.id.btnConfirm);

        TextView txtCount =
                Warning.findViewById(R.id.txtCount);

        btnConfirm.setEnabled(false);

        btnConfirm.setBackgroundDrawable(
                ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.custom_button_warning
                )
        );

        new CountDownTimer(6000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

                txtCount.setText(
                        String.valueOf(
                                millisUntilFinished / 1000
                        )
                );
            }

            @Override
            public void onFinish() {

                txtCount.setText("");

                btnConfirm.setEnabled(true);

                btnConfirm.setBackgroundDrawable(
                        ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.custom_button
                        )
                );
            }

        }.start();

        btnReturn.setOnClickListener(v -> {

            Warning.dismiss();

        });

        btnConfirm.setOnClickListener(v -> {

            btnConfirm.setEnabled(false);

            FirebaseUser currentUser =
                    FirebaseAuth.getInstance()
                            .getCurrentUser();

            if (currentUser == null) {

                showDeleteError(
                        "Usuário não autenticado",
                        null
                );

                return;
            }

            boolean googleAccount = false;

            for (com.google.firebase.auth.UserInfo provider
                    : currentUser.getProviderData()) {

                if ("google.com".equals(provider.getProviderId())) {

                    googleAccount = true;
                    break;
                }
            }

            if (googleAccount) {

                reauthenticateGoogleForDeletion();

            } else {

                // Para e-mail/senha, a reautenticação
                // já ocorreu em showDeletePopup().

                deleteUserAccount();
            }
        });

        Warning.show();

        if (Warning.getWindow() != null) {

            Warning.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT)
            );
        }
    }
    private void Exit() {

        FirebaseUser currentUser = mAuth.getCurrentUser();

        boolean googleAccount = Login.GoogleLogin == 1;

        if (currentUser != null) {

            for (com.google.firebase.auth.UserInfo provider
                    : currentUser.getProviderData()) {

                if ("google.com".equals(provider.getProviderId())) {

                    googleAccount = true;
                    break;
                }
            }
        }

        // Encerra a sessão do Firebase Authentication.
        mAuth.signOut();

        Login.GoogleLogin = 0;

        if (googleAccount) {

            GoogleSignInOptions gso =
                    new GoogleSignInOptions.Builder(
                            GoogleSignInOptions.DEFAULT_SIGN_IN
                    ).build();

            GoogleSignInClient googleSignInClient =
                    GoogleSignIn.getClient(
                            requireActivity(),
                            gso
                    );

            googleSignInClient.signOut()
                    .addOnCompleteListener(task -> {

                        if (!task.isSuccessful()) {

                            Log.w(
                                    "FragmentoPerfil",
                                    "Falha ao encerrar sessão Google",
                                    task.getException()
                            );
                        }

                        openLoginAfterLogout();
                    });

        } else {

            openLoginAfterLogout();
        }
    }
    private void openLoginAfterLogout() {

        if (!isAdded()) {
            return;
        }

        Intent intent = new Intent(
                requireActivity(),
                Login.class
        );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
    }

    private void exitDelete() {

        Exit();

    }
    private void uploadImageToFirebase(byte[] imageByte) {
        StorageReference fileRef = mStorage.child(FirebaseAuth.getInstance().getCurrentUser().getUid()+"/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+".png");
        fileRef.putBytes(imageByte).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getActivity().getApplicationContext(), "Imagem enviada com sucesso", Toast.LENGTH_SHORT);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity().getApplicationContext(), "Erro ao enviar imagem", Toast.LENGTH_SHORT);
            }
        });
    }
}


