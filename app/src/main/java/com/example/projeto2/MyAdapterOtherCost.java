package com.example.projeto2;

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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MyAdapterOtherCost extends RecyclerView.Adapter<MyAdapterOtherCost.MyViewHolder>{

    Context context;
    ArrayList<OutrosCustos> listOtherCost;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private SelectListener listener;

    public MyAdapterOtherCost(Context context, ArrayList<OutrosCustos> listOtherCost) {
        this.context = context;
        this.listOtherCost = listOtherCost;
    }

    public MyAdapterOtherCost(Context context, ArrayList<OutrosCustos> listOtherCost, SelectListener listener){
        this.context = context;
        this.listOtherCost = listOtherCost;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_other_cost,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        OutrosCustos outrosCustos = listOtherCost.get(position);

        holder.txtValueOC1.setText(PrecoUtils.moedaSemSimbolo(outrosCustos.getValorCusto()));
        holder.txtOtherCost1.setText(outrosCustos.getNomeCusto());

        holder.cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClicked(outrosCustos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listOtherCost.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtOtherCost1, txtValueOC1;
        public CardView cardView1;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtOtherCost1 = itemView.findViewById(R.id.txtOtherCost1);
            txtValueOC1 = itemView.findViewById(R.id.txtValueOC1);
            cardView1 = itemView.findViewById(R.id.card_outros_custos);
        }
    }
    public void deleteItemOtherCost(int position, String custo) {

        if (position < 0 || position >= listOtherCost.size()) {
            return;
        }

        OutrosCustos outrosCustos = listOtherCost.get(position);

        String custoID = outrosCustos.getIdCusto();

        if (custoID == null || custoID.trim().isEmpty()) {

            Log.e("MyAdapterOtherCost", "Custo sem identificador");

            Toast.makeText(
                    context,
                    "Não foi possível identificar o custo",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        db.collection("OutrosCustos")
                .document(custoID)
                .delete()
                .addOnSuccessListener(unused -> {
                    String uid = com.google.firebase.auth.FirebaseAuth.getInstance()
                            .getCurrentUser() == null ? "" :
                            com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid();
                    String pid = outrosCustos.getIdProduto();
                    if (!uid.isEmpty() && pid != null) {
                        PrecificacaoRepository.atualizarOutrosCustos(db, uid, pid)
                                .addOnFailureListener(e -> Log.e("MyAdapterOtherCost", "Erro ao atualizar total", e));
                    }
                    // A lista é atualizada pelo listener em FragmentoReceita.
                })
                .addOnFailureListener(e -> {
                    Log.e("MyAdapterOtherCost", "Erro ao excluir custo", e);
                    if (position >= 0 && position < listOtherCost.size()) notifyItemChanged(position); // restaura o swipe
                    Toast.makeText(context, "Não foi possível excluir o custo", Toast.LENGTH_SHORT).show();
                });
    }
}
