package com.example.projeto2;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class FragmentoDetalhes extends Fragment {

    Button btnIncome;
    TextView txtValueTotalIngredients, txtValueTotalOtherCost, txtValueProductCost, txtValueProductUnitCost, txtValueProfit;
    TextView edtRecipeYield, edtProfitMarginPercentage;
    public static double totalIngredientes, totalOutrosCustos, totalProduto, rendimento, margemLucro;

    private Dialog addRendimento, addMargemLucro;
    DecimalFormat df = new DecimalFormat("#,###.##");


    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_perfil_produto_detalhes, container, false);

        btnIncome = view.findViewById(R.id.btnIncome);
        txtValueTotalIngredients = view.findViewById(R.id.txtValueTotalIngredients);
        txtValueTotalOtherCost = view.findViewById(R.id.txtValueTotalOtherCost);
        txtValueProductCost = view.findViewById(R.id.txtValueProductCost);
        edtRecipeYield = view.findViewById(R.id.edtRecipeYield);
        addRendimento = new Dialog(getActivity());
        addMargemLucro = new Dialog(getActivity());
        txtValueProductUnitCost = view.findViewById(R.id.txtValueProductUnitCost);
        edtProfitMarginPercentage = view.findViewById(R.id.edtProfitMarginPercentage);
        txtValueProfit = view.findViewById(R.id.txtValueProfit);

        btnIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentoReceita.count2 = 0;
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                FragmentoReceita fragReceita = new FragmentoReceita();
                transaction.replace(R.id.fltPerfilProduto, fragReceita);
                transaction.commit();
            }
        });

        edtRecipeYield.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupAddYield();
            }
        });

        edtProfitMarginPercentage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupAddPercentage();
            }
        });

        db.collection("Produto").document(FragmentoProduto.produtoID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.getDouble("totalIngredientes") == null) {

                }else {
                    totalIngredientes = documentSnapshot.getDouble("totalIngredientes");
                    txtValueTotalIngredients.setText("R$ "+df.format(totalIngredientes));
                }
                if (documentSnapshot.getDouble("totalOutrosCustos") == null) {

                }else {
                    totalOutrosCustos = documentSnapshot.getDouble("totalOutrosCustos");
                    txtValueTotalOtherCost.setText("R$ "+df.format(totalOutrosCustos));
                }
                if (documentSnapshot.getDouble("totalIngredientes") == null && documentSnapshot.getDouble("totalOutrosCustos") == null){

                }else {
                    totalProduto = totalIngredientes+totalOutrosCustos;
                    txtValueProductCost.setText("R$ "+df.format(totalProduto));
                }
                if (documentSnapshot.getDouble("rendimento") == null) {

                }else {
                    rendimento = documentSnapshot.getDouble("rendimento");
                    edtRecipeYield.setText((int) (rendimento)+" und");
                }
                txtValueProductUnitCost.setText("R$ "+df.format(totalProduto/rendimento));
                if (documentSnapshot.getDouble("margemLucro") == null) {

                }else {
                    margemLucro = documentSnapshot.getDouble("margemLucro");
                    edtProfitMarginPercentage.setText((int) (margemLucro)+" %");
                }
                txtValueProfit.setText("R$ "+(df.format(totalProduto*(margemLucro/100))));
            }
        });

        return view;
    }

    public void showPopupAddYield() {
        EditText edtValueRendimento;
        Button btnSaveRendimento;

        addRendimento.setContentView(R.layout.popup_addrendimento);

        edtValueRendimento = addRendimento.findViewById(R.id.edtValueRendimento);
        btnSaveRendimento = addRendimento.findViewById(R.id.btnSaveRendimento);

        btnSaveRendimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("Produto").document(FragmentoProduto.produtoID).update("rendimento",Double.parseDouble(edtValueRendimento.getText().toString()));
                addRendimento.dismiss();
                FragmentoReceita.count2 = 1;
                getActivity().finish();
                getActivity().overridePendingTransition(0, 0);
                startActivity(getActivity().getIntent());
                getActivity().overridePendingTransition(0, 0);
            }
        });
        addRendimento.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addRendimento.show();
    }

    public void showPopupAddPercentage() {
        EditText edtValueMargemLucro;
        Button btnSaveMargemLucro;

        addMargemLucro.setContentView(R.layout.popup_addmargemlucro);

        edtValueMargemLucro = addMargemLucro.findViewById(R.id.edtValueMargemLucro);
        btnSaveMargemLucro = addMargemLucro.findViewById(R.id.btnSaveMargemLucro);

        btnSaveMargemLucro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("Produto").document(FragmentoProduto.produtoID).update("margemLucro",Double.parseDouble(edtValueMargemLucro.getText().toString()));
                addMargemLucro.dismiss();
                FragmentoReceita.count2 = 1;
                getActivity().finish();
                getActivity().overridePendingTransition(0, 0);
                startActivity(getActivity().getIntent());
                getActivity().overridePendingTransition(0, 0);
            }
        });


        addMargemLucro.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addMargemLucro.show();
    }

}
