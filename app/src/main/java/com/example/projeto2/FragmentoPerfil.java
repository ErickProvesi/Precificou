package com.example.projeto2;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.FileUtils;
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
import android.window.SplashScreen;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class FragmentoPerfil extends Fragment {

    KeyListener listener;
    Dialog EditEmail, EditPassword, DeleteAccount, Warning;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String senhaAtual="a";
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



            db.collection("Usuario").whereEqualTo("idUsuario", FragmentoProduto.userID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {

                        for (QueryDocumentSnapshot document : task.getResult()) {

                            senhaAtual = document.getString("senhaUsuario");
                        }
                    }
                }

            });

            db.collection("Usuario").whereEqualTo("idUsuario", FragmentoProduto.userID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {

                        for (QueryDocumentSnapshot document : task.getResult()) {

                            edtProfileName.setText(document.getString("nomeUsuario"));

                        }
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

                        Picasso.get().load(personPhoto).into(imgProfilePic);
                        System.out.println("Executou aqui");

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

        imgEditPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(intent, 1000);
            }
        });

        listener = edtProfileName.getKeyListener();

        edtProfileName.setKeyListener(null);

        EditEmail = new Dialog(getActivity());
        EditPassword = new Dialog(getActivity());
        DeleteAccount = new Dialog(getActivity());
        Warning = new Dialog(getActivity());

        txtEmailProfile.setText(mAuth.getCurrentUser().getEmail());

        db.collection("Usuario").whereEqualTo("idUsuario", FragmentoProduto.userID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {

                        senhaAtual = document.getString("senhaUsuario");

                    }

                }
            }
        });


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
    public void showEmailPopup(View view){
        Button btnModifyEmail;
        EditText edtCurrentEmail, edtNewEmail, edtConfirmNewEmail, edtPassword;

        EditEmail.setContentView(R.layout.popup_alteraremail);

        edtCurrentEmail = EditEmail.findViewById(R.id.edtCurrentEmail);
        btnModifyEmail = EditEmail.findViewById(R.id.btnModifyEmail);
        edtNewEmail = EditEmail.findViewById(R.id.edtNewEmail);
        edtConfirmNewEmail = EditEmail.findViewById(R.id.edtConfirmNewEmail);
        edtPassword = EditEmail.findViewById(R.id.edtPassword);

        btnModifyEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (edtCurrentEmail.getText().toString().equals(email) && edtPassword.getText().toString().equals(senhaAtual) && edtConfirmNewEmail.getText().toString().equals(edtNewEmail.getText().toString())) {

                    AuthCredential credential = EmailAuthProvider.getCredential(email, senhaAtual);

                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d("Sucesso", "Usuário re-autenticado");

                            user.updateEmail(edtNewEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("Sucesso", "Email Atualizado!");
                                        Snackbar snackbar = Snackbar.make(view,"E-mail alterado com Sucesso!",Snackbar.LENGTH_SHORT);
                                        snackbar.setBackgroundTint(Color.WHITE);
                                        snackbar.setTextColor(Color.BLACK);
                                        snackbar.show();
                                        txtEmailProfile.setText(mAuth.getCurrentUser().getEmail());
                                        email = edtNewEmail.getText().toString();
                                    }
                                }
                            });
                        }
                    });

                }else {
                    Snackbar snackbar = Snackbar.make(view,"Verifique os dados inseridos",Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                }
            }
        });


        EditEmail.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        EditEmail.show();
    }

    public void showPasswordPopup(View view) {
        Button btnModifyPassword;
        EditText edtCurrentPassword, edtNewPassword, edtConfirmNewPassword;

        EditPassword.setContentView(R.layout.popup_alterarsenha);

        btnModifyPassword = EditPassword.findViewById(R.id.btnModifyPassword);
        edtCurrentPassword = EditPassword.findViewById(R.id.edtCurrentPassword);
        edtNewPassword = EditPassword.findViewById(R.id.edtNewPassword);
        edtConfirmNewPassword = EditPassword.findViewById(R.id.edtConfirmNewPassword);

        btnModifyPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (edtCurrentPassword.getText().toString().equals(senhaAtual) && edtConfirmNewPassword.getText().toString().equals(edtNewPassword.getText().toString())) {

                    AuthCredential credential = EmailAuthProvider.getCredential(email, senhaAtual);

                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d("Sucesso", "Usuário re-autenticado");

                            user.updatePassword(edtNewPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("Sucesso", "Senha Atualizada!");
                                        Snackbar snackbar = Snackbar.make(view,"Senha alterada com Sucesso!",Snackbar.LENGTH_SHORT);
                                        snackbar.setBackgroundTint(Color.WHITE);
                                        snackbar.setTextColor(Color.BLACK);
                                        snackbar.show();
                                        senhaAtual = edtNewPassword.getText().toString();
                                    }
                                }
                            });
                        }
                    });

                    db.collection("Usuario").document(FragmentoProduto.userID).update("senhaUsuario", edtNewPassword.getText().toString());

                }else {
                    Snackbar snackbar = Snackbar.make(view,"Verifique os dados inseridos",Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                }
            }
        });

        EditPassword.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        EditPassword.show();
    }

    public void showDeletePopup(View view) {
        EditText edtEmail, edtPassword, edtConfirmPassword;
        Button btnDeleteAccountPopUp;

        DeleteAccount.setContentView(R.layout.popup_excluirconta);

        edtEmail = DeleteAccount.findViewById(R.id.edtEmail);
        edtPassword = DeleteAccount.findViewById(R.id.edtPassword);
        edtConfirmPassword = DeleteAccount.findViewById(R.id.edtConfirmPassword);
        btnDeleteAccountPopUp = DeleteAccount.findViewById(R.id.btnDeleteAccountPopUp);

        btnDeleteAccountPopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtEmail.getText().toString().equals(email) && edtPassword.getText().toString().equals(senhaAtual) && edtConfirmPassword.getText().toString().equals(edtPassword.getText().toString())) {
                    DeleteAccount.dismiss();
                    showWarningPopup();

                }else {
                    Snackbar snackbar = Snackbar.make(view,"Verifique os dados inseridos",Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                }
            }
        });

        DeleteAccount.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        DeleteAccount.show();

    }
    public void showWarningPopup() {
        if (Login.GoogleLogin == 1) {
            Button btnReturn, btnConfirm;
            TextView txtCount;
            Warning.setContentView(R.layout.popup_aviso);

            txtCount = Warning.findViewById(R.id.txtCount);
            btnReturn = Warning.findViewById(R.id.btnReturn);
            btnConfirm = Warning.findViewById(R.id.btnConfirm);
            btnConfirm.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.custom_button_warning));
            btnConfirm.setEnabled(false);
            System.out.println("Id usuario"+FragmentoProduto.userID);
            new CountDownTimer(6000, 1000) {
                @Override
                public void onTick(long l) {
                    txtCount.setText(""+l / 1000);
                }
                @Override
                public void onFinish() {
                    txtCount.setText("");
                    btnConfirm.setEnabled(true);
                    btnConfirm.setBackgroundDrawable(ContextCompat.getDrawable(getContext(),R.drawable.custom_button ));
                }
            }.start();
            btnReturn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Warning.dismiss();
                }
            });
            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    db.collection("Usuario").document(FragmentoProduto.userID).delete();

                    StorageReference referenceProfilePhoto = mStorage.child(FragmentoProduto.userID+"/"+FragmentoProduto.userID+".png");
                    referenceProfilePhoto.delete();

                    db.collection("Produto").whereEqualTo("idUsuario", FragmentoProduto.userID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            for (QueryDocumentSnapshot document : task.getResult()) {


                                StorageReference storageReference = mStorage.child(FragmentoProduto.userID+"/Produtos/"+document.getString("idProduto")+".png");
                                storageReference.delete();
                                document.getReference().delete();
                            }
                        }
                    });

                    db.collection("ListaIngrediente").whereEqualTo("idUsuario", FragmentoProduto.userID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                document.getReference().delete();
                            }
                        }
                    });

                    db.collection("OutrosCustos").whereEqualTo("idUsuario", FragmentoProduto.userID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                document.getReference().delete();
                            }
                        }
                    });

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mAuth.getCurrentUser().delete();
                            exitDelete();
                        }
                    }, 1000);


                }
            });
            Warning.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            Warning.show();
        }else {
            Button btnReturn, btnConfirm;
            Warning.setContentView(R.layout.popup_aviso);
            StorageReference storageReference = mStorage.child(FragmentoProduto.userID+"/");
            btnReturn = Warning.findViewById(R.id.btnReturn);
            btnConfirm = Warning.findViewById(R.id.btnConfirm);
            btnReturn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Warning.dismiss();
                    showDeletePopup(view);
                }
            });
            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    db.collection("Usuario").document(FragmentoProduto.userID).delete();

                    StorageReference referenceProfilePhoto = mStorage.child(FragmentoProduto.userID+"/"+FragmentoProduto.userID+".png");
                    referenceProfilePhoto.delete();

                    db.collection("Produto").whereEqualTo("idUsuario", FragmentoProduto.userID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                StorageReference storageReference = mStorage.child(FragmentoProduto.userID+"/Produtos/"+document.getString("idProduto")+".png");
                                storageReference.delete();
                                document.getReference().delete();
                            }
                        }
                    });

                    db.collection("ListaIngrediente").whereEqualTo("idUsuario", FragmentoProduto.userID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                document.getReference().delete();
                            }
                        }
                    });

                    db.collection("OutrosCustos").whereEqualTo("idUsuario", FragmentoProduto.userID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                document.getReference().delete();
                            }
                        }
                    });

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            mAuth.getCurrentUser().delete();
                            exitDelete();
                        }
                    }, 1000);
                }
            });
            Warning.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            Warning.show();
        }
    }
    private void Exit() {
        if (Login.GoogleLogin == 1) {
            mAuth.signOut();
            GoogleSignInOptions gso = new GoogleSignInOptions.
                    Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                    build();
            GoogleSignInClient googleSignInClient=GoogleSignIn.getClient(getContext(),gso);
            googleSignInClient.signOut();
            Intent intent = new Intent(getActivity(), Login.class);
            reload = 1;
            startActivity(intent);
            Login.GoogleLogin = 0;
            getActivity().finish();
        }else {
            Intent intent = new Intent(getActivity(), Login.class);
            mAuth.signOut();
            reload = 1;
            startActivity(intent);
            Login.GoogleLogin = 0;
            getActivity().finish();
        }
        }
    private void exitDelete() {
        if (Login.GoogleLogin == 1 ) {
            Intent intent = new Intent(getActivity(), Login.class);
            mAuth.signOut();
            GoogleSignInOptions gso = new GoogleSignInOptions.
                    Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                    build();
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(getContext(), gso);
            googleSignInClient.signOut();
            startActivity(intent);
            Login.GoogleLogin = 0;
            reload = 1;
            getActivity().finish();
        }else {
            Intent intent = new Intent(getActivity(), Login.class);
            mAuth.signOut();
            reload = 1;
            startActivity(intent);
            Login.GoogleLogin = 0;
            getActivity().finish();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000){
            if (resultCode == Activity.RESULT_OK){
                imgUri = data.getData();
                try {
                    Bitmap original = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),imgUri);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    original.compress(Bitmap.CompressFormat.JPEG, 30,stream);
                    imgProfilePic.setBackground(null);
                    imgProfilePic.setImageBitmap(original);
                    imageByte = stream.toByteArray();
                    uploadImageToFirebase(imageByte);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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


