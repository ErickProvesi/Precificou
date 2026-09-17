package com.example.projeto2;

public class OutrosCustos {

    double valorCusto;
    String nomeCusto, idCusto;

    public OutrosCustos(){
    }

    public OutrosCustos(double valorCusto, String nomeCusto, String idCusto) {
        this.valorCusto = valorCusto;
        this.nomeCusto = nomeCusto;
        this.idCusto = idCusto;
    }

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
