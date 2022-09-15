package com.example.projeto2;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FragmentoProduto extends Fragment {

    private Dialog addProduct;
    public static int voltou = 0;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_principal, container, false);

        FloatingActionButton btnAddProduct = view.findViewById(R.id.btnAddProduct);

        addProduct = new Dialog(getActivity());

        voltou = 0;

        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(view);
            }
        });

        return view;

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
}