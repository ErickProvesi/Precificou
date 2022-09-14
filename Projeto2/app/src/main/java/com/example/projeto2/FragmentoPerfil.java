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

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

public class FragmentoPerfil extends Fragment {

    private  Dialog EditEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_perfil, container, false);

        Button btnEditEmail = view.findViewById(R.id.btnEditEmail);
        Button btnEditPassword = view.findViewById(R.id.btnEditPassword);
        Button btnDeleteAccount = view.findViewById(R.id.btnDeleteAccount);

        EditEmail = new Dialog(getActivity());

        btnEditEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEmailPopup(view);
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

}
