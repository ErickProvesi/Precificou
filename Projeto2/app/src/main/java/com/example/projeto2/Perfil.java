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

    public void ShowEmailPopup(View view){
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

    public void ShowPasswordPopup(View view) {
        Button btnModifyPassword;
        EditText edtCurrentPassword, edtNewPassword, edtConfirmNewPassword;

        ModifyPassword.setContentView(R.layout.popup_alterarsenha);

        btnModifyPassword = ModifyPassword.findViewById(R.id.btnModifyPassword);
        edtCurrentPassword = ModifyPassword.findViewById(R.id.edtCurrentPassword);
        edtNewPassword = ModifyPassword.findViewById(R.id.edtNewPassword);
        edtConfirmNewPassword = ModifyPassword.findViewById(R.id.edtConfirmNewPassword);

        ModifyPassword.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ModifyPassword.show();
    }
    public void ShowDeletePopup(View view) {
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
                ShowWarningPopup();
            }
        });

        DeleteAccount.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        DeleteAccount.show();

    }

    public void ShowWarningPopup() {
        Button btnReturn, btnConfirm;

        Warning.setContentView(R.layout.popup_aviso);

        btnReturn = Warning.findViewById(R.id.btnReturn);
        btnConfirm = Warning.findViewById(R.id.btnConfirm);

        Warning.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Warning.show();

    }
}