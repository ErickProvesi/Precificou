package com.example.projeto2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Perfil extends AppCompatActivity {

    private Dialog ModifyEmail, ModifyPassword, DeleteAccount, Warning;
    private Button btnModifyEmail, btnModifyPassword, btnDeleteAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        btnModifyEmail = findViewById(R.id.btnModifyEmail);
        btnModifyPassword = findViewById(R.id.btnModifyPassword);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);

        ModifyEmail = new Dialog(this);
        ModifyPassword = new Dialog(this);
        DeleteAccount = new Dialog(this);
        Warning = new Dialog(this);

        btnModifyEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowEmailPopup(view);
            }
        });

        btnModifyPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowPasswordPopup(view);
            }
        });

        btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowDeletePopup(view);
            }
        });

    }

    private void ShowEmailPopup(View view){
        Button btnModifyEmail;
        EditText edtCurrentEmail, edtNewEmail, edtConfirmNewEmail, edtPassword;

        ModifyEmail.setContentView(R.layout.popup_alteraremail);

        edtCurrentEmail = ModifyEmail.findViewById(R.id.edtCurrentEmail);
        btnModifyEmail = ModifyEmail.findViewById(R.id.btnModifyEmail);
        edtNewEmail = ModifyEmail.findViewById(R.id.edtNewEmail);
        edtConfirmNewEmail = ModifyEmail.findViewById(R.id.edtConfirmNewEmail);
        edtPassword = ModifyEmail.findViewById(R.id.edtPassword);


        ModifyEmail.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ModifyEmail.show();
    }

    private void ShowPasswordPopup(View view) {
        Button btnModifyPassword;
        EditText edtCurrentPassword, edtNewPassword, edtConfirmNewPassword;

        ModifyPassword.setContentView(R.layout.popup_alterarsenha);

        btnModifyPassword = findViewById(R.id.btnModifyPassword);
        edtCurrentPassword = findViewById(R.id.edtCurrentPassword);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmNewPassword = findViewById(R.id.edtConfirmNewPassword);

        ModifyPassword.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ModifyPassword.show();
    }

    private void ShowDeletePopup(View view) {
        Button btnDeleteAccount;
        EditText edtEmail, edtPassword, edtConfirmPassword;

        DeleteAccount.setContentView(R.layout.popup_excluirconta);

        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);

        btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowWarningPopup(view);
            }
        });

        DeleteAccount.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        DeleteAccount.show();
    }

    private void ShowWarningPopup(View view) {
        Button btnReturn, btnConfirm;

        Warning.setContentView(R.layout.popup_aviso);

        btnReturn = findViewById(R.id.btnReturn);
        btnConfirm = findViewById(R.id.btnConfirm);

        Warning.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Warning.show();

    }
}