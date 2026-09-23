package com.erickprovesi.precificou;

import android.app.AlertDialog;
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
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FragmentoReceita extends Fragment implements SelectListener{

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Dialog insertOrCalculateValue, calculateElectricity, calculateGasoline;
    private Dialog calculateCookingGas;
    private Dialog addingredient, addOtherCost;
    double result;
    private Dialog editElectricity, editGasoline, editCookingGas, editOtherCost, editQuantity;
    Button btnDetails;
    ImageView imgAddIngredient,imgAddOtherCost;
    public static String UidOutrosCustos,UidElectricity,UidGasoline,UidCookingGas;
    int count=0;
    public static String nomeCusto,nomeIngrediente,undIngrediente;
    public static String custoID, idIngrediente;

    public static String outroCustoID;

    String valueIngrediente, quantityIng;
    double resultadoIng;

    private double workedHours, kwhValues;
    private double editworkedHours, editkwhValues;

    private double kmTraveled, kmLiter, fuelPrice;
    private double editkmTraveled, editkmLiter, editfuelPrice;

    private double usageTime, burnerConsumption, cylinderCapacity, gasPrice;
    private double editusageTime, editburnerConsumption, editcylinderCapacity, editgasPrice;

    private double valueElectricity, valueCookingGas, valueGasoline, otherValues;
    private double totalIngredientes;
    private double editvalueElectricity, editvalueCookingGas, editvalueGasoline, editotherValues;

    MyAdapterTeste myAdapterTeste;
    ArrayList<Ingrediente> list, listProdIng2;
    ArrayList<ProdutoIng> listProdIng;
    ArrayList<String> IdIng = new ArrayList<String>();
    MyAdapterProdutoIng myAdapterProdutoIng;
    MyAdapterOtherCost myAdapterOtherCost;
    public static int count2=0;
    ArrayList<OutrosCustos> listOtherCost;
    RecyclerView RywOtherCost,RywIngredientProd;
    private ListenerRegistration custosListener, ingredientesListener;

    ArrayList<Ingrediente> listaPesquisa2 = new ArrayList<Ingrediente>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.perfil_produto_receita, container, false);



        if (count2 == 1) {
            ViewPager.count = 0;
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            FragmentoDetalhes fragDetalhes = new FragmentoDetalhes();
            transaction.replace(R.id.fltPerfilProduto, fragDetalhes);
            transaction.commit();
        }

        insertOrCalculateValue = new Dialog(getActivity());
        calculateElectricity = new Dialog(getActivity());
        calculateGasoline = new Dialog(getActivity());
        calculateCookingGas = new Dialog(getActivity());
        addingredient = new Dialog(getActivity());
        addOtherCost = new Dialog(getActivity());
        editElectricity = new Dialog(getActivity());
        editGasoline = new Dialog(getActivity());
        editCookingGas = new Dialog(getActivity());
        editOtherCost = new Dialog(getActivity());
        editQuantity = new Dialog(getActivity());
        btnDetails = view.findViewById(R.id.btnDetails);
        imgAddIngredient = view.findViewById(R.id.imgAddIngredient);
        imgAddOtherCost = view.findViewById(R.id.imgAddOtherCost);

        btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                FragmentoDetalhes fragDetalhes = new FragmentoDetalhes();
                transaction.replace(R.id.fltPerfilProduto, fragDetalhes);
                transaction.commit();
            }
        });


        imgAddIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopUpAddIngrediente();
            }
        });

        imgAddOtherCost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInsertOrCalculatePopup();
            }
        });

        RywOtherCost = view.findViewById(R.id.RywOtherCost);
        RywOtherCost.setHasFixedSize(true);
        RywOtherCost.setLayoutManager(new WrapContentLinearLayoutManage(getContext(), LinearLayoutManager.VERTICAL, false));
        listOtherCost = new ArrayList<OutrosCustos>();
        myAdapterOtherCost = new MyAdapterOtherCost(getActivity(), listOtherCost, this);
        RywOtherCost.setAdapter(myAdapterOtherCost);

        RywIngredientProd = view.findViewById(R.id.RywIngredientProd);
        RywIngredientProd.setHasFixedSize(true);
        RywIngredientProd.setLayoutManager(new WrapContentLinearLayoutManage(getContext(), LinearLayoutManager.VERTICAL, false));
        listProdIng = new ArrayList<ProdutoIng>();
        myAdapterProdutoIng = new MyAdapterProdutoIng(getActivity(), listProdIng, this);
        RywIngredientProd.setAdapter(myAdapterProdutoIng);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new CustoSwipeItem(myAdapterOtherCost, listOtherCost));
        itemTouchHelper.attachToRecyclerView(RywOtherCost);

        ItemTouchHelper itemTouchHelper2 = new ItemTouchHelper(new ProdIngSwipeItem(myAdapterProdutoIng, listProdIng));
        itemTouchHelper2.attachToRecyclerView(RywIngredientProd);

        EventChangListerner2();
        EventChangListerner3();

        return view;
    }


    public void showInsertOrCalculatePopup() {

        Button btnCalculateElectricity, btnCalculateCookingGas, btnCalculateGasoline;
        Button btnSaveOtherCost;
        EditText edtValueElectricity, edtValueCookingGas, edtValueGasoline;
        EditText edtOtherValues, edtNameOther;

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;
        final String uid = currentUser.getUid();
        final String pid = FragmentoProduto.produtoID;

        insertOrCalculateValue.setContentView(R.layout.popup_outroscustos);


        btnCalculateElectricity = insertOrCalculateValue.findViewById(R.id.btnCalculateEletricity);
        btnCalculateCookingGas = insertOrCalculateValue.findViewById(R.id.btnCalculateCookingGas);
        btnCalculateGasoline = insertOrCalculateValue.findViewById(R.id.btnCalculateGasoline);
        btnSaveOtherCost = insertOrCalculateValue.findViewById(R.id.btnSaveOtherCost);
        edtValueElectricity = insertOrCalculateValue.findViewById(R.id.edtValueElectricity);
        edtValueCookingGas = insertOrCalculateValue.findViewById(R.id.edtValueCookingGas);
        edtValueGasoline = insertOrCalculateValue.findViewById(R.id.edtValueGasoline);
        edtOtherValues = insertOrCalculateValue.findViewById(R.id.edtOtherValues);
        edtNameOther = insertOrCalculateValue.findViewById(R.id.edtNameOther);

        if(valueElectricity != 0){
            edtValueElectricity.setText(PrecoUtils.edicao(valueElectricity));
        }

        if(valueGasoline!= 0){
            edtValueGasoline.setText(PrecoUtils.edicao(valueGasoline));
        }

        if(valueCookingGas != 0){
            edtValueCookingGas.setText(PrecoUtils.edicao(valueCookingGas));
        }

        if (otherValues != 0){
            edtOtherValues.setText(PrecoUtils.edicao(otherValues));
        }


        btnCalculateElectricity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertOrCalculateValue.dismiss();
                showCalculateElectricityPopup();

            }
        });

        btnCalculateCookingGas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertOrCalculateValue.dismiss();
                showCalculateCookingGasPopup();

            }
        });

        btnCalculateGasoline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertOrCalculateValue.dismiss();
                showCalculateGasolinePopup();

            }
        });

        btnSaveOtherCost.setOnClickListener(v -> {
            WriteBatch batch = db.batch();
            int adicionados = 0;
            try {
                if (!edtValueElectricity.getText().toString().trim().isEmpty()) {
                    double valor = PrecoUtils.parse(edtValueElectricity.getText().toString());
                    if (valor < 0) throw new NumberFormatException("Eletricidade negativa");
                    UidElectricity = UUID.randomUUID().toString();
                    Map<String, Object> custo = new HashMap<>();
                    custo.put("nomeCusto", "Eletricidade");
                    custo.put("valorCusto", valor);
                    custo.put("idProduto", pid);
                    custo.put("idUsuario", uid);
                    custo.put("idCusto", UidElectricity);
                    batch.set(db.collection("OutrosCustos").document(UidElectricity), custo);
                    adicionados++;
                }
                if (!edtValueCookingGas.getText().toString().trim().isEmpty()) {
                    double valor = PrecoUtils.parse(edtValueCookingGas.getText().toString());
                    if (valor < 0) throw new NumberFormatException("Gás negativo");
                    UidCookingGas = UUID.randomUUID().toString();
                    Map<String, Object> custo = new HashMap<>();
                    custo.put("nomeCusto", "Gás");
                    custo.put("valorCusto", valor);
                    custo.put("idProduto", pid);
                    custo.put("idUsuario", uid);
                    custo.put("idCusto", UidCookingGas);
                    batch.set(db.collection("OutrosCustos").document(UidCookingGas), custo);
                    adicionados++;
                }
                if (!edtValueGasoline.getText().toString().trim().isEmpty()) {
                    double valor = PrecoUtils.parse(edtValueGasoline.getText().toString());
                    if (valor < 0) throw new NumberFormatException("Gasolina negativa");
                    UidGasoline = UUID.randomUUID().toString();
                    Map<String, Object> custo = new HashMap<>();
                    custo.put("nomeCusto", "Gasolina");
                    custo.put("valorCusto", valor);
                    custo.put("idProduto", pid);
                    custo.put("idUsuario", uid);
                    custo.put("idCusto", UidGasoline);
                    batch.set(db.collection("OutrosCustos").document(UidGasoline), custo);
                    adicionados++;
                }
                if (!edtOtherValues.getText().toString().trim().isEmpty()) {
                    double valor = PrecoUtils.parse(edtOtherValues.getText().toString());
                    if (valor < 0) throw new NumberFormatException("Custo negativo");
                    if (edtNameOther.getText().toString().trim().isEmpty()) {
                        edtNameOther.setError("Informe o nome do custo");
                        return;
                    }
                    UidOutrosCustos = UUID.randomUUID().toString();
                    Map<String, Object> custo = new HashMap<>();
                    custo.put("nomeCusto", edtNameOther.getText().toString().trim());
                    custo.put("valorCusto", valor);
                    custo.put("idProduto", pid);
                    custo.put("idUsuario", uid);
                    custo.put("idCusto", UidOutrosCustos);
                    batch.set(db.collection("OutrosCustos").document(UidOutrosCustos), custo);
                    adicionados++;
                }
            } catch (NumberFormatException e) {
                if (isAdded()) Toast.makeText(requireContext(), "Informe valores monetários válidos e não negativos", Toast.LENGTH_SHORT).show();
                return;
            }
            if (adicionados == 0) {
                if (isAdded()) Toast.makeText(requireContext(), "Informe pelo menos um custo", Toast.LENGTH_SHORT).show();
                return;
            }
            btnSaveOtherCost.setEnabled(false);
            batch.commit()
                    .addOnSuccessListener(unused -> {
                        PrecificacaoRepository.atualizarOutrosCustos(db, uid, pid)
                                .addOnFailureListener(e -> Log.e("Receita", "Falha ao atualizar soma dos custos", e));
                        valueElectricity = 0;
                        valueGasoline = 0;
                        valueCookingGas = 0;
                        otherValues = 0;
                        insertOrCalculateValue.dismiss();
                    })
                    .addOnFailureListener(e -> {
                        btnSaveOtherCost.setEnabled(true);
                        Log.e("Receita", "Falha ao cadastrar custos", e);
                        if (isAdded()) Toast.makeText(requireContext(), "Não foi possível salvar os custos", Toast.LENGTH_SHORT).show();
                    });
        });
        insertOrCalculateValue.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        insertOrCalculateValue.show();
    }


    public void showCalculateElectricityPopup() {
        EditText edtWorkedHours, edtKwhValues, edtPowerWatts;
        Button btnSaveEnergy;
        ImageView imgQuestionMarkElectricity;

        calculateElectricity.setContentView(R.layout.popup_energiaeletrica);

        edtWorkedHours = calculateElectricity.findViewById(R.id.edtWorkedHours);
        edtKwhValues = calculateElectricity.findViewById(R.id.edtKwhValues);
        edtPowerWatts = calculateElectricity.findViewById(R.id.edtPowerWatts);
        imgQuestionMarkElectricity = calculateElectricity.findViewById(R.id.imgQuestionMarkEletricity);
        btnSaveEnergy = calculateElectricity.findViewById(R.id.btnSaveEnergy);
        btnSaveEnergy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    workedHours = PrecoUtils.parse(edtWorkedHours.getText().toString());
                    kwhValues = PrecoUtils.parse(edtKwhValues.getText().toString());
                    double watts = PrecoUtils.parse(edtPowerWatts.getText().toString());
                    valueElectricity = PrecoUtils.custoEnergia(watts, workedHours, kwhValues);
                } catch (IllegalArgumentException e) {
                    if (isAdded()) Toast.makeText(requireContext(), "Preencha tempo, potência (W) e tarifa válida", Toast.LENGTH_LONG).show();
                    return;
                }

                calculateElectricity.dismiss();
                showInsertOrCalculatePopup();
            }
        });

        imgQuestionMarkElectricity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder msgE = new AlertDialog.Builder(calculateElectricity.getContext(),R.style.alert_dialog);
                msgE.setTitle("Onde consultar o kWh?");
                msgE.setMessage("Consulte o valor em R$ de 1 kWh em " +
                        "sua tarifa de energia elétrica ou  " +
                        "com a empresa concessionária de " +
                        "energia da sua região.");
                msgE.show();
            }
        });
        calculateElectricity.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        calculateElectricity.show();
    }


    public void showCalculateGasolinePopup() {
        EditText edtKmTraveled, edtKmLiter, edtFuelPrice;
        Button btnSaveFuel;
        ImageView imgQuestionMarkGasoline;

        calculateGasoline.setContentView(R.layout.popup_combustivel);

        edtKmTraveled = calculateGasoline.findViewById(R.id.edtKmTraveled);
        edtKmLiter = calculateGasoline.findViewById(R.id.edtKmLiter);
        edtFuelPrice = calculateGasoline.findViewById(R.id.edtFuelPrice);
        btnSaveFuel = calculateGasoline.findViewById(R.id.btnSaveFuel);
        imgQuestionMarkGasoline = calculateGasoline.findViewById(R.id.imgQuestionMarkGasoline);

        btnSaveFuel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                kmTraveled= PrecoUtils.parse(edtKmTraveled.getText().toString());
                kmLiter = PrecoUtils.parse(edtKmLiter.getText().toString());
                fuelPrice = PrecoUtils.parse(edtFuelPrice.getText().toString());

                if (kmLiter <= 0) {
                    if (isAdded()) Toast.makeText(requireContext(), "Consumo (km/l) deve ser maior que zero", Toast.LENGTH_LONG).show();
                    return;
                }
                valueGasoline = (kmTraveled/kmLiter)*fuelPrice;


                calculateGasoline.dismiss();
                showInsertOrCalculatePopup();

            }
        });

        imgQuestionMarkGasoline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder msgG = new AlertDialog.Builder(calculateGasoline.getContext(),R.style.alert_dialog);
                msgG.setTitle("Onde consultar o km/l do veículo?");
                msgG.setMessage("Consulte o computador de bordo do " +
                        "veículo para obter essa informação. " +
                        "Ou, abasteça-o por completo e zere " +
                        "seu hodômetro parcial. Então, no " +
                        "próximo abastecimento, anote o " +
                        "combustível que entrou no veículo, " +
                        "pela bomba do posto. Por fim, basta " +
                        "dividir os quilômetros marcados no  " +
                        "hodômetro pela quantidade de litros que " +
                        "a bomba fornece no segundo abastecimento.");
                msgG.show();
            }
        });
        calculateGasoline.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        calculateGasoline.show();
    }

    public void showCalculateCookingGasPopup() {
        EditText edtUsageTime,edtBurnerConsumption,edtCylinderCapacity,edtGasPrice;
        Button btnSaveGas;
        ImageView imgQuestionMarkGas, imgQuestionMarkGas2;

        calculateCookingGas.setContentView(R.layout.popup_gas);

        edtUsageTime= calculateCookingGas.findViewById(R.id.edtUsageTime);
        edtBurnerConsumption= calculateCookingGas.findViewById(R.id.edtBurnerConsumption);
        edtCylinderCapacity= calculateCookingGas.findViewById(R.id.edtCylinderCapacity);
        edtGasPrice= calculateCookingGas.findViewById(R.id.edtGasPrice);
        btnSaveGas = calculateCookingGas.findViewById(R.id.btnSaveGas);
        imgQuestionMarkGas = calculateCookingGas.findViewById(R.id.imgQuestinMarkGas);
        imgQuestionMarkGas2= calculateCookingGas.findViewById(R.id.imgQuestionMarkGas2);

        btnSaveGas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                usageTime = PrecoUtils.parse(edtUsageTime.getText().toString());
                burnerConsumption = PrecoUtils.parse(edtBurnerConsumption.getText().toString());
                cylinderCapacity = PrecoUtils.parse(edtCylinderCapacity.getText().toString());
                gasPrice = PrecoUtils.parse(edtGasPrice.getText().toString());

                if (cylinderCapacity <= 0) {
                    if (isAdded()) Toast.makeText(requireContext(), "Capacidade do botijão deve ser maior que zero", Toast.LENGTH_LONG).show();
                    return;
                }
                valueCookingGas = (((usageTime/60) * burnerConsumption)/cylinderCapacity) * gasPrice;


                calculateCookingGas.dismiss();
                showInsertOrCalculatePopup();

            }
        });

        imgQuestionMarkGas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder msgG = new AlertDialog.Builder(calculateCookingGas.getContext(),R.style.alert_dialog);
                msgG.setTitle("Onde consultar o consumo do queimador?");
                msgG.setMessage("Consulte o manual do fabricante " +
                        "do fogão. O valor é dado em kg/h e varia "+
                        "de acordo com a temperatura de consumo.");
                msgG.show();
            }
        });


        imgQuestionMarkGas2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder msgG = new AlertDialog.Builder(calculateCookingGas.getContext(),R.style.alert_dialog);
                msgG.setTitle("O que é a capacidade do botijão?");
                msgG.setMessage("Corresponde ao peso do modelo " +
                        "de botijão de GLP. Sendo 13 kg " +
                        "para o modelo P13 e 45 kg para " +
                        "o modelo P45.");
                msgG.show();
            }
        });
        calculateCookingGas.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        calculateCookingGas.show();
    }

    public void showPopUpAddIngrediente() {

        RecyclerView RywViewIngredient;
        Button btnSave;
        SearchView schMyIngredients2;

        addingredient.setContentView(R.layout.popup_addingredient);

        RywViewIngredient = addingredient.findViewById(R.id.RywViewIngredient);
        RywViewIngredient.setHasFixedSize(true);
        RywViewIngredient.setLayoutManager(new LinearLayoutManager(getActivity()));
        list = new ArrayList<Ingrediente>();
        myAdapterTeste = new MyAdapterTeste(getActivity(),list, this);
        RywViewIngredient.setAdapter(myAdapterTeste);

        btnSave = addingredient.findViewById((R.id.btnSave));
        schMyIngredients2 = addingredient.findViewById(R.id.schMyIngredients2);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i=0; i < IdIng.size();) {
                    db.collection("ListaIngrediente").document(IdIng.get(i)).update(FragmentoProduto.produtoID, Arrays.asList());
                    db.collection("ListaIngrediente").document(IdIng.get(i)).update("idProduto", FieldValue.arrayUnion(FragmentoProduto.produtoID));
                    i++;
                }

                addingredient.dismiss();
            }
        });

        schMyIngredients2.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(s.equals("")){
                    myAdapterTeste = new MyAdapterTeste(getActivity(), list);
                }
                else{
                    filtroPesquisa(s);
                    myAdapterTeste = new MyAdapterTeste(getActivity(),listaPesquisa2);
                }

                RywViewIngredient.setAdapter(myAdapterTeste);
                return true;
            }
        });


        EventChangListerner8();

        addingredient.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addingredient.show();
    }

    public void showPopupEditElectricity() {

        EditText edtEditValueElectricity;
        Button btnEditCalculateEletricity,btnSaveEditEletricity;

        editElectricity.setContentView(R.layout.popup_edit_energiaeletrica);

        edtEditValueElectricity = editElectricity.findViewById(R.id.edtEditValueElectricity);
        btnEditCalculateEletricity = editElectricity.findViewById(R.id.btnEditCalculateEletricity);
        btnSaveEditEletricity = editElectricity.findViewById(R.id.btnSaveEditEletricity);


        if(editvalueElectricity != 0){
            edtEditValueElectricity.setText(PrecoUtils.edicao(editvalueElectricity));
        }


        btnEditCalculateEletricity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editElectricity.dismiss();
                showCalculateElectricityPopup2();
            }
        });

        btnSaveEditEletricity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!edtEditValueElectricity.getText().toString().isEmpty()) {
                    try {
                    editvalueElectricity = PrecoUtils.parse(edtEditValueElectricity.getText().toString());
                    } catch (IllegalArgumentException e) {
                        edtEditValueElectricity.setError("Informe um valor monetário válido");
                        return;
                    }
                    if (editvalueElectricity < 0) {
                        edtEditValueElectricity.setError("O valor não pode ser negativo");
                        return;
                    }
                    Map<String, Object> Electricity = new HashMap<>();
                    Electricity.put("valorCusto",editvalueElectricity);
                    db.collection("OutrosCustos").document(custoID).update(Electricity)
                            .addOnSuccessListener(unused -> PrecificacaoRepository.atualizarOutrosCustos(
                                    db, FirebaseAuth.getInstance().getCurrentUser().getUid(), FragmentoProduto.produtoID)
                                    .addOnFailureListener(e -> Log.e("Receita", "Falha ao atualizar custo total", e)))
                            .addOnFailureListener(e -> Log.e("Receita", "Falha ao editar custo", e));

                }

                editElectricity.dismiss();


            }
        });

        editElectricity.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        editElectricity.show();
    }

    public void showCalculateElectricityPopup2() {
        EditText edtWorkedHours, edtKwhValues, edtPowerWatts;
        Button btnSaveEnergy;
        ImageView imgQuestionMarkElectricity;

        calculateElectricity.setContentView(R.layout.popup_energiaeletrica);

        edtWorkedHours = calculateElectricity.findViewById(R.id.edtWorkedHours);
        edtKwhValues = calculateElectricity.findViewById(R.id.edtKwhValues);
        edtPowerWatts = calculateElectricity.findViewById(R.id.edtPowerWatts);
        imgQuestionMarkElectricity = calculateElectricity.findViewById(R.id.imgQuestionMarkEletricity);
        btnSaveEnergy = calculateElectricity.findViewById(R.id.btnSaveEnergy);
        btnSaveEnergy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    editworkedHours = PrecoUtils.parse(edtWorkedHours.getText().toString());
                    editkwhValues = PrecoUtils.parse(edtKwhValues.getText().toString());
                    double watts = PrecoUtils.parse(edtPowerWatts.getText().toString());
                    editvalueElectricity = PrecoUtils.custoEnergia(watts, editworkedHours, editkwhValues);
                } catch (IllegalArgumentException e) {
                    if (isAdded()) Toast.makeText(requireContext(), "Preencha tempo, potência (W) e tarifa válida", Toast.LENGTH_LONG).show();
                    return;
                }

                calculateElectricity.dismiss();
                showPopupEditElectricity();
            }
        });

        imgQuestionMarkElectricity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder msgE = new AlertDialog.Builder(calculateElectricity.getContext(),R.style.alert_dialog);
                msgE.setTitle("Onde consultar o kWh?");
                msgE.setMessage("Consulte o valor em R$ de 1 kWh em " +
                        "sua tarifa de energia elétrica ou  " +
                        "com a empresa concessionária de " +
                        "energia da sua região.");
                msgE.show();
            }
        });
        calculateElectricity.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        calculateElectricity.show();
    }

    public void showPopupEditGasoline () {
        EditText edtEditValueGasoline;
        Button btnEditCalculateGasoline, btnSaveEditGasoline;

        editGasoline.setContentView(R.layout.popup_edit_combustivel);

        edtEditValueGasoline = editGasoline.findViewById(R.id.edtEditValueGasoline);
        btnEditCalculateGasoline = editGasoline.findViewById(R.id.btnEditCalculateGasoline);
        btnSaveEditGasoline = editGasoline.findViewById(R.id.btnSaveEditGasoline);


        if (editvalueGasoline != 0) {
            edtEditValueGasoline.setText(PrecoUtils.edicao(editvalueGasoline));
        }


        btnEditCalculateGasoline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editGasoline.dismiss();
                showCalculateGasolinePopup2();

            }
        });

        btnSaveEditGasoline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!edtEditValueGasoline.getText().toString().isEmpty()) {
                    try {
                    editvalueGasoline = PrecoUtils.parse(edtEditValueGasoline.getText().toString());
                    } catch (IllegalArgumentException e) {
                        edtEditValueGasoline.setError("Informe um valor monetário válido");
                        return;
                    }
                    if (editvalueGasoline < 0) {
                        edtEditValueGasoline.setError("O valor não pode ser negativo");
                        return;
                    }
                    Map<String, Object> Gasoline = new HashMap<>();
                    Gasoline.put("valorCusto", editvalueGasoline);
                    db.collection("OutrosCustos").document(custoID).update(Gasoline)
                            .addOnSuccessListener(unused -> PrecificacaoRepository.atualizarOutrosCustos(
                                    db, FirebaseAuth.getInstance().getCurrentUser().getUid(), FragmentoProduto.produtoID)
                                    .addOnFailureListener(e -> Log.e("Receita", "Falha ao atualizar custo total", e)))
                            .addOnFailureListener(e -> Log.e("Receita", "Falha ao editar custo", e));

                }
                editGasoline.dismiss();


            }
        });

        editGasoline.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        editGasoline.show();
    }

    public void showCalculateGasolinePopup2() {
        EditText edtKmTraveled, edtKmLiter, edtFuelPrice;
        Button btnSaveFuel;
        ImageView imgQuestionMarkGasoline;

        calculateGasoline.setContentView(R.layout.popup_combustivel);

        edtKmTraveled = calculateGasoline.findViewById(R.id.edtKmTraveled);
        edtKmLiter = calculateGasoline.findViewById(R.id.edtKmLiter);
        edtFuelPrice = calculateGasoline.findViewById(R.id.edtFuelPrice);
        btnSaveFuel = calculateGasoline.findViewById(R.id.btnSaveFuel);
        imgQuestionMarkGasoline = calculateGasoline.findViewById(R.id.imgQuestionMarkGasoline);

        btnSaveFuel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editkmTraveled= PrecoUtils.parse(edtKmTraveled.getText().toString());
                editkmLiter = PrecoUtils.parse(edtKmLiter.getText().toString());
                editfuelPrice = PrecoUtils.parse(edtFuelPrice.getText().toString());

                if (editkmLiter <= 0) {
                    if (isAdded()) Toast.makeText(requireContext(), "Consumo (km/l) deve ser maior que zero", Toast.LENGTH_LONG).show();
                    return;
                }
                editvalueGasoline = (editkmTraveled/editkmLiter)*editfuelPrice;

                calculateGasoline.dismiss();
                showPopupEditGasoline();


            }
        });

        imgQuestionMarkGasoline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder msgG = new AlertDialog.Builder(calculateGasoline.getContext(),R.style.alert_dialog);
                msgG.setTitle("Onde consultar o km/l do veículo?");
                msgG.setMessage("Consulte o computador de bordo do " +
                        "veículo para obter essa informação. " +
                        "Ou, abasteça-o por completo e zere " +
                        "seu hodômetro parcial. Então, no " +
                        "próximo abastecimento, anote o " +
                        "combustível que entrou no veículo, " +
                        "pela bomba do posto. Por fim, basta " +
                        "dividir os quilômetros marcados no  " +
                        "hodômetro pela quantidade de litros que " +
                        "a bomba fornece no segundo abastecimento.");
                msgG.show();
            }
        });
        calculateGasoline.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        calculateGasoline.show();
    }

    public void showPopupEditCookingGas() {
        EditText edtEditValueCookingGas;
        Button btnEditCalculateCookingGas,btnSaveEditCookingGas;

        editCookingGas.setContentView(R.layout.popup_edit_gas);

        edtEditValueCookingGas = editCookingGas.findViewById(R.id.edtEditValueCookingGas);
        btnEditCalculateCookingGas = editCookingGas.findViewById(R.id.btnEditCalculateCookingGas);
        btnSaveEditCookingGas = editCookingGas.findViewById(R.id.btnSaveEditCookingGas);



        if(editvalueCookingGas != 0){
            edtEditValueCookingGas.setText(PrecoUtils.edicao(editvalueCookingGas));
        }

        btnEditCalculateCookingGas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editCookingGas.dismiss();
                showCalculateCookingGasPopup2();

            }
        });

        btnSaveEditCookingGas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!edtEditValueCookingGas.getText().toString().isEmpty()) {
                    try {
                    editvalueCookingGas = PrecoUtils.parse(edtEditValueCookingGas.getText().toString());
                    } catch (IllegalArgumentException e) {
                        edtEditValueCookingGas.setError("Informe um valor monetário válido");
                        return;
                    }
                    if (editvalueCookingGas < 0) {
                        edtEditValueCookingGas.setError("O valor não pode ser negativo");
                        return;
                    }
                    Map<String, Object> CookingGas = new HashMap<>();
                    CookingGas.put("valorCusto",editvalueCookingGas);
                    db.collection("OutrosCustos").document(custoID).update(CookingGas)
                            .addOnSuccessListener(unused -> PrecificacaoRepository.atualizarOutrosCustos(
                                    db, FirebaseAuth.getInstance().getCurrentUser().getUid(), FragmentoProduto.produtoID)
                                    .addOnFailureListener(e -> Log.e("Receita", "Falha ao atualizar custo total", e)))
                            .addOnFailureListener(e -> Log.e("Receita", "Falha ao editar custo", e));
                }
                editCookingGas.dismiss();

            }
        });
        editCookingGas.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        editCookingGas.show();
    }

    public void showCalculateCookingGasPopup2() {
        EditText edtEditUsageTime,edtEditBurnerConsumption,edtEditCylinderCapacity,edtEditGasPrice;
        Button btnEditSaveGas;
        ImageView imgQuestionMarkGas, imgQuestionMarkGas2;

        calculateCookingGas.setContentView(R.layout.popup_gas);

        edtEditUsageTime= calculateCookingGas.findViewById(R.id.edtUsageTime);
        edtEditBurnerConsumption= calculateCookingGas.findViewById(R.id.edtBurnerConsumption);
        edtEditCylinderCapacity= calculateCookingGas.findViewById(R.id.edtCylinderCapacity);
        edtEditGasPrice= calculateCookingGas.findViewById(R.id.edtGasPrice);
        btnEditSaveGas = calculateCookingGas.findViewById(R.id.btnSaveGas);
        imgQuestionMarkGas = calculateCookingGas.findViewById(R.id.imgQuestinMarkGas);
        imgQuestionMarkGas2= calculateCookingGas.findViewById(R.id.imgQuestionMarkGas2);

        btnEditSaveGas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editusageTime = PrecoUtils.parse(edtEditUsageTime.getText().toString());
                editburnerConsumption = PrecoUtils.parse(edtEditBurnerConsumption.getText().toString());
                editcylinderCapacity = PrecoUtils.parse(edtEditCylinderCapacity.getText().toString());
                editgasPrice = PrecoUtils.parse(edtEditGasPrice.getText().toString());

                if (editcylinderCapacity <= 0) {
                    if (isAdded()) Toast.makeText(requireContext(), "Capacidade do botijão deve ser maior que zero", Toast.LENGTH_LONG).show();
                    return;
                }
                editvalueCookingGas = (((editusageTime/60) * editburnerConsumption)/editcylinderCapacity) * editgasPrice;

                calculateCookingGas.dismiss();
                showPopupEditCookingGas();
            }
        });

        imgQuestionMarkGas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder msgG = new AlertDialog.Builder(calculateCookingGas.getContext(),R.style.alert_dialog);
                msgG.setTitle("Onde consultar o consumo do queimador?");
                msgG.setMessage("Consulte o manual do fabricante " +
                        "do fogão. O valor é dado em kg/h e varia "+
                        "de acordo com a temperatura de consumo.");
                msgG.show();
            }
        });

        imgQuestionMarkGas2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder msgG = new AlertDialog.Builder(calculateCookingGas.getContext(),R.style.alert_dialog);
                msgG.setTitle("O que é a capacidade do botijão?");
                msgG.setMessage("Corresponde ao peso do modelo " +
                        "de botijão de GLP. Sendo 13 kg " +
                        "para o modelo P13 e 45 kg para " +
                        "o modelo P45.");
                msgG.show();
            }
        });
        calculateCookingGas.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        calculateCookingGas.show();
    }

    public void showPopupEditOtherCost() {
        Button btnSaveEditOtherCost;
        EditText edtEditOtherValues, edtEditNameOther;

        editOtherCost.setContentView(R.layout.popup_edit_outro_custo);

        btnSaveEditOtherCost = editOtherCost.findViewById(R.id.btnSaveEditOtherCost);
        edtEditOtherValues = editOtherCost.findViewById(R.id.edtEditOtherValues);
        edtEditNameOther = editOtherCost.findViewById(R.id.edtEditNameOther);

        edtEditNameOther.setText(nomeCusto);

        if (editotherValues != 0){
            edtEditOtherValues.setText(PrecoUtils.edicao(editotherValues));
        }

        btnSaveEditOtherCost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!edtEditOtherValues.getText().toString().isEmpty()){
                    try {
                    editotherValues = PrecoUtils.parse(edtEditOtherValues.getText().toString());
                    } catch (IllegalArgumentException e) {
                        edtEditOtherValues.setError("Informe um valor monetário válido");
                        return;
                    }
                    if (editotherValues < 0) {
                        edtEditOtherValues.setError("O valor não pode ser negativo");
                        return;
                    }

                    Map<String, Object> editother = new HashMap<>();
                    editother.put("nomeCusto",edtEditNameOther.getText().toString());
                    editother.put("valorCusto",PrecoUtils.parse(edtEditOtherValues.getText().toString()));
                    db.collection("OutrosCustos").document(custoID).update(editother)
                            .addOnSuccessListener(unused -> PrecificacaoRepository.atualizarOutrosCustos(
                                    db, FirebaseAuth.getInstance().getCurrentUser().getUid(), FragmentoProduto.produtoID)
                                    .addOnFailureListener(e -> Log.e("Receita", "Falha ao atualizar custo total", e)))
                            .addOnFailureListener(e -> Log.e("Receita", "Falha ao editar custo", e));
                }
                editOtherCost.dismiss();

            }
        });
        editOtherCost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        editOtherCost.show();
    }

    public void showPopupQtdIngrediente() {
        EditText edtQuantity;
        TextView txtIngredient;
        Spinner SpinnerTypeQuantity;
        Button btnSaveQtdIng;

        FirebaseUser currentUser =
                FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            return;
        }

        db.collection("Produto")
                .whereEqualTo(
                        "idUsuario",
                        FirebaseAuth.getInstance().getCurrentUser().getUid()
                )
                .whereEqualTo(
                        "idProduto",
                        FragmentoProduto.produtoID
                )
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {

                        if(document.getDouble("totalIngredientes") != null){
                            totalIngredientes = document.getDouble("totalIngredientes");
                        } else{
                            totalIngredientes = 0;
                        }
                    }
                }
            }
        });


        editQuantity.setContentView(R.layout.popup_qtdingrediente);

        edtQuantity = editQuantity.findViewById(R.id.edtQuantity);
        txtIngredient = editQuantity.findViewById(R.id.txtIngredient);

        SpinnerTypeQuantity = editQuantity.findViewById(R.id.SpinnerTypeQuantity);
        btnSaveQtdIng = editQuantity.findViewById(R.id.btnSaveQtdIng);

        txtIngredient.setText(nomeIngrediente);

        btnSaveQtdIng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (valueIngrediente == null || quantityIng == null ||
                        valueIngrediente.trim().isEmpty() || quantityIng.trim().isEmpty()) {
                    Toast.makeText(getActivity(), "Adicione um valor ou quantidade para o Ingrediente "+nomeIngrediente, Toast.LENGTH_SHORT).show();
                }else {
                    try {
                        if (PrecoUtils.parse(quantityIng) <= 0) {
                            Toast.makeText(getActivity(), "Quantidade cadastrada deve ser maior que zero", Toast.LENGTH_LONG).show();
                            return;
                        }
                    } catch (IllegalArgumentException e) {
                        Toast.makeText(getActivity(), "Quantidade ou valor do ingrediente inválido", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (undIngrediente.equals("Und") && SpinnerTypeQuantity.getSelectedItem().toString().equals("Und") && !edtQuantity.getText().toString().isEmpty()) {
                        result = PrecoUtils.parse(edtQuantity.getText().toString()) * (PrecoUtils.parse(valueIngrediente) / PrecoUtils.parse(quantityIng));
                        Map<String, Object> saveing = new HashMap<>();
                        saveing.put("quantidadeProd", edtQuantity.getText().toString());
                        saveing.put("tipoUnid", SpinnerTypeQuantity.getSelectedItem().toString());
                        saveing.put("idProduto", FragmentoProduto.produtoID);
                        saveing.put("valorIngProd", result);
                        db.collection("ListaIngrediente").document(idIngrediente)
                                .update(FragmentoProduto.produtoID, saveing)
                                .addOnSuccessListener(unused -> PrecificacaoRepository.atualizarIngredientes(
                                        db, FirebaseAuth.getInstance().getCurrentUser().getUid(), FragmentoProduto.produtoID)
                                        .addOnFailureListener(e -> Log.e("Receita", "Erro ao somar ingredientes", e)))
                                .addOnFailureListener(e -> Log.e("Receita", "Erro ao salvar quantidade", e));
                        editQuantity.dismiss();
                    } else if (undIngrediente.equals("Kg") && SpinnerTypeQuantity.getSelectedItem().toString().equals("Kg") && !edtQuantity.getText().toString().isEmpty()) {
                        result = PrecoUtils.parse(edtQuantity.getText().toString()) * (PrecoUtils.parse(valueIngrediente) / PrecoUtils.parse(quantityIng));
                        Map<String, Object> saveing = new HashMap<>();
                        saveing.put("quantidadeProd", edtQuantity.getText().toString());
                        saveing.put("tipoUnid", SpinnerTypeQuantity.getSelectedItem().toString());
                        saveing.put("idProduto", FragmentoProduto.produtoID);
                        saveing.put("valorIngProd", result);
                        db.collection("ListaIngrediente").document(idIngrediente)
                                .update(FragmentoProduto.produtoID, saveing)
                                .addOnSuccessListener(unused -> PrecificacaoRepository.atualizarIngredientes(
                                        db, FirebaseAuth.getInstance().getCurrentUser().getUid(), FragmentoProduto.produtoID)
                                        .addOnFailureListener(e -> Log.e("Receita", "Erro ao somar ingredientes", e)))
                                .addOnFailureListener(e -> Log.e("Receita", "Erro ao salvar quantidade", e));
                        editQuantity.dismiss();
                    } else if (undIngrediente.equals("Kg") && SpinnerTypeQuantity.getSelectedItem().toString().equals("g") && !edtQuantity.getText().toString().isEmpty()) {
                        result = PrecoUtils.parse(edtQuantity.getText().toString()) * (PrecoUtils.parse(valueIngrediente) / (PrecoUtils.parse(quantityIng) * 1000));
                        Map<String, Object> saveing = new HashMap<>();
                        saveing.put("quantidadeProd", edtQuantity.getText().toString());
                        saveing.put("tipoUnid", SpinnerTypeQuantity.getSelectedItem().toString());
                        saveing.put("idProduto", FragmentoProduto.produtoID);
                        saveing.put("valorIngProd", result);
                        db.collection("ListaIngrediente").document(idIngrediente)
                                .update(FragmentoProduto.produtoID, saveing)
                                .addOnSuccessListener(unused -> PrecificacaoRepository.atualizarIngredientes(
                                        db, FirebaseAuth.getInstance().getCurrentUser().getUid(), FragmentoProduto.produtoID)
                                        .addOnFailureListener(e -> Log.e("Receita", "Erro ao somar ingredientes", e)))
                                .addOnFailureListener(e -> Log.e("Receita", "Erro ao salvar quantidade", e));
                        editQuantity.dismiss();
                    } else if (undIngrediente.equals("Kg") && SpinnerTypeQuantity.getSelectedItem().toString().equals("mg") && !edtQuantity.getText().toString().isEmpty()) {
                        result = PrecoUtils.parse(edtQuantity.getText().toString()) * (PrecoUtils.parse(valueIngrediente) / (PrecoUtils.parse(quantityIng) * 1000000));
                        Map<String, Object> saveing = new HashMap<>();
                        saveing.put("quantidadeProd", edtQuantity.getText().toString());
                        saveing.put("tipoUnid", SpinnerTypeQuantity.getSelectedItem().toString());
                        saveing.put("idProduto", FragmentoProduto.produtoID);
                        saveing.put("valorIngProd", result);
                        db.collection("ListaIngrediente").document(idIngrediente)
                                .update(FragmentoProduto.produtoID, saveing)
                                .addOnSuccessListener(unused -> PrecificacaoRepository.atualizarIngredientes(
                                        db, FirebaseAuth.getInstance().getCurrentUser().getUid(), FragmentoProduto.produtoID)
                                        .addOnFailureListener(e -> Log.e("Receita", "Erro ao somar ingredientes", e)))
                                .addOnFailureListener(e -> Log.e("Receita", "Erro ao salvar quantidade", e));
                        editQuantity.dismiss();
                    } else if (undIngrediente.equals("g") && SpinnerTypeQuantity.getSelectedItem().toString().equals("mg") && !edtQuantity.getText().toString().isEmpty()) {
                        result = PrecoUtils.parse(edtQuantity.getText().toString()) * (PrecoUtils.parse(valueIngrediente) / (PrecoUtils.parse(quantityIng) * 1000));
                        Map<String, Object> saveing = new HashMap<>();
                        saveing.put("quantidadeProd", edtQuantity.getText().toString());
                        saveing.put("tipoUnid", SpinnerTypeQuantity.getSelectedItem().toString());
                        saveing.put("idProduto", FragmentoProduto.produtoID);
                        saveing.put("valorIngProd", result);
                        db.collection("ListaIngrediente").document(idIngrediente)
                                .update(FragmentoProduto.produtoID, saveing)
                                .addOnSuccessListener(unused -> PrecificacaoRepository.atualizarIngredientes(
                                        db, FirebaseAuth.getInstance().getCurrentUser().getUid(), FragmentoProduto.produtoID)
                                        .addOnFailureListener(e -> Log.e("Receita", "Erro ao somar ingredientes", e)))
                                .addOnFailureListener(e -> Log.e("Receita", "Erro ao salvar quantidade", e));
                        editQuantity.dismiss();
                    } else if (undIngrediente.equals("mg") && SpinnerTypeQuantity.getSelectedItem().toString().equals("g") && !edtQuantity.getText().toString().isEmpty()) {
                        result = PrecoUtils.parse(edtQuantity.getText().toString()) * (PrecoUtils.parse(valueIngrediente) / (PrecoUtils.parse(quantityIng) / 1000));
                        Map<String, Object> saveing = new HashMap<>();
                        saveing.put("quantidadeProd", edtQuantity.getText().toString());
                        saveing.put("tipoUnid", SpinnerTypeQuantity.getSelectedItem().toString());
                        saveing.put("idProduto", FragmentoProduto.produtoID);
                        saveing.put("valorIngProd", result);
                        db.collection("ListaIngrediente").document(idIngrediente)
                                .update(FragmentoProduto.produtoID, saveing)
                                .addOnSuccessListener(unused -> PrecificacaoRepository.atualizarIngredientes(
                                        db, FirebaseAuth.getInstance().getCurrentUser().getUid(), FragmentoProduto.produtoID)
                                        .addOnFailureListener(e -> Log.e("Receita", "Erro ao somar ingredientes", e)))
                                .addOnFailureListener(e -> Log.e("Receita", "Erro ao salvar quantidade", e));
                        editQuantity.dismiss();
                    } else if (undIngrediente.equals("mg") && SpinnerTypeQuantity.getSelectedItem().toString().equals("Kg") && !edtQuantity.getText().toString().isEmpty()) {
                        result = PrecoUtils.parse(edtQuantity.getText().toString()) * (PrecoUtils.parse(valueIngrediente) / (PrecoUtils.parse(quantityIng) / 1000000));
                        Map<String, Object> saveing = new HashMap<>();
                        saveing.put("quantidadeProd", edtQuantity.getText().toString());
                        saveing.put("tipoUnid", SpinnerTypeQuantity.getSelectedItem().toString());
                        saveing.put("idProduto", FragmentoProduto.produtoID);
                        saveing.put("valorIngProd", result);
                        db.collection("ListaIngrediente").document(idIngrediente)
                                .update(FragmentoProduto.produtoID, saveing)
                                .addOnSuccessListener(unused -> PrecificacaoRepository.atualizarIngredientes(
                                        db, FirebaseAuth.getInstance().getCurrentUser().getUid(), FragmentoProduto.produtoID)
                                        .addOnFailureListener(e -> Log.e("Receita", "Erro ao somar ingredientes", e)))
                                .addOnFailureListener(e -> Log.e("Receita", "Erro ao salvar quantidade", e));
                        editQuantity.dismiss();
                    } else if (undIngrediente.equals("g") && SpinnerTypeQuantity.getSelectedItem().toString().equals("Kg") && !edtQuantity.getText().toString().isEmpty()) {
                        result = PrecoUtils.parse(edtQuantity.getText().toString()) * (PrecoUtils.parse(valueIngrediente) / (PrecoUtils.parse(quantityIng) / 1000));
                        Map<String, Object> saveing = new HashMap<>();
                        saveing.put("quantidadeProd", edtQuantity.getText().toString());
                        saveing.put("tipoUnid", SpinnerTypeQuantity.getSelectedItem().toString());
                        saveing.put("idProduto", FragmentoProduto.produtoID);
                        saveing.put("valorIngProd", result);
                        db.collection("ListaIngrediente").document(idIngrediente)
                                .update(FragmentoProduto.produtoID, saveing)
                                .addOnSuccessListener(unused -> PrecificacaoRepository.atualizarIngredientes(
                                        db, FirebaseAuth.getInstance().getCurrentUser().getUid(), FragmentoProduto.produtoID)
                                        .addOnFailureListener(e -> Log.e("Receita", "Erro ao somar ingredientes", e)))
                                .addOnFailureListener(e -> Log.e("Receita", "Erro ao salvar quantidade", e));
                        editQuantity.dismiss();
                    } else if (undIngrediente.equals("L") && SpinnerTypeQuantity.getSelectedItem().toString().equals("mL") && !edtQuantity.getText().toString().isEmpty()) {
                        result = PrecoUtils.parse(edtQuantity.getText().toString()) * (PrecoUtils.parse(valueIngrediente) / (PrecoUtils.parse(quantityIng) * 1000));
                        Map<String, Object> saveing = new HashMap<>();
                        saveing.put("quantidadeProd", edtQuantity.getText().toString());
                        saveing.put("tipoUnid", SpinnerTypeQuantity.getSelectedItem().toString());
                        saveing.put("idProduto", FragmentoProduto.produtoID);
                        saveing.put("valorIngProd", result);
                        db.collection("ListaIngrediente").document(idIngrediente)
                                .update(FragmentoProduto.produtoID, saveing)
                                .addOnSuccessListener(unused -> PrecificacaoRepository.atualizarIngredientes(
                                        db, FirebaseAuth.getInstance().getCurrentUser().getUid(), FragmentoProduto.produtoID)
                                        .addOnFailureListener(e -> Log.e("Receita", "Erro ao somar ingredientes", e)))
                                .addOnFailureListener(e -> Log.e("Receita", "Erro ao salvar quantidade", e));
                        editQuantity.dismiss();
                    } else if (undIngrediente.equals("mL") && SpinnerTypeQuantity.getSelectedItem().toString().equals("L") && !edtQuantity.getText().toString().isEmpty()) {
                        result = PrecoUtils.parse(edtQuantity.getText().toString()) * (PrecoUtils.parse(valueIngrediente) / (PrecoUtils.parse(quantityIng) / 1000));
                        Map<String, Object> saveing = new HashMap<>();
                        saveing.put("quantidadeProd", edtQuantity.getText().toString());
                        saveing.put("tipoUnid", SpinnerTypeQuantity.getSelectedItem().toString());
                        saveing.put("idProduto", FragmentoProduto.produtoID);
                        saveing.put("valorIngProd", result);
                        db.collection("ListaIngrediente").document(idIngrediente)
                                .update(FragmentoProduto.produtoID, saveing)
                                .addOnSuccessListener(unused -> PrecificacaoRepository.atualizarIngredientes(
                                        db, FirebaseAuth.getInstance().getCurrentUser().getUid(), FragmentoProduto.produtoID)
                                        .addOnFailureListener(e -> Log.e("Receita", "Erro ao somar ingredientes", e)))
                                .addOnFailureListener(e -> Log.e("Receita", "Erro ao salvar quantidade", e));
                        editQuantity.dismiss();
                    }else if (undIngrediente.equals("g") && SpinnerTypeQuantity.getSelectedItem().toString().equals("g") && !edtQuantity.getText().toString().isEmpty()) {
                        result = PrecoUtils.parse(edtQuantity.getText().toString()) * (PrecoUtils.parse(valueIngrediente) / (PrecoUtils.parse(quantityIng)));
                        Map<String, Object> saveing = new HashMap<>();
                        saveing.put("quantidadeProd", edtQuantity.getText().toString());
                        saveing.put("tipoUnid", SpinnerTypeQuantity.getSelectedItem().toString());
                        saveing.put("idProduto", FragmentoProduto.produtoID);
                        saveing.put("valorIngProd", result);
                        db.collection("ListaIngrediente").document(idIngrediente)
                                .update(FragmentoProduto.produtoID, saveing)
                                .addOnSuccessListener(unused -> PrecificacaoRepository.atualizarIngredientes(
                                        db, FirebaseAuth.getInstance().getCurrentUser().getUid(), FragmentoProduto.produtoID)
                                        .addOnFailureListener(e -> Log.e("Receita", "Erro ao somar ingredientes", e)))
                                .addOnFailureListener(e -> Log.e("Receita", "Erro ao salvar quantidade", e));
                        editQuantity.dismiss();
                    }else if (undIngrediente.equals("mg") && SpinnerTypeQuantity.getSelectedItem().toString().equals("mg") && !edtQuantity.getText().toString().isEmpty()) {
                        result = PrecoUtils.parse(edtQuantity.getText().toString()) * (PrecoUtils.parse(valueIngrediente) / (PrecoUtils.parse(quantityIng)));
                        Map<String, Object> saveing = new HashMap<>();
                        saveing.put("quantidadeProd", edtQuantity.getText().toString());
                        saveing.put("tipoUnid", SpinnerTypeQuantity.getSelectedItem().toString());
                        saveing.put("idProduto", FragmentoProduto.produtoID);
                        saveing.put("valorIngProd", result);
                        db.collection("ListaIngrediente").document(idIngrediente)
                                .update(FragmentoProduto.produtoID, saveing)
                                .addOnSuccessListener(unused -> PrecificacaoRepository.atualizarIngredientes(
                                        db, FirebaseAuth.getInstance().getCurrentUser().getUid(), FragmentoProduto.produtoID)
                                        .addOnFailureListener(e -> Log.e("Receita", "Erro ao somar ingredientes", e)))
                                .addOnFailureListener(e -> Log.e("Receita", "Erro ao salvar quantidade", e));
                        editQuantity.dismiss();
                    }else if (undIngrediente.equals("L") && SpinnerTypeQuantity.getSelectedItem().toString().equals("L") && !edtQuantity.getText().toString().isEmpty()) {
                        result = PrecoUtils.parse(edtQuantity.getText().toString()) * (PrecoUtils.parse(valueIngrediente) / (PrecoUtils.parse(quantityIng)));
                        Map<String, Object> saveing = new HashMap<>();
                        saveing.put("quantidadeProd", edtQuantity.getText().toString());
                        saveing.put("tipoUnid", SpinnerTypeQuantity.getSelectedItem().toString());
                        saveing.put("idProduto", FragmentoProduto.produtoID);
                        saveing.put("valorIngProd", result);
                        db.collection("ListaIngrediente").document(idIngrediente)
                                .update(FragmentoProduto.produtoID, saveing)
                                .addOnSuccessListener(unused -> PrecificacaoRepository.atualizarIngredientes(
                                        db, FirebaseAuth.getInstance().getCurrentUser().getUid(), FragmentoProduto.produtoID)
                                        .addOnFailureListener(e -> Log.e("Receita", "Erro ao somar ingredientes", e)))
                                .addOnFailureListener(e -> Log.e("Receita", "Erro ao salvar quantidade", e));
                        editQuantity.dismiss();
                    }else if (undIngrediente.equals("mL") && SpinnerTypeQuantity.getSelectedItem().toString().equals("mL") && !edtQuantity.getText().toString().isEmpty()) {
                        result = PrecoUtils.parse(edtQuantity.getText().toString()) * (PrecoUtils.parse(valueIngrediente) / (PrecoUtils.parse(quantityIng)));
                        Map<String, Object> saveing = new HashMap<>();
                        saveing.put("quantidadeProd", edtQuantity.getText().toString());
                        saveing.put("tipoUnid", SpinnerTypeQuantity.getSelectedItem().toString());
                        saveing.put("idProduto", FragmentoProduto.produtoID);
                        saveing.put("valorIngProd", result);
                        db.collection("ListaIngrediente").document(idIngrediente)
                                .update(FragmentoProduto.produtoID, saveing)
                                .addOnSuccessListener(unused -> PrecificacaoRepository.atualizarIngredientes(
                                        db, FirebaseAuth.getInstance().getCurrentUser().getUid(), FragmentoProduto.produtoID)
                                        .addOnFailureListener(e -> Log.e("Receita", "Erro ao somar ingredientes", e)))
                                .addOnFailureListener(e -> Log.e("Receita", "Erro ao salvar quantidade", e));
                        editQuantity.dismiss();
                    }else {
                        Toast.makeText(getActivity(), "Não tem como converter a medida ou quantidade vazia", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        editQuantity.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        editQuantity.show();
    }


    private void EventChangListerner3() {
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        if (usuario == null) return;
        final String uid = usuario.getUid();
        final String pid = FragmentoProduto.produtoID;
        ingredientesListener = db.collection("ListaIngrediente")
                .whereEqualTo("idUsuario", uid)
                .whereArrayContains("idProduto", pid)
                .addSnapshotListener((value, error) -> {
                    if (error != null) { Log.e("Receita", "Erro ao ouvir ingredientes", error); return; }
                    if (value == null || listProdIng == null || myAdapterProdutoIng == null) return;
                    listProdIng.clear();
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        listProdIng.add(doc.toObject(ProdutoIng.class));
                    }
                    myAdapterProdutoIng.notifyDataSetChanged();
                    if (!value.getMetadata().isFromCache() && !value.getMetadata().hasPendingWrites()) {
                        PrecificacaoRepository.atualizarIngredientes(db, uid, pid)
                                .addOnFailureListener(e -> Log.e("Receita", "Falha ao somar ingredientes", e));
                    }
                });
    }

    private void EventChangListerner8() {

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
                            myAdapterTeste.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void EventChangListerner2() {
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        if (usuario == null) return;
        final String uid = usuario.getUid();
        final String pid = FragmentoProduto.produtoID;
        custosListener = db.collection("OutrosCustos")
                .whereEqualTo("idUsuario", uid)
                .whereEqualTo("idProduto", pid)
                .addSnapshotListener((value, error) -> {
                    if (error != null) { Log.e("Receita", "Erro ao ouvir custos", error); return; }
                    if (value == null || listOtherCost == null || myAdapterOtherCost == null) return;
                    listOtherCost.clear();
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        listOtherCost.add(doc.toObject(OutrosCustos.class));
                    }
                    myAdapterOtherCost.notifyDataSetChanged();
                    if (!value.getMetadata().isFromCache() && !value.getMetadata().hasPendingWrites()) {
                        PrecificacaoRepository.atualizarOutrosCustos(db, uid, pid)
                                .addOnFailureListener(e -> Log.e("Receita", "Falha ao somar custos", e));
                    }
                });
    }

    @Override
    public void onDestroyView() {
        if (custosListener != null) { custosListener.remove(); custosListener = null; }
        if (ingredientesListener != null) { ingredientesListener.remove(); ingredientesListener = null; }
        super.onDestroyView();
    }

    @Override
    public void onItemClicked(Ingrediente ingrediente) {

        if (IdIng.size() == 0) {
            IdIng.add(ingrediente.getIdIngrediente());

        }else {
            for (int i=0; i != IdIng.size();) {
                if (ingrediente.getIdIngrediente().equals(IdIng.get(i))) {
                    IdIng.remove(i);
                    count = 1;

                }else {
                    i++;
                }
            }
            if (count == 0) {
                IdIng.add(ingrediente.getIdIngrediente());
        }else {
            count = 0;
            }
        }

   }

    @Override
    public void onItemClicked(Produto produto) {
    }

    @Override
    public void onItemClicked(OutrosCustos outrosCustos) {
        nomeCusto = outrosCustos.getNomeCusto();
        custoID = outrosCustos.getIdCusto();
        System.out.println("nomecusto "+ nomeCusto);
        db.collection("OutrosCustos").document(custoID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists() || documentSnapshot.getDouble("valorCusto") == null) return;
                    double valor = documentSnapshot.getDouble("valorCusto");
                    if ("Eletricidade".equals(nomeCusto)) {
                        editvalueElectricity = valor;
                        showPopupEditElectricity();
                    } else if ("Gasolina".equals(nomeCusto)) {
                        editvalueGasoline = valor;
                        showPopupEditGasoline();
                    } else if ("Gás".equals(nomeCusto)) {
                        editvalueCookingGas = valor;
                        showPopupEditCookingGas();
                    } else {
                        editotherValues = valor;
                        showPopupEditOtherCost();
                    }
                })
                .addOnFailureListener(e -> Log.e("Receita", "Erro ao carregar custo", e));
    }

    @Override
    public void onItemClicked(ProdutoIng produtoIng) {
        nomeIngrediente = produtoIng.getNomeIngrediente();
        undIngrediente = produtoIng.getUnidade();
        idIngrediente = produtoIng.getIdIngrediente();
        valueIngrediente = produtoIng.getPrecoIngrediente();
        quantityIng = produtoIng.getQtdIngrediente();

        if (valueIngrediente == null || quantityIng == null) {
            Log.e("tag","Quantidade ou valor do ingrediente não cadastrado.");
        }else {
            showPopupQtdIngrediente();
        }
    }
    public void filtroPesquisa(String s) {

        listaPesquisa2.clear();
        for (Ingrediente ingrediente : list) {
            if (ingrediente.getNomeIngrediente().toLowerCase().contains(s.toLowerCase())) {
                listaPesquisa2.add(ingrediente);
            }
        }
    }
}
