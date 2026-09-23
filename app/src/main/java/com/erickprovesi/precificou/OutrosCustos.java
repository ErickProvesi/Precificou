package com.erickprovesi.precificou;

public class OutrosCustos {

    double valorCusto;
    String nomeCusto, idCusto, idUsuario, idProduto;

    public OutrosCustos(){
    }

    public OutrosCustos(double valorCusto, String nomeCusto, String idCusto) {
        this.valorCusto = valorCusto;
        this.nomeCusto = nomeCusto;
        this.idCusto = idCusto;
    }

    public String getIdUsuario() { return idUsuario; }
    public void setIdUsuario(String idUsuario) { this.idUsuario = idUsuario; }
    public String getIdProduto() { return idProduto; }
    public void setIdProduto(String idProduto) { this.idProduto = idProduto; }

    public double getValorCusto() {
        return valorCusto;
    }

    public void setValorCusto(double valorCusto) {
        this.valorCusto = valorCusto;
    }

    public String getNomeCusto() {
        return nomeCusto;
    }

    public void setNomeCusto(String nomeCusto) {
        this.nomeCusto = nomeCusto;
    }

    public String getIdCusto() {
        return idCusto;
    }

    public void setIdCusto(String idCusto) {
        this.idCusto = idCusto;
    }
}
