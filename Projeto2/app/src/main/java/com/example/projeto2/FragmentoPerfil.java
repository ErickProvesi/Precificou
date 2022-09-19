package com.example.projeto2;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class FragmentoPerfil extends Fragment {

    private KeyListener listener;

    private Dialog EditEmail, EditPassword, DeleteAccount, Warning;
    private String userID;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String senhaAtual;
    FirebaseUser user = mAuth.getCurrentUser();
    TextView txtEmailProfile;
    Button btnEditEmail, btnEditPassword, btnDeleteAccount, btnExitAccount;
    ImageView imgProfilePic, imgEditName, imgProfileCheck;
    EditText edtProfileName;
    String email = mAuth.getCurrentUser().getEmail();

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

        listener = edtProfileName.getKeyListener();

        edtProfileName.setKeyListener(null);


        EditEmail = new Dialog(getActivity());
        EditPassword = new Dialog(getActivity());
        DeleteAccount = new Dialog(getActivity());
        Warning = new Dialog(getActivity());

        userID = mAuth.getCurrentUser().getUid();

        txtEmailProfile.setText(mAuth.getCurrentUser().getEmail());

        db.collection("Usuario").whereEqualTo("idUsuario", userID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {

                        senhaAtual = document.getString("senhaUsuario");

                    }

                }
            }
        });

        db.collection("Usuario").whereEqualTo("idUsuario", userID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {

                       edtProfileName.setText(document.getString("nomeUsuario"));

                    }

                }
            }
        });

        btnExitAccount.setOnClickListener(view14 -> {
            Exit();
        });


        btnEditEmail.setOnClickListener(view13 -> showEmailPopup(view13));

        btnEditPassword.setOnClickListener(view12 -> showPasswordPopup(view12));

        btnDeleteAccount.setOnClickListener(view1 -> showDeletePopup(view1));

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
                            db.collection("Usuario").document(userID).update("nomeUsuario", edtProfileName.getText().toString());
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

                    db.collection("Usuario").document(userID).update("senhaUsuario", edtNewPassword.getText().toString());

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
        Button btnReturn, btnConfirm;

        Warning.setContentView(R.layout.popup_aviso);

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
                db.collection("Usuario").document(userID).delete();
                mAuth.getCurrentUser().delete();
                exitDelete();
            }
        });

        Warning.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Warning.show();

    }

    private void Exit() {
        Intent intent = new Intent(getActivity(), Login.class);
        mAuth.signOut();
        startActivity(intent);
    }

    private void exitDelete() {
        Intent intent = new Intent(getActivity(), Login.class);
        mAuth.signOut();
        startActivity(intent);
    }

}


