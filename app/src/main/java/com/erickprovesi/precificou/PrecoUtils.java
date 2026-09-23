package com.erickprovesi.precificou;

import java.text.NumberFormat;
import java.util.Locale;

/** Valores brutos sempre numéricos no Firestore; formatação apenas na interface. */
public final class PrecoUtils {
    private PrecoUtils() {}

    public static String moeda(double valor) {
        if (!Double.isFinite(valor)) return "—";
        return NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(valor);
    }

    public static String moedaSemSimbolo(double valor) {
        if (!Double.isFinite(valor)) return "—";

        NumberFormat formato = NumberFormat.getNumberInstance(
                new Locale("pt", "BR")
        );

        formato.setMinimumFractionDigits(2);
        formato.setMaximumFractionDigits(2);

        return formato.format(valor);
    }

    /** Para EditText: não inclui R$ nem separadores de milhares. */
    public static String edicao(double valor) {
        if (!Double.isFinite(valor)) return "";
        return String.format(Locale.US, "%.2f", valor).replace('.', ',');
    }

    /** Usado com valores legados que podem conter dados inválidos. */
    public static String moedaTexto(String valor) {
        try { return moeda(parse(valor)); }
        catch (IllegalArgumentException e) { return "—"; }
    }

    public static double parse(String valor) {
        String texto = valor.trim().replace(',', '.');
        double numero = Double.parseDouble(texto);
        if (!Double.isFinite(numero)) throw new NumberFormatException("Valor não finito");
        return numero;
    }

    public static double numero(Number n) {
        return n == null || !Double.isFinite(n.doubleValue()) ? 0.0 : n.doubleValue();
    }

    /** W / 1000 * minutos / 60 * R$/kWh. */
    public static double custoEnergia(double watts, double minutos, double precoKwh) {
        if (!Double.isFinite(watts) || watts <= 0 ||
                !Double.isFinite(minutos) || minutos <= 0 ||
                !Double.isFinite(precoKwh) || precoKwh < 0) {
            throw new IllegalArgumentException("Potência e tempo devem ser positivos; tarifa não pode ser negativa");
        }
        return (watts / 1000.0) * (minutos / 60.0) * precoKwh;
    }

    public static double precoFinal(double ingredientes, double outrosCustos, double margemPercentual) {
        double base = ingredientes + outrosCustos;
        double preco = base * (1.0 + margemPercentual / 100.0);
        return Double.isFinite(preco) ? preco : Double.NaN;
    }
}
