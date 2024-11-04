package model;

public class Calculadora {

    private String combustivel;
    private double fatorEmissao;

    public Calculadora(String combustivel){
         this.combustivel = combustivel;

    }

    public String getCombustivel() {
        return combustivel;
    }

    public void setCombustivel(String combustivel) {
        this.combustivel = combustivel;
    }

    public double getFatorEmissao() {
        return fatorEmissao;
    }

    public void setFatorEmissao(double fatorEmissao) {
        this.fatorEmissao = fatorEmissao;
    }
}
