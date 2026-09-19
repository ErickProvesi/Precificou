package com.example.projeto2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MyAdapterProdutoIng extends RecyclerView.Adapter<MyAdapterProdutoIng.MyViewHolder>{

    String txtQuantity2;
    String txtValue;
    String txtQuantity;
    String ingId;

    Context context;
    ArrayList<ProdutoIng> listProdIng;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    SelectListener listener;

    public MyAdapterProdutoIng(Context context, ArrayList<ProdutoIng> listProdIng) {
        this.context = context;
        this.listProdIng = listProdIng;
    }

    public MyAdapterProdutoIng(Context context, ArrayList<ProdutoIng> listProdIng, SelectListener listener) {
        this.context = context;
        this.listProdIng = listProdIng;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_ingrediente_receita,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        ProdutoIng produtoIng = listProdIng.get(position);
        holder.txtValueIngr1.setText(PrecoUtils.moeda(0));
        holder.txtQuantity1.setText("—");

        holder.txtIngr1.setTag(produtoIng.getIdIngrediente());
        db.collection("ListaIngrediente")
                .whereEqualTo(
                        "idUsuario",
                        FirebaseAuth.getInstance().getCurrentUser().getUid()
                )
                .whereArrayContains(
                        "idProduto",
                        FragmentoProduto.produtoID
                )
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override

            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (!task.isSuccessful() || task.getResult() == null ||
                        !produtoIng.getIdIngrediente().equals(holder.txtIngr1.getTag())) return;
                for(QueryDocumentSnapshot document : task.getResult()){

                    if (document.getString("idIngrediente").equals(produtoIng.idIngrediente)){
                        if (document.get(FragmentoProduto.produtoID+".quantidadeProd") != null && document.get(FragmentoProduto.produtoID+".tipoUnid") != null && document.get(FragmentoProduto.produtoID+".valorIngProd") != null) {
                             txtQuantity = document.get(FragmentoProduto.produtoID + ".quantidadeProd").toString();
                             txtQuantity2 = document.get(FragmentoProduto.produtoID + ".tipoUnid").toString();
                             txtValue = document.get(FragmentoProduto.produtoID+".valorIngProd").toString();
                            holder.txtValueIngr1.setText(PrecoUtils.moedaTexto(txtValue));
                            holder.txtQuantity1.setText(txtQuantity + " " + txtQuantity2);
                            break;

                        }else if (document.get(FragmentoProduto.produtoID+".quantidadeProd") != null && document.get(FragmentoProduto.produtoID+".valorIngProd") == null){
                            txtQuantity = document.get(FragmentoProduto.produtoID + ".quantidadeProd").toString();
                            txtQuantity2 = document.get(FragmentoProduto.produtoID + ".tipoUnid").toString();
                            holder.txtQuantity1.setText(txtQuantity + " " + txtQuantity2);
                            break;

                        }else if (document.get(FragmentoProduto.produtoID+".quantidadeProd") == null && document.get(FragmentoProduto.produtoID+".valorIngProd") != null){

                            txtQuantity2 = document.get(FragmentoProduto.produtoID + ".tipoUnid").toString();
                            txtValue = document.get(FragmentoProduto.produtoID+".valorIngProd").toString();
                            holder.txtValueIngr1.setText(PrecoUtils.moedaTexto(txtValue));
                            holder.txtQuantity1.setText("0" + " " + txtQuantity2);
                        }

                    }else {

                    }
                }



            }
        });

        holder.txtIngr1.setText(produtoIng.getNomeIngrediente());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClicked(listProdIng.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return listProdIng.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtIngr1,txtQuantity1,txtValueIngr1;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtIngr1=itemView.findViewById(R.id.txtIngr1);
            txtValueIngr1=itemView.findViewById(R.id.txtValueIngr1);
            txtQuantity1= itemView.findViewById(R.id.txtQuantity1);
            cardView=itemView.findViewById(R.id.card_produtoIng);

        }
    }

    public void deleteItemProdIng(int position, String ingredienteId) {
        if (com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser() == null ||
                ingredienteId == null || ingredienteId.trim().isEmpty()) return;
        String uid = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid();
        String pid = FragmentoProduto.produtoID;
        // Remove vínculo e o mapa de receita deste produto em uma única atualização.
        db.collection("ListaIngrediente").document(ingredienteId)
                .update("idProduto", FieldValue.arrayRemove(pid), pid, FieldValue.delete())
                .addOnSuccessListener(unused -> PrecificacaoRepository.atualizarIngredientes(db, uid, pid)
                        .addOnFailureListener(e -> Log.e("MyAdapterProdutoIng", "Erro ao somar ingredientes", e)))
                .addOnFailureListener(e -> {
                    Log.e("MyAdapterProdutoIng", "Erro ao remover ingrediente da receita", e);
                    notifyItemChanged(position);
                    Toast.makeText(context, "Não foi possível retirar o ingrediente", Toast.LENGTH_SHORT).show();
                });
        // A lista será refeita pelo listener de FragmentoReceita.
    }
}
