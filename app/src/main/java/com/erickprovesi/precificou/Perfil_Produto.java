package com.erickprovesi.precificou;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.activity.OnBackPressedCallback;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Perfil_Produto extends AppCompatActivity {


    EditText edtProductName;
    KeyListener listener;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    StorageReference mStorage = FirebaseStorage.getInstance().getReference();
    Uri imgUri;
    byte[] imageByte;
    double total;
    private ListenerRegistration produtoListener;
    private DocumentSnapshot produtoAtual;

    ImageView imgEditProductName, imgConfirmProducName, imgProductPhoto, imgEditImageProduct;
    Spinner SpinnerUnd;
    TextView txtUnitOrTotalResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_produto);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                FragmentoProduto.i = 1;
                setEnabled(false);
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        final long ONE_MEGABYTE = 768 * 768;

        edtProductName = findViewById(R.id.edtProductName);
        imgEditProductName = findViewById(R.id.imgEditProductName);
        imgConfirmProducName = findViewById(R.id.imgConfirmProductName);
        imgProductPhoto = findViewById(R.id.imgProductPhoto);
        imgEditImageProduct = findViewById(R.id.imgEditImageProduct);
        SpinnerUnd = findViewById(R.id.SpinnerUnd);
        txtUnitOrTotalResult = findViewById(R.id.txtUnitOrTotalResult);

        produtoListener = db.collection("Produto")
                .document(FragmentoProduto.produtoID)
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null) {
                        Log.e("Perfil_Produto", "Erro ao ler preço", error);
                        return;
                    }
                    if (snapshot == null || !snapshot.exists()) return;
                    produtoAtual = snapshot;
                    atualizarPrecoExibido();
                });

        SpinnerUnd.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                atualizarPrecoExibido();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        StorageReference productPhotoReference = mStorage.child(FragmentoProduto.userID + "/" + "Produtos/" + FragmentoProduto.produtoID + ".png");
        imgProductPhoto.setBackground(null);
        productPhotoReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imgProductPhoto.setImageBitmap(bmp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });


        imgEditImageProduct.setOnClickListener(view -> {

            pickImageLauncher.launch(
                    new PickVisualMediaRequest.Builder()
                            .setMediaType(
                                    ActivityResultContracts.PickVisualMedia
                                            .ImageOnly.INSTANCE
                            )
                            .build()
            );

        });


        edtProductName.setText(FragmentoProduto.nomeProduto);

        listener = edtProductName.getKeyListener();

        edtProductName.setKeyListener(null);

        imgEditProductName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtProductName.setKeyListener(listener);
                imgEditProductName.setVisibility(View.INVISIBLE);
                imgConfirmProducName.setVisibility(View.VISIBLE);
                edtProductName.requestFocus();

                edtProductName.requestFocus();
                edtProductName.setFocusableInTouchMode(true);

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(edtProductName, InputMethodManager.SHOW_FORCED);


                imgConfirmProducName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (edtProductName.getText().toString().isEmpty()) {
                            imgConfirmProducName.setVisibility(View.INVISIBLE);
                            edtProductName.setError("Não deixe em branco!");

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    edtProductName.setError(null);
                                    imgConfirmProducName.setVisibility(View.VISIBLE);


                                }
                            }, 1000);

                        } else {
                            db.collection("Produto").document(FragmentoProduto.produtoID).update("nomeProduto", edtProductName.getText().toString());
                            imgConfirmProducName.setVisibility(View.INVISIBLE);
                            imgEditProductName.setVisibility(View.VISIBLE);
                            edtProductName.setKeyListener(null);

                            FragmentoProduto.list2.clear();
                            db.collection("Produto").whereEqualTo("idUsuario", FragmentoProduto.userID)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                                            if (error != null) {

                                                Log.e("Firestore error", error.getMessage());
                                                return;
                                            }

                                            for (DocumentChange dc : value.getDocumentChanges()) {
                                                if (dc.getType() == DocumentChange.Type.ADDED) {
                                                    FragmentoProduto.list2.add(dc.getDocument().toObject(Produto.class));
                                                }
                                                FragmentoProduto.myAdapter2.notifyDataSetChanged();
                                            }
                                        }
                                    });
                        }
                    }
                });
            }
        });
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        FragmentoReceita fragReceita = new FragmentoReceita();
        transaction.replace(R.id.fltPerfilProduto, fragReceita);
        transaction.commit();

    }

    private void atualizarPrecoExibido() {
        if (produtoAtual == null || txtUnitOrTotalResult == null) return;
        double ingredientes = PrecoUtils.numero(produtoAtual.getDouble("totalIngredientes"));
        double outros = PrecoUtils.numero(produtoAtual.getDouble("totalOutrosCustos"));
        double margem = PrecoUtils.numero(produtoAtual.getDouble("margemLucro"));
        double rendimento = PrecoUtils.numero(produtoAtual.getDouble("rendimento"));
        double preco = PrecoUtils.precoFinal(ingredientes, outros, margem);
        String escolha = SpinnerUnd.getSelectedItem() == null ? "Total" :
                SpinnerUnd.getSelectedItem().toString();
        if (!"Total".equals(escolha)) {
            if (rendimento <= 0) {
                txtUnitOrTotalResult.setText("Defina o rendimento");
                return;
            }
            preco /= rendimento;
        }
        txtUnitOrTotalResult.setText(PrecoUtils.moeda(preco));
    }

    @Override
    protected void onDestroy() {
        if (produtoListener != null) produtoListener.remove();
        super.onDestroy();
    }

    private final ActivityResultLauncher<PickVisualMediaRequest> pickImageLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.PickVisualMedia(),
                    uri -> {

                        if (uri == null) {
                            return;
                        }

                        imgUri = uri;

                        try {
                            Bitmap original = MediaStore.Images.Media.getBitmap(
                                    getContentResolver(),
                                    imgUri
                            );

                            ByteArrayOutputStream stream =
                                    new ByteArrayOutputStream();

                            original.compress(
                                    Bitmap.CompressFormat.JPEG,
                                    15,
                                    stream
                            );

                            imgProductPhoto.setBackground(null);
                            imgProductPhoto.setImageBitmap(original);

                            imageByte = stream.toByteArray();

                            uploadImageToFirebase(imageByte);

                        } catch (IOException | SecurityException e) {
                            e.printStackTrace();

                            Toast.makeText(
                                    Perfil_Produto.this,
                                    "Não foi possível carregar a imagem",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
            );

        private void uploadImageToFirebase ( byte[] imageByte){
            StorageReference storageReference = mStorage.child(FragmentoProduto.userID + "/Produtos/" + FragmentoProduto.produtoID + ".png");
            storageReference.delete();
            StorageReference fileRef = mStorage.child(FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + "Produtos" + "/" + FragmentoProduto.produtoID + ".png");
            fileRef.putBytes(imageByte).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(Perfil_Produto.this.getApplicationContext(), "Imagem enviada com sucesso", Toast.LENGTH_SHORT);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Perfil_Produto.this.getApplicationContext(), "Erro ao enviar imagem", Toast.LENGTH_SHORT);
                }
            });
        }

}