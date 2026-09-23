package com.erickprovesi.precificou;

public interface SelectListener {
    void onItemClicked(Ingrediente ingrediente);
    void onItemClicked(Produto produto);
    void onItemClicked(OutrosCustos outrosCustos);
    void onItemClicked(ProdutoIng produtoIng);
}
