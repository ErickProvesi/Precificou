package com.erickprovesi.precificou;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;
    ArrayList<Ingrediente> list;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private SelectListener listener;

    public MyAdapter(Context context, ArrayList<Ingrediente> list){
        this.context = context;
        this.list = list;
    }
    public MyAdapter(Context context, ArrayList<Ingrediente> list, SelectListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_ingrediente,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Ingrediente ingrediente = list.get(position);
        holder.NameMyIngredient.setText(ingrediente.getNomeIngrediente());
        holder.ValueMyIngredient.setText("R$ "+ingrediente.getPrecoIngrediente());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClicked(list.get(position));

            }
        });


    }

    public void deleteItem(int position, String ingrediente2) {

        if (position < 0 || position >= list.size()) {
            return;
        }

        Ingrediente ingrediente = list.get(position);
        String ingredienteID = ingrediente.getIdIngrediente();

        if (ingredienteID == null || ingredienteID.trim().isEmpty()) {

            // Devolve o card à posição original se não puder excluir.
            notifyItemChanged(position);
            return;
        }

        // Remove da interface imediatamente, acompanhando o swipe.
        list.remove(position);
        notifyItemRemoved(position);

        // Depois solicita a exclusão no Firebase.
        db.collection("ListaIngrediente")
                .document(ingredienteID)
                .delete()

                .addOnSuccessListener(unused -> {

                    Log.d(
                            "MyAdapter",
                            "Ingrediente excluído: " + ingredienteID
                    );

                    // Não removemos da lista novamente.
                    // O card já desapareceu no momento do swipe.
                })

                .addOnFailureListener(e -> {

                    Log.e(
                            "MyAdapter",
                            "Erro ao excluir ingrediente",
                            e
                    );

                    // Restaura o ingrediente se o Firebase rejeitar.
                    int posicaoRestaurada = Math.min(
                            position,
                            list.size()
                    );

                    list.add(posicaoRestaurada, ingrediente);
                    notifyItemInserted(posicaoRestaurada);

                    android.widget.Toast.makeText(
                            context,
                            "Não foi possível excluir o ingrediente",
                            android.widget.Toast.LENGTH_SHORT
                    ).show();
                });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView NameMyIngredient, ValueMyIngredient;
        public CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            NameMyIngredient = itemView.findViewById(R.id.NameMyIngredient);
            ValueMyIngredient = itemView.findViewById(R.id.ValueMyIngredient);
            cardView = itemView.findViewById(R.id.card_ingrediente);

        }
    }

}
