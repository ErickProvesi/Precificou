package com.example.projeto2;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.activity.OnBackPressedCallback;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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

        db.collection("Produto").document(FragmentoProduto.produtoID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.getDouble("totalIngredientes") == null) {

                } else {
                    total = ((FragmentoDetalhes.totalProduto * (FragmentoDetalhes.margemLucro / 100)) + FragmentoDetalhes.totalProduto);
                    if (SpinnerUnd.getSelectedItem().toString().equals("Total")) {
                        txtUnitOrTotalResult.setText(String.valueOf(total));
                        System.out.println("aquiiii " + total);
                    } else {
                        total = ((FragmentoDetalhes.totalProduto * (FragmentoDetalhes.margemLucro / 100)) + FragmentoDetalhes.totalProduto) / FragmentoDetalhes.rendimento;
                        txtUnitOrTotalResult.setText(String.valueOf(total));
                        System.out.println("aquiiii 2 " + total);
                    }
                }
            }
        });

        SpinnerUnd.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (SpinnerUnd.getSelectedItem().toString().equals("Total")) {
                    total = ((FragmentoDetalhes.totalProduto * (FragmentoDetalhes.margemLucro / 100)) + FragmentoDetalhes.totalProduto);
                    txtUnitOrTotalResult.setText(String.valueOf(total));
                    System.out.println("aquiiii " + total);

                } else {
                    total = ((FragmentoDetalhes.totalProduto * (FragmentoDetalhes.margemLucro / 100)) + FragmentoDetalhes.totalProduto) / FragmentoDetalhes.rendimento;
                    txtUnitOrTotalResult.setText(String.valueOf(total));
                    System.out.println("aquiiii 2 " + total);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
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


        imgEditImageProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(intent, 1000);
            }
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

        @Override
        public void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data){
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 1000) {
                if (resultCode == Activity.RESULT_OK) {
                    imgUri = data.getData();
                    try {
                        Bitmap original = MediaStore.Images.Media.getBitmap(Perfil_Produto.this.getContentResolver(), imgUri);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        original.compress(Bitmap.CompressFormat.JPEG, 15, stream);
                        imgProductPhoto.setBackground(null);
                        imgProductPhoto.setImageBitmap(original);
                        imageByte = stream.toByteArray();
                        uploadImageToFirebase(imageByte);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
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