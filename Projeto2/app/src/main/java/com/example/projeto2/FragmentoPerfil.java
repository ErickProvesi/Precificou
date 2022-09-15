package com.example.projeto2;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.text.method.KeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.ItemTouchUIUtil;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class FragmentoPerfil extends Fragment {

    private KeyListener listener;

    private Dialog EditEmail, EditPassword, DeleteAccount, Warning;
    private String userID;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_perfil, container, false);

        Button btnEditEmail = view.findViewById(R.id.btnEditEmail);
        Button btnEditPassword = view.findViewById(R.id.btnEditPassword);
        Button btnDeleteAccount = view.findViewById(R.id.btnDeleteAccount);
        ImageView imgProfilePic = view.findViewById(R.id.imgProfilePic);
        ImageView imgEditName = view.findViewById(R.id.imgEditName);
        TextView txtEmailProfile = view.findViewById(R.id.txtEmailProfile);
        EditText edtProfileName = view.findViewById(R.id.edtProfileName);
        ImageView imgProfileCheck = view.findViewById(R.id.imgProfileCheck);

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

                       edtProfileName.setText(document.getString("nomeUsuario"));

                    }

                }
            }
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
                DeleteAccount.dismiss();
                showWarningPopup();
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

        Warning.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Warning.show();

    }

}


