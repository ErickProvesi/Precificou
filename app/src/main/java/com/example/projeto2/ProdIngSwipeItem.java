package com.example.projeto2;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ProdIngSwipeItem extends ItemTouchHelper.SimpleCallback {

    public static String idIng55;
    MyAdapterProdutoIng myAdapterProdutoIng;
    ArrayList<ProdutoIng> listProdIng;

    public ProdIngSwipeItem(MyAdapterProdutoIng myAdapterProdutoIng, ArrayList<ProdutoIng> listProdIng) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.myAdapterProdutoIng = myAdapterProdutoIng;
        this.listProdIng = listProdIng;
    }


    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        int position = viewHolder.getBindingAdapterPosition();
        if (position == RecyclerView.NO_POSITION || position >= listProdIng.size()) return;
        myAdapterProdutoIng.deleteItemProdIng(position, listProdIng.get(position).getIdIngrediente());

    }
}
