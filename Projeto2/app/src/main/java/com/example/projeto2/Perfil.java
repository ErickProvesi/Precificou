package com.example.projeto2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Perfil extends FragmentActivity {

    private  Dialog EditEmail, EditPassword, DeleteAccount, Warning;
    private  Button btnEditEmail, btnEditPassword, btnDeleteAccount;

    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        btnEditEmail = findViewById(R.id.btnEditEmail);
        btnEditPassword = findViewById(R.id.btnEditPassword);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);

        EditEmail = new Dialog(this);
        EditPassword = new Dialog(this);
        DeleteAccount = new Dialog(this);
        Warning = new Dialog(this);

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