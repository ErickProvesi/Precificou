package com.example.projeto2;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Principal extends FragmentActivity {

    FloatingActionButton btnAddProduct;
    private Button btnBackLogin, btnGoProfile;
    public static int voltou = 0;
    private Dialog addProduct;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        addProduct = new Dialog(this);

        voltou = 0;

        btnBackLogin = findViewById(R.id.btnBackLogin);
        btnAddProduct = findViewById(R.id.btnAddProduct);
        btnGoProfile = findViewById(R.id.btnGoProfile);

        btnBackLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnLogin();
            }
        });

        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(view);
            }
        });

        btnGoProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goProfile();
            }
        });

    }
    private void returnLogin() {
        voltou++;
        Intent voltarLogin = new Intent(Principal.this, Login.class);
        startActivity(voltarLogin);
        finish();

    }
    public void showPopup(View view) {

        Button btnSaveProduct;
        EditText edtNameProduct;
        addProduct.setContentView(R.layout.popup_adicionarproduto);

        edtNameProduct = addProduct.findViewById(R.id.edtNameProduct);
        btnSaveProduct = addProduct.findViewById(R.id.btnSaveProduct);

        btnSaveProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtNameProduct.getText().toString().isEmpty()) {
                    edtNameProduct.setError("Não deixe o campo vazio");

                }else {

                    userID = FirebaseAuth.getInstance().getCurrentUser().getUid();


                    Map<String, Object> product = new HashMap<>();
                    product.put("capacidadeBotijao", 0);
                    product.put("consumoQueimadorGas",0);
                    product.put("descricaoProduto",0);
                    product.put("fotoProduto",0);
                    product.put("idUsuario", userID);
                    product.put("idProduto",0);
                    product.put("kmLitroVeiculo",0);
                    product.put("kmRodado",0);
                    product.put("margemLucro",0);
                    product.put("nomeProduto",edtNameProduct.getText().toString());
                    product.put("outroCusto",0);
                    product.put("precoBotijao",0);
                    product.put("precoCombustivel",0);
                    product.put("tempoUsoEnergia",0);
                    product.put("tempoUsoGas",0);
                    product.put("valorKWH",0);

                    DocumentReference documentReference = db.collection("Produto").document(UUID.randomUUID().toString());
                    documentReference.set(product).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d("db", "Sucesso ao salvar os dados");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("db_error" ,"Erro ao salvar os dados" + e.toString());
                                }
                            });
                }
                }
        });
        addProduct.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addProduct.show();
    }

    private void goProfile() {
        Intent intent = new Intent(Principal.this, Perfil.class);
        startActivity(intent);
    }


}