package com.example.projeto2;

public class Produto {

    String descricaoProduto, idProduto, idUsuario, nomeProduto, tipoCusto;
    String precoFinal;
    int margemLucro, outroCusto, totalEletricidade, totalGasolina, totalConsumoGas;

    String valorQqr;

    public Produto() {
    }

    public Produto(String descricaoProduto, String idProduto, String idUsuario, String nomeProduto, String precoFinal, int totalEletricidade, int totalGasolina, int totalConsumoGas, int margemLucro, int outroCusto,  String valorQqr, String tipoCusto) {
        this.descricaoProduto = descricaoProduto;
        this.idProduto = idProduto;
        this.idUsuario = idUsuario;
        this.nomeProduto = nomeProduto;
        this.precoFinal = precoFinal;
        this.margemLucro = margemLucro;
        this.outroCusto = outroCusto;
        this.valorQqr = valorQqr;
        this.totalEletricidade = totalEletricidade;
        this.totalGasolina = totalGasolina;
        this.totalConsumoGas = totalConsumoGas;
        this.tipoCusto = tipoCusto;
    }

    public String getValorQqr() {
        return "00.00";
    }

    public void setValorQqr(String valorQqr) {
        this.valorQqr = valorQqr;
    }

    public String getDescricaoProduto() {
        return descricaoProduto;
    }

    public void setDescricaoProduto(String descricaoProduto) {
        this.descricaoProduto = descricaoProduto;
    }

    public String getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(String idProduto) {
        this.idProduto = idProduto;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public String getPrecoFinal() {
        return precoFinal;
    }

    public void setPrecoFinal(String precoFinal) {
        this.precoFinal = precoFinal;
    }

    public int getMargemLucro() {
        return margemLucro;
    }

    public void setMargemLucro(int margemLucro) {
        this.margemLucro = margemLucro;
    }

    public int getOutroCusto() {
        return outroCusto;
    }

    public void setOutroCusto(int outroCusto) {
        this.outroCusto = outroCusto;
    }

    public int getTotalEletricidade() {
        return totalEletricidade;
    }

    public void setTotalEletricidade(int totalEletricidade) {
        this.totalEletricidade = totalEletricidade;
    }

    public int getTotalGasolina() {
        return totalGasolina;
    }

    public void setTotalGasolina(int totalGasolina) {
        this.totalGasolina = totalGasolina;
    }

    public int getTotalConsumoGas() {
        return totalConsumoGas;
    }

    public void setTotalConsumoGas(int totalConsumoGas) {
        this.totalConsumoGas = totalConsumoGas;
    }

    public String getTipoCusto() {
        return tipoCusto;
    }

    public void setTipoCusto(String tipoCusto) {
        this.tipoCusto = tipoCusto;
    }

}