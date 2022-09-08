package com.example.projeto2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Principal extends AppCompatActivity {

    FloatingActionButton btnAddProduct;
    private Button btnVoltarLogin;
    public static int voltou = 0;
    private Dialog addProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);


        addProduct = new Dialog(this);


        voltou = 0;

        btnVoltarLogin = findViewById(R.id.btnVoltarLogin);
        btnAddProduct = findViewById(R.id.btnAddProduct);


        btnVoltarLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VoltarLogin();
            }
        });

        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.S)
            @Override
            public void onClick(View view) {
                ShowPopup(view);
            }
        });

    }
    private void VoltarLogin() {
        voltou++;
        Intent voltarLogin = new Intent(Principal.this, Login.class);
        startActivity(voltarLogin);
        finish();

    }
    @RequiresApi(api = Build.VERSION_CODES.S)
    public void ShowPopup(View view) {

        Button btnSaveProduct;
        EditText edtNameProduct;
        addProduct.setContentView(R.layout.custom_popup);

        edtNameProduct = addProduct.findViewById(R.id.edtNameProduct);
        btnSaveProduct = addProduct.findViewById(R.id.btnSaveProduct);

        btnSaveProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtNameProduct.getText().toString().isEmpty()) {
                    edtNameProduct.setError("Não deixe o campo vazio");

                }else {

                }
            }
        });
        addProduct.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addProduct.show();
    }
}