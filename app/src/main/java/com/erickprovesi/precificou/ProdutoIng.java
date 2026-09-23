package com.erickprovesi.precificou;

import java.util.ArrayList;

public class ProdutoIng {

    String idIngrediente,idUsuario,nomeIngrediente,precoIngrediente,qtdIngrediente,unidade;
    String tipoUnid, quantidadeProd;

    ArrayList idProdutos;

    public ProdutoIng(){}

    public ProdutoIng(String idIngrediente, String idUsuario, String nomeIngrediente, String precoIngrediente, String qtdIngrediente, String unidade, ArrayList idProdutos,String tipoUnid,String quantidadeProd) {
        this.idIngrediente = idIngrediente;
        this.idUsuario = idUsuario;
        this.nomeIngrediente = nomeIngrediente;
        this.precoIngrediente = precoIngrediente;
        this.qtdIngrediente = qtdIngrediente;
        this.unidade = unidade;
        this.idProdutos = idProdutos;
        this.tipoUnid = tipoUnid;
        this.quantidadeProd = quantidadeProd;
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

    public String getTipoUnid() {
        return tipoUnid;
    }

    public void setTipoUnid(String tipoUnid) {
        this.tipoUnid = tipoUnid;
    }

    public String getQuantidadeProd() {
        return quantidadeProd;
    }

    public void setQuantidadeProd(String quantidadeProd) {
        this.quantidadeProd = quantidadeProd;
    }
}

