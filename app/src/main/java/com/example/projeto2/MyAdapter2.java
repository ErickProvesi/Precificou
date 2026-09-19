package com.example.projeto2;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class MyAdapter2 extends RecyclerView.Adapter<MyAdapter2.MyViewHolder>{

    Context context;
    ArrayList<Produto> list2;
    public static int delete = 0;
    public static int position2;
    double totalProductValue;
    StorageReference mStorage = FirebaseStorage.getInstance().getReference();

    SelectListener listener;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Dialog confirmDeleteProduct;

    public MyAdapter2(Context context, ArrayList<Produto> list2){
        this.context = context;
        this.list2 = list2;
    }
    public MyAdapter2(Context context, ArrayList<Produto> list2, SelectListener listener) {
        this.context = context;
        this.list2 = list2;
        this.listener = listener;
    }

    @NonNull

    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_produto,parent,false);
        confirmDeleteProduct = new Dialog(context.getApplicationContext());

        return new MyViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        final long ONE_MEGABYTE = 768 * 768;

        Produto produto = list2.get(position);
        holder.NameProduct.setText(produto.getNomeProduto());

        holder.imgProductPhoto.setBackground(null);

        StorageReference PhotoReference = mStorage.child(FragmentoProduto.userID+"/Produtos/"+produto.getIdProduto()+".png");
        System.out.println("ID PRODUTO "+produto.getIdProduto());

        PhotoReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                bmp.compress(Bitmap.CompressFormat.JPEG, 15, out);
                holder.imgProductPhoto.setImageBitmap(bmp);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        double ingredientes = PrecoUtils.numero(produto.getTotalIngredientes());
        double outros = PrecoUtils.numero(produto.getTotalOutrosCustos());
        holder.ValueProduct.setText(PrecoUtils.moedaSemSimbolo(
                PrecoUtils.precoFinal(ingredientes, outros, produto.getMargemLucro())));

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) listener.onItemClicked(produto);
            }
        });

        holder.DeleteProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete++;
                if (listener != null) listener.onItemClicked(produto);
                position2 = holder.getAbsoluteAdapterPosition();
            }
        });

    }

    @Override
    public int getItemCount() {
        return list2.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView NameProduct, ValueProduct;
        ImageView DeleteProduct, imgProductPhoto;
        public CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProductPhoto = itemView.findViewById(R.id.imgProductPhoto);
            DeleteProduct = itemView.findViewById(R.id.DeleteProduct);
            NameProduct = itemView.findViewById(R.id.NameProduct);
            ValueProduct = itemView.findViewById(R.id.ValueProduct);
            cardView = itemView.findViewById(R.id.card_produto);
        }
    }

}

