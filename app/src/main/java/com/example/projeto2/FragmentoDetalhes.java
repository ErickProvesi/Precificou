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
import com.google.firebase.firestore.ListenerRegistration;
import android.util.Log;
import android.widget.Toast;
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
    private ListenerRegistration produtoListener;

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

        produtoListener = db.collection("Produto")
                .document(FragmentoProduto.produtoID)
                .addSnapshotListener((documentSnapshot, error) -> {
                    if (error != null) {
                        Log.e("FragmentoDetalhes", "Falha ao acompanhar o produto", error);
                        return;
                    }
                    if (documentSnapshot == null || !documentSnapshot.exists() || !isAdded()) return;

                    totalIngredientes = PrecoUtils.numero(documentSnapshot.getDouble("totalIngredientes"));
                    totalOutrosCustos = PrecoUtils.numero(documentSnapshot.getDouble("totalOutrosCustos"));
                    totalProduto = totalIngredientes + totalOutrosCustos;
                    rendimento = PrecoUtils.numero(documentSnapshot.getDouble("rendimento"));
                    margemLucro = PrecoUtils.numero(documentSnapshot.getDouble("margemLucro"));

                    txtValueTotalIngredients.setText(PrecoUtils.moeda(totalIngredientes));
                    txtValueTotalOtherCost.setText(PrecoUtils.moeda(totalOutrosCustos));
                    txtValueProductCost.setText(PrecoUtils.moeda(totalProduto));
                    txtValueProductUnitCost.setText(rendimento > 0
                            ? PrecoUtils.moeda(totalProduto / rendimento) : "Defina o rendimento");
                    edtRecipeYield.setText(rendimento > 0
                            ? PrecoUtils.edicao(rendimento) + " und" : "Definir rendimento");
                    edtProfitMarginPercentage.setText(PrecoUtils.edicao(margemLucro) + " %");
                    txtValueProfit.setText(PrecoUtils.moeda(totalProduto * margemLucro / 100.0));
                });

        return view;
    }

    public void showPopupAddYield() {
        EditText edtValueRendimento;
        Button btnSaveRendimento;

        addRendimento.setContentView(R.layout.popup_addrendimento);

        edtValueRendimento = addRendimento.findViewById(R.id.edtValueRendimento);
        btnSaveRendimento = addRendimento.findViewById(R.id.btnSaveRendimento);

        btnSaveRendimento.setOnClickListener(view -> {
            double valor;
            try { valor = PrecoUtils.parse(edtValueRendimento.getText().toString()); }
            catch (NumberFormatException e) { edtValueRendimento.setError("Informe um rendimento válido"); return; }
            if (valor <= 0) { edtValueRendimento.setError("O rendimento deve ser maior que zero"); return; }
            db.collection("Produto").document(FragmentoProduto.produtoID)
                    .update("rendimento", valor)
                    .addOnSuccessListener(unused -> addRendimento.dismiss())
                    .addOnFailureListener(e -> {
                        Log.e("FragmentoDetalhes", "Erro ao salvar rendimento", e);
                        if (isAdded()) Toast.makeText(requireContext(), "Erro ao salvar rendimento", Toast.LENGTH_SHORT).show();
                    });
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

        btnSaveMargemLucro.setOnClickListener(view -> {
            double valor;
            try { valor = PrecoUtils.parse(edtValueMargemLucro.getText().toString()); }
            catch (NumberFormatException e) { edtValueMargemLucro.setError("Informe uma margem válida"); return; }
            if (valor < 0) { edtValueMargemLucro.setError("A margem não pode ser negativa"); return; }
            db.collection("Produto").document(FragmentoProduto.produtoID)
                    .update("margemLucro", valor)
                    .addOnSuccessListener(unused -> addMargemLucro.dismiss())
                    .addOnFailureListener(e -> {
                        Log.e("FragmentoDetalhes", "Erro ao salvar margem", e);
                        if (isAdded()) Toast.makeText(requireContext(), "Erro ao salvar margem", Toast.LENGTH_SHORT).show();
                    });
        });



        addMargemLucro.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addMargemLucro.show();
    }

    @Override
    public void onDestroyView() {
        if (produtoListener != null) { produtoListener.remove(); produtoListener = null; }
        super.onDestroyView();
    }
}
