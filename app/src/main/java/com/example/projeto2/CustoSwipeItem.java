package com.example.projeto2;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustoSwipeItem extends ItemTouchHelper.SimpleCallback {

    MyAdapterOtherCost myAdapterOtherCost;
    ArrayList<OutrosCustos> listOtherCost;

    CustoSwipeItem(MyAdapterOtherCost myAdapterOtherCost, ArrayList<OutrosCustos> listOtherCost){
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.myAdapterOtherCost = myAdapterOtherCost;
        this.listOtherCost = listOtherCost;

    }


    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        int position = viewHolder.getBindingAdapterPosition();
        myAdapterOtherCost.deleteItemOtherCost(position, listOtherCost
                .get(viewHolder.getBindingAdapterPosition()).getNomeCusto());

    }
}
