package com.erickprovesi.precificou;

import java.util.ArrayList;

public class Ingrediente {

    String idIngrediente,idUsuario,nomeIngrediente,precoIngrediente,qtdIngrediente,unidade;

    ArrayList idProdutos;

    public Ingrediente(){}

    public Ingrediente(String idIngrediente, String idUsuario, String nomeIngrediente, String precoIngrediente, String qtdIngrediente, String unidade, ArrayList idProdutos) {
        this.idIngrediente = idIngrediente;
        this.idUsuario = idUsuario;
        this.nomeIngrediente = nomeIngrediente;
        this.precoIngrediente = precoIngrediente;
        this.qtdIngrediente = qtdIngrediente;
        this.unidade = unidade;
        this.idProdutos = idProdutos;
    }



    public String getIdIngrediente() {
        return idIngrediente;
    }

    public void setIdIngrediente(String idIngrediente) {
        this.idIngrediente = idIngrediente;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNomeIngrediente() {
        return nomeIngrediente;
    }

    public void setNomeIngrediente(String nomeIngrediente) {
        this.nomeIngrediente = nomeIngrediente;
    }

    public String getPrecoIngrediente() {
        return precoIngrediente;
    }

    public void setPrecoIngrediente(String precoIngrediente) {
        this.precoIngrediente = precoIngrediente;
    }

    public String getQtdIngrediente() {
        return qtdIngrediente;
    }

    public void setQtdIngrediente(String qtdIngrediente) {
        this.qtdIngrediente = qtdIngrediente;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public ArrayList getIdProdutos() {
        return idProdutos;
    }

    public void setIdProdutos(ArrayList idProdutos) {
        this.idProdutos = idProdutos;
    }
}
