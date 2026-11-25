package academia.modelo;

public class ExercicioCardio extends Exercicio {

    private static final long serialVersionUID = 1L;

    private int tempoMinutos;
    private String intensidade;

    public ExercicioCardio(String nome, String observacao, int tempoMinutos, String intensidade) {
        super(nome, observacao);
        this.tempoMinutos = tempoMinutos;
        this.intensidade = intensidade;
    }

    public int getTempoMinutos() {
        return tempoMinutos;
    }

    public void setTempoMinutos(int tempoMinutos) {
        this.tempoMinutos = tempoMinutos;
    }

    public String getIntensidade() {
        return intensidade;
    }

    public void setIntensidade(String intensidade) {
        this.intensidade = intensidade;
    }

    @Override
    public String getTipo() {
        return "Cardio";
    }

    @Override
    public String getDetalhes() {
        return String.format("%d min - intensidade %s", tempoMinutos, intensidade);
    }

    @Override
    public double calcularDuracaoMinutos() {
        return tempoMinutos;
    }
}