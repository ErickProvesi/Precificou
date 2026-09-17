package com.example.projeto2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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

    public void deleteItem(int position, String ingrediente2 ) {


        db.collection("ListaIngrediente").whereEqualTo("nomeIngrediente", ingrediente2).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {


                for (QueryDocumentSnapshot document : task.getResult()) {

                    FragmentoMeusIngredientes.ingredientID = document.getString("idIngrediente");

                    db.collection("ListaIngrediente").document(FragmentoMeusIngredientes.ingredientID).delete();


                    System.out.print("ID INGREDIENTE" + FragmentoMeusIngredientes.ingredientID);


                }


            }

        });
        this.list.remove(position);
        notifyItemChanged(position);

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
