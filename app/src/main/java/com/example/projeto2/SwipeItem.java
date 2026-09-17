package com.example.projeto2;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SwipeItem extends ItemTouchHelper.SimpleCallback {



    MyAdapter mItemAdapter;
    ArrayList<Ingrediente> listIngredient2;



    SwipeItem(MyAdapter myAdapter, ArrayList<Ingrediente> listIngredient2){
        super(0, ItemTouchHelper.LEFT);
        this.mItemAdapter = myAdapter;
        this.listIngredient2 = listIngredient2;

    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getBindingAdapterPosition();

//      Log.i("teste",mItemAdapter.teste3("2"));
        mItemAdapter.deleteItem(position, listIngredient2.get(viewHolder.getBindingAdapterPosition()).getNomeIngrediente());



    }

}
