package com.example.projeto2;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FragmentoProduto extends Fragment implements SelectListener{

    private Dialog addProduct;
    public static int voltou = 0;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public static String userID;
    public static String produtoID;
    public static String nomeProduto;
    public static int count5=0;
    ArrayList<Produto> listSearchProduct =  new ArrayList<Produto>();
    SearchView schMyProducts;
    StorageReference mStorage = FirebaseStorage.getInstance().getReference();
    Dialog confirmDeleteProduct;
    public static int i =0;
    KeyListener listener;
    int position;

    RecyclerView recyclerView2;
    public static MyAdapter2 myAdapter2;
    public static ArrayList<Produto> list2;
    FirebaseFirestore db2;
    private com.google.firebase.firestore.ListenerRegistration produtosListener;
    byte[] imageByte;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_principal, container, false);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return view;
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();


        Button btnAddProduct = view.findViewById(R.id.btnAddProduct);
        schMyProducts = view.findViewById(R.id.schMyProducts);
        addProduct = new Dialog(getActivity());
        confirmDeleteProduct = new Dialog(getActivity());
        i = 0;

        voltou = 0;

        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup();
            }
        });

        recyclerView2 = view.findViewById(R.id.MeusProdutos);
        recyclerView2.setHasFixedSize(true);
        recyclerView2.setLayoutManager(new LinearLayoutManager(getActivity()));

        db2 = FirebaseFirestore.getInstance();
        list2 = new ArrayList<Produto>();
        myAdapter2 = new MyAdapter2(getActivity(),list2,this);

        schMyProducts.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(s.equals("")){
                    myAdapter2 = new MyAdapter2(getActivity(),list2,FragmentoProduto.this);
                }else {
                    filtroPesquisaProduto(s);
                    myAdapter2 = new MyAdapter2(getActivity(), listSearchProduct,FragmentoProduto.this);

                }
                recyclerView2.setLayoutManager(new GridLayoutManager(myAdapter2.context, 2));
                recyclerView2.setAdapter(myAdapter2);
                return true;
            }

        });

        recyclerView2.setLayoutManager(new GridLayoutManager(myAdapter2.context, 2));
        recyclerView2.setAdapter(myAdapter2);
        EventChangListerner();

        return view;

    }

    @Override
    public void onItemClicked(Ingrediente ingrediente) {

    }

    @Override
    public void onItemClicked(Produto produto) {

        if (produto == null || produto.getIdProduto() == null) {
            return;
        }

        produtoID = produto.getIdProduto();
        nomeProduto = produto.getNomeProduto();

        if (MyAdapter2.delete == 0) {

            Intent intent = new Intent(
                    requireActivity(),
                    Perfil_Produto.class
            );

            startActivity(intent);

        } else {

            showConfirmDeletePopup();

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (i == 1){

            getActivity().finish();
            getActivity().overridePendingTransition(0, 0);
            startActivity(getActivity().getIntent());
            getActivity().overridePendingTransition(0, 0);
        }else {

        }
    }

    @Override
    public void onItemClicked(OutrosCustos outrosCustos) {

    }




    @Override
    public void onItemClicked(ProdutoIng produtoIng) {

    }

    public void showPopup() {

        Button btnSaveProduct;
        EditText edtNameProduct, edtQuantity,edtValueElectricity,edtValueCookingGas,edtValueGasoline,edtOtherValues;
        ProgressBar pgbAddProduct;




        addProduct.setContentView(R.layout.popup_adicionarproduto);

        edtNameProduct = addProduct.findViewById(R.id.edtNameProduct);
        btnSaveProduct = addProduct.findViewById(R.id.btnSaveProduct);
        pgbAddProduct = addProduct.findViewById(R.id.pgbAddProduct);


        btnSaveProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtNameProduct.getText().toString().isEmpty()) {
                    edtNameProduct.setError("NÃ£o deixe o campo vazio");

                }else {

                    edtNameProduct.clearFocus();
                    edtNameProduct.setFocusable(false);
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    listener = btnSaveProduct.getKeyListener();
                    btnSaveProduct.setKeyListener(null);
                    btnSaveProduct.setText("Aguarde");
                    btnSaveProduct.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.custom_button_warning));

                    pgbAddProduct.setVisibility(View.VISIBLE);

                    userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    String UidProduto = UUID.randomUUID().toString();


                    Map<String, Object> product = new HashMap<>();

                    product.put("idUsuario", userID);

                    product.put("nomeProduto",edtNameProduct.getText().toString());

                    product.put("idProduto", UidProduto);


                    DocumentReference documentReference = db.collection("Produto").document(UidProduto);
                    documentReference.set(product).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d("db", "Sucesso ao salvar os dados");

                                    Bitmap bm = BitmapFactory.decodeResource( getResources(), R.drawable.logo_precificou);

                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    bm.compress(Bitmap.CompressFormat.PNG, 10,stream);
                                    imageByte = stream.toByteArray();

                                    StorageReference fileRef = mStorage.child(FirebaseAuth.getInstance().getCurrentUser().getUid()+"/"+"Produtos"+"/"+UidProduto+".png");
                                    fileRef.putBytes(imageByte);



                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            pgbAddProduct.setVisibility(View.INVISIBLE);
                                            addProduct.dismiss();
                                            ViewPager.count = 1;
                                            getActivity().finish();
                                            getActivity().overridePendingTransition(0, 0);
                                            startActivity(getActivity().getIntent());
                                            getActivity().overridePendingTransition(0, 0);
                                        }
                                    }, 1500);

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("db_error" ,"Erro ao salvar os dados" + e.toString());
                                }
                            });
                }
            }
        });
        addProduct.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addProduct.show();
    }

    public void EventChangListerner() {
        produtosListener = db.collection("Produto").whereEqualTo("idUsuario", userID)
                .addSnapshotListener((value, error) -> {
                    if (error != null) { Log.e("FragmentoProduto", "Erro ao ouvir produtos", error); return; }
                    if (value == null || list2 == null || myAdapter2 == null) return;
                    list2.clear();
                    for (com.google.firebase.firestore.DocumentSnapshot doc : value.getDocuments()) {
                        list2.add(doc.toObject(Produto.class));
                    }
                    if (schMyProducts != null && !schMyProducts.getQuery().toString().isEmpty()) {
                        filtroPesquisaProduto(schMyProducts.getQuery().toString());
                    }
                    myAdapter2.notifyDataSetChanged();
                });
    }

    @Override
    public void onDestroyView() {
        if (produtosListener != null) { produtosListener.remove(); produtosListener = null; }
        super.onDestroyView();
    }

    public void filtroPesquisaProduto(String s) {
        listSearchProduct.clear();

        for (Produto produto : list2) {

            if (produto.getNomeProduto().toLowerCase().contains(s.toLowerCase())) {
                System.out.println(produto.getNomeProduto());
                listSearchProduct.add(produto);
            }
        }
    }
    public void showConfirmDeletePopup(){

        MyAdapter2.delete = 0;

        Button btnConfirmdeleteProduct, btnReturnDeleteProduct;

        confirmDeleteProduct.setContentView(R.layout.popup_confirmacao_excluirproduto);

        btnConfirmdeleteProduct = confirmDeleteProduct.findViewById(R.id.btnConfirmDeleteProduct);
        btnReturnDeleteProduct = confirmDeleteProduct.findViewById(R.id.btnReturnDeleteProduct);

        FirebaseUser currentUser =
                FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(
                    requireContext(),
                    "Sua sessão expirou. Faça login novamente.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        btnReturnDeleteProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDeleteProduct.dismiss();
            }
        });

        btnConfirmdeleteProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("Produto").document(produtoID).delete();
                StorageReference fileRef = mStorage.child(FirebaseAuth.getInstance().getCurrentUser().getUid()+"/"+"Produtos"+"/"+FragmentoProduto.produtoID+".png");
                fileRef.delete();


                db.collection("ListaIngrediente")
                        .whereEqualTo(
                                "idUsuario",
                                FirebaseAuth.getInstance().getCurrentUser().getUid()
                        )
                        .whereArrayContains("idProduto", produtoID)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override

                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        System.out.println("Executou");
                        for(QueryDocumentSnapshot document : task.getResult()){
                            document.getReference().update("idProduto", FieldValue.arrayRemove(produtoID));
                        }
                    }
                });

                db.collection("OutrosCustos")
                        .whereEqualTo(
                                "idUsuario",
                                FirebaseAuth.getInstance().getCurrentUser().getUid()
                        )
                        .whereEqualTo("idProduto", produtoID)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        for (QueryDocumentSnapshot document : task.getResult()){

                            document.getReference().delete();
                        }
                        confirmDeleteProduct.dismiss();
                    }
                });
            }
        });

        confirmDeleteProduct.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        confirmDeleteProduct.show();



    }


}