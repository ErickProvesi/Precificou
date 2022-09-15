package com.example.projeto2;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

public class FragmentoPerfil extends Fragment {

    private Dialog EditEmail, EditPassword, DeleteAccount, Warning;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_perfil, container, false);

        Button btnEditEmail = view.findViewById(R.id.btnEditEmail);
        Button btnEditPassword = view.findViewById(R.id.btnEditPassword);
        Button btnDeleteAccount = view.findViewById(R.id.btnDeleteAccount);
        ImageView imgProfilePic = view.findViewById(R.id.imgProfilePic);

        EditEmail = new Dialog(getActivity());
        EditPassword = new Dialog(getActivity());
        DeleteAccount = new Dialog(getActivity());
        Warning = new Dialog(getActivity());

        btnEditEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEmailPopup(view);
            }
        });

        btnEditPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPasswordPopup(view);
            }
        });

        btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeletePopup(view);
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
