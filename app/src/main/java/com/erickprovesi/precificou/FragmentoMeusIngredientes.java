package com.erickprovesi.precificou;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FragmentoMeusIngredientes extends Fragment implements SelectListener{

    private Dialog createIngredient,editIngredient;
    private Button btnAddIngredient;
    private SearchView schMyIngredients;
    public static String UidIngredient;
    public static String ingredientID;

    public static String nomeIngrediente2, undIngrediente;
    public static String quantitadeIngrediente, precoIngrediente;

    RecyclerView recyclerView;
    MyAdapter myAdapter;
    ArrayList<Ingrediente> list;
    ArrayList<Ingrediente> listaPesquisa = new ArrayList<Ingrediente>();
    FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_meus_ingredientes, container, false);

        btnAddIngredient = view.findViewById(R.id.btnAddIngredient);
        schMyIngredients = view.findViewById(R.id.schMyIngredients);
        createIngredient = new Dialog(getActivity());
        editIngredient = new Dialog(getActivity());


        btnAddIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreateIngredientPopUp();

            }
        });

        recyclerView = view.findViewById(R.id.MeusIngredientes);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManage(getContext(), LinearLayoutManager.VERTICAL, false));


        db = FirebaseFirestore.getInstance();
        list = new ArrayList<Ingrediente>();
        myAdapter = new MyAdapter(getActivity(),list, this);

        schMyIngredients.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(s.equals("")){
                    myAdapter = new MyAdapter(getActivity(), list);
                }
                else{
                    filtroPesquisa(s);
                    myAdapter = new MyAdapter(getActivity(),listaPesquisa);
                }

                recyclerView.setAdapter(myAdapter);
                return true;
            }
        });

        recyclerView.setAdapter(myAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeItem(myAdapter, list));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        EventChangListerner();

        return view;

    }

    public void showCreateIngredientPopUp() {
        Button btnCreate;
        Spinner SpinnerUnd;
        EditText edtIngredientName, edtIngredientPrice, edtIngredientQuantity;

        createIngredient.setContentView(R.layout.popup_criaringrediente);

        btnCreate = createIngredient.findViewById(R.id.btnCreate);
        edtIngredientName = createIngredient.findViewById(R.id.edtIngredientName);
        edtIngredientPrice = createIngredient.findViewById(R.id.edtIngredientPrice);
        edtIngredientQuantity = createIngredient.findViewById((R.id.edtQuantityIngredient));
        SpinnerUnd = createIngredient.findViewById(R.id.SpinnerUnd);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (edtIngredientName.getText().toString().isEmpty()) {
                    edtIngredientName.setError("O Nome é Obrigatório");
                } else {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    UidIngredient = UUID.randomUUID().toString();

                    Map<String, Object> ListaIngrediente = new HashMap<>();
                    ListaIngrediente.put("nomeIngrediente", edtIngredientName.getText().toString());
                    ListaIngrediente.put("precoIngrediente", edtIngredientPrice.getText().toString());
                    ListaIngrediente.put("qtdIngrediente", edtIngredientQuantity.getText().toString());
                    ListaIngrediente.put("unidade", SpinnerUnd.getSelectedItem().toString());
                    ListaIngrediente.put("idIngrediente", UidIngredient);
                    ListaIngrediente.put("idUsuario", FragmentoProduto.userID);
                    ListaIngrediente.put("idProduto", Arrays.asList());

                    DocumentReference documentReference = db.collection("ListaIngrediente").document(UidIngredient);
                    documentReference.set(ListaIngrediente).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d("db", "Sucesso ao salvar os dados");
                                    createIngredient.dismiss();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("db_error", "Erro ao salvar os dados" + e.toString());
                                }
                            });
                }

            }
        });
        createIngredient.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        createIngredient.show();
    }

    public void showEditIngredientPopUp() {
        Button btnEdit;
        Spinner SpinnerEditUnd;
        EditText edtEditNameIngredient, edtEditIngredientPrice, edtEditQuantityIngredient;

        editIngredient.setContentView(R.layout.popup_editingrediente);

        btnEdit = editIngredient.findViewById(R.id.btnEdit);
        edtEditNameIngredient = editIngredient.findViewById(R.id.edtEditNameIngredient);
        edtEditIngredientPrice = editIngredient.findViewById(R.id.edtEditIngredientPrice);
        edtEditQuantityIngredient = editIngredient.findViewById((R.id.edtEditQuantityIngredient));
        SpinnerEditUnd = editIngredient.findViewById(R.id.SpinnerEditUnd);

        edtEditNameIngredient.setText(nomeIngrediente2);
        if (!precoIngrediente.isEmpty()){
            edtEditIngredientPrice.setText(precoIngrediente);
        }
        if (!quantitadeIngrediente.isEmpty()){
            edtEditQuantityIngredient.setText(quantitadeIngrediente);
        }

        for (int i =0; i <= SpinnerEditUnd.getAdapter().getCount();) {
            if (undIngrediente.equals(SpinnerEditUnd.getItemAtPosition(i).toString())) {
                SpinnerEditUnd.setSelection(i);
                break;
            }else {
                i++;
            }

        }


        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (edtEditNameIngredient.getText().toString().isEmpty()) {
                    edtEditNameIngredient.setError("O Nome é Obrigatório");
                } else{
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    Map<String, Object> EditIngrediente = new HashMap<>();
                    EditIngrediente.put("nomeIngrediente", edtEditNameIngredient.getText().toString());
                    EditIngrediente.put("precoIngrediente", edtEditIngredientPrice.getText().toString());
                    EditIngrediente.put("qtdIngrediente", edtEditQuantityIngredient.getText().toString());
                    EditIngrediente.put("unidade", SpinnerEditUnd.getSelectedItem().toString());

                    db.collection("ListaIngrediente").document(ingredientID).update(EditIngrediente);
                    editIngredient.dismiss();
                    ViewPager.count = 2;
                    getActivity().finish();
                    getActivity().overridePendingTransition(0, 0);
                    startActivity(getActivity().getIntent());
                    getActivity().overridePendingTransition(0, 0);
                }
            }
        });
        editIngredient.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        editIngredient.show();
    }


    private void EventChangListerner() {

        db.collection("ListaIngrediente").whereEqualTo("idUsuario", FragmentoProduto.userID)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null){

                            Log.e("Firestore error",error.getMessage());
                            return;
                        }

                        for (DocumentChange dc : value.getDocumentChanges()){
                            if (dc.getType() == DocumentChange.Type.ADDED){
                                list.add(dc.getDocument().toObject(Ingrediente.class));
                            }
                            myAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    @Override
    public void onItemClicked(Ingrediente ingrediente) {
        nomeIngrediente2 = ingrediente.getNomeIngrediente();
        precoIngrediente = ingrediente.getPrecoIngrediente();
        undIngrediente = ingrediente.getUnidade();
        quantitadeIngrediente = ingrediente.getQtdIngrediente();
        ingredientID = ingrediente.getIdIngrediente();

        showEditIngredientPopUp();

    }

    @Override
    public void onItemClicked(Produto produto) {

    }

    @Override
    public void onItemClicked(OutrosCustos outrosCustos) {

    }

    @Override
    public void onItemClicked(ProdutoIng produtoIng) {

    }

    public void filtroPesquisa(String s) {

        listaPesquisa.clear();
        for (Ingrediente ingrediente : list) {
            if (ingrediente.getNomeIngrediente().toLowerCase().contains(s.toLowerCase())) {
                listaPesquisa.add(ingrediente);
            }
        }
    }

}