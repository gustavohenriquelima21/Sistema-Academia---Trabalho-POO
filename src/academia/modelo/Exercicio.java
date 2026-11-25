package academia.modelo;

import java.io.Serializable;

public abstract class Exercicio implements Serializable, DuracaoCalculavel {

    private static final long serialVersionUID = 1L;

    private String nome;
    private String observacao;

    public Exercicio(String nome, String observacao) {
        this.nome = nome;
        this.observacao = observacao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public abstract String getTipo();

    public abstract String getDetalhes();

    public String getDescricao() {
        return nome + " - " + getDetalhes();
    }

    @Override
    public abstract double calcularDuracaoMinutos();

    @Override
    public String toString() {
        return getDescricao();
    }
}
