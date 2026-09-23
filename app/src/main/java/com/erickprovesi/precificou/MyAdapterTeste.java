package com.erickprovesi.precificou;

import static com.erickprovesi.precificou.R.drawable.custom_button_profile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapterTeste extends RecyclerView.Adapter<MyAdapterTeste.MyViewHolder> {

    Context context;
    ArrayList<Ingrediente> list;
    public static int adicionado=0;
    public static int tirou=0;

    private SelectListener listener;

    public MyAdapterTeste(Context context, ArrayList<Ingrediente> list){
        this.context = context;
        this.list = list;
    }

    public MyAdapterTeste(Context context, ArrayList<Ingrediente> list, SelectListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_btningrediente,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Ingrediente ingrediente = list.get(position);
        holder.txtCardNameMyIngredient.setText(ingrediente.getNomeIngrediente());
        holder.btnAddIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                System.out.println("Fundo aqui  " + holder.btnAddIngredient.getBackgroundTintMode());

                if (holder.btnAddIngredient.getBackgroundTintMode() == null) {
                holder.btnAddIngredient.setBackground(ContextCompat.getDrawable(context, R.drawable.card));
                holder.LinearCard.setBackground(ContextCompat.getDrawable(context, R.drawable.card));
                listener.onItemClicked(list.get(position));
                holder.btnAddIngredient.setBackgroundTintMode(PorterDuff.Mode.ADD);
                tirou = 1;
                adicionado = 0;

                 }
                else if (holder.btnAddIngredient.getBackgroundTintMode().toString().equalsIgnoreCase("add")) {

                    holder.btnAddIngredient.setBackground(ContextCompat.getDrawable(context, R.drawable.card_clicado));
                    holder.LinearCard.setBackground(ContextCompat.getDrawable(context, R.drawable.card_clicado));
                    holder.btnAddIngredient.setBackgroundTintMode(null);
                    listener.onItemClicked(list.get(position));
                    adicionado = 1;
                    tirou = 0;

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public  static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtCardNameMyIngredient;
        public CardView btnAddIngredient;
        LinearLayout LinearCard;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtCardNameMyIngredient = itemView.findViewById(R.id.txtCardNameMyIngredient);
            btnAddIngredient = itemView.findViewById(R.id.btnAddIngredient);
            LinearCard = itemView.findViewById(R.id.LinearCard);
        }
    }
}
