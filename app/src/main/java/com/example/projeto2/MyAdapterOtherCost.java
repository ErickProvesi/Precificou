package com.example.projeto2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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

        holder.txtValueOC1.setText(String.valueOf(outrosCustos.getValorCusto()));
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
    public void deleteItemOtherCost(int position, String custo ) {


        db.collection("OutrosCustos").whereEqualTo("nomeCusto", custo).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {


                for (QueryDocumentSnapshot document : task.getResult()) {

                    document.getReference().delete();

                    System.out.print("ID CUSTO" + FragmentoReceita.outroCustoID);

                }

            }

        });
        this.listOtherCost.remove(position);
        notifyItemChanged(position);
}
}
