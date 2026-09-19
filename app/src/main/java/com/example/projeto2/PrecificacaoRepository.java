package com.example.projeto2;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

/** Recompõe os totais do produto a partir dos documentos atuais, não de somas locais antigas. */
public final class PrecificacaoRepository {
    private PrecificacaoRepository() {}

    private static boolean usuarioCorreto(String uid) {
        return FirebaseAuth.getInstance().getCurrentUser() != null &&
                uid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    public static Task<Void> atualizarOutrosCustos(FirebaseFirestore db, String uid, String produtoId) {
        if (!usuarioCorreto(uid)) return Tasks.forException(new IllegalStateException("Sessão inválida"));
        return db.collection("OutrosCustos")
                .whereEqualTo("idUsuario", uid)
                .whereEqualTo("idProduto", produtoId)
                .get()
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) return Tasks.forException(task.getException());
                    double total = 0.0;
                    for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                        total += PrecoUtils.numero(doc.getDouble("valorCusto"));
                    }
                    if (!Double.isFinite(total)) return Tasks.forException(new IllegalStateException("Total inválido"));
                    return db.collection("Produto").document(produtoId).update("totalOutrosCustos", total);
                });
    }

    public static Task<Void> atualizarIngredientes(FirebaseFirestore db, String uid, String produtoId) {
        if (!usuarioCorreto(uid)) return Tasks.forException(new IllegalStateException("Sessão inválida"));
        return db.collection("ListaIngrediente")
                .whereEqualTo("idUsuario", uid)
                .whereArrayContains("idProduto", produtoId)
                .get()
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) return Tasks.forException(task.getException());
                    double total = 0.0;
                    for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                        Object porProduto = doc.get(produtoId);
                        if (porProduto instanceof Map) {
                            Object valor = ((Map<?, ?>) porProduto).get("valorIngProd");
                            if (valor instanceof Number) total += PrecoUtils.numero((Number) valor);
                        }
                    }
                    if (!Double.isFinite(total)) return Tasks.forException(new IllegalStateException("Total inválido"));
                    return db.collection("Produto").document(produtoId).update("totalIngredientes", total);
                });
    }
}
