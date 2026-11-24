package academia.modelo;

public class ExercicioMusculacao extends Exercicio {

    private static final long serialVersionUID = 1L;
    private static final int SEGUNDOS_POR_REPETICAO = 3;

    private String grupoMuscular;
    private int series;
    private int repeticoes;
    private double carga;
    private int descansoSegundos;

    public ExercicioMusculacao(String nome, String observacao, String grupoMuscular,
                               int series, int repeticoes, double carga, int descansoSegundos) {
        super(nome, observacao);
        this.grupoMuscular = grupoMuscular;
        this.series = series;
        this.repeticoes = repeticoes;
        this.carga = carga;
        this.descansoSegundos = descansoSegundos;
    }

    public String getGrupoMuscular() {
        return grupoMuscular;
    }

    public void setGrupoMuscular(String grupoMuscular) {
        this.grupoMuscular = grupoMuscular;
    }

    public int getSeries() {
        return series;
    }

    public void setSeries(int series) {
        this.series = series;
    }

    public int getRepeticoes() {
        return repeticoes;
    }

    public void setRepeticoes(int repeticoes) {
        this.repeticoes = repeticoes;
    }

    public double getCarga() {
        return carga;
    }

    public void setCarga(double carga) {
        this.carga = carga;
    }

    public int getDescansoSegundos() {
        return descansoSegundos;
    }

    public void setDescansoSegundos(int descansoSegundos) {
        this.descansoSegundos = descansoSegundos;
    }

    @Override
    public String getTipo() {
        return "Musculação";
    }

    @Override
    public String getDetalhes() {
        return String.format("%s - %dx%d - %.1f kg - descanso %ds",
                grupoMuscular, series, repeticoes, carga, descansoSegundos);
    }

    @Override
    public double calcularDuracaoMinutos() {
        long segundos = (long) series * repeticoes * SEGUNDOS_POR_REPETICAO
                + (long) Math.max(0, series - 1) * descansoSegundos;
        return segundos / 60.0;
    }
}
