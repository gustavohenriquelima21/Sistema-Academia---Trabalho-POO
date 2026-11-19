package academia.modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Treino implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String nome;
    private String objetivo;
    private LocalDate dataCriacao;
    private Instrutor instrutor;
    private Aluno aluno;
    private final List<Exercicio> exercicios = new ArrayList<>();

    public Treino(int id, String nome, String objetivo, Instrutor instrutor, Aluno aluno) {
        this.id = id;
        this.nome = nome;
        this.objetivo = objetivo;
        this.instrutor = instrutor;
        this.aluno = aluno;
        this.dataCriacao = LocalDate.now();
    }

    public ExercicioMusculacao adicionarMusculacao(String nome, String observacao, String grupoMuscular,
                                                   int series, int repeticoes, double carga, int descansoSegundos) {
        ExercicioMusculacao exercicio = new ExercicioMusculacao(nome, observacao, grupoMuscular,
                series, repeticoes, carga, descansoSegundos);
        exercicios.add(exercicio);
        return exercicio;
    }

    public ExercicioCardio adicionarCardio(String nome, String observacao, int tempoMinutos, String intensidade) {
        ExercicioCardio exercicio = new ExercicioCardio(nome, observacao, tempoMinutos, intensidade);
        exercicios.add(exercicio);
        return exercicio;
    }

    public void removerExercicio(int indice) {
        if (indice < 0 || indice >= exercicios.size()) {
            throw new IndexOutOfBoundsException("Exercício inexistente na posição " + indice);
        }
        exercicios.remove(indice);
    }

    public List<Exercicio> getExercicios() {
        return Collections.unmodifiableList(exercicios);
    }

    public int getTotalExercicios() {
        return exercicios.size();
    }

    public double calcularDuracaoTotal() {
        double total = 0;
        for (Exercicio exercicio : exercicios) {
            total += exercicio.calcularDuracaoMinutos();
        }
        return total;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getObjetivo() {
        return objetivo;
    }

    public void setObjetivo(String objetivo) {
        this.objetivo = objetivo;
    }

    public LocalDate getDataCriacao() {
        return dataCriacao;
    }

    public Instrutor getInstrutor() {
        return instrutor;
    }

    public void setInstrutor(Instrutor instrutor) {
        this.instrutor = instrutor;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

    @Override
    public String toString() {
        return nome + " - " + aluno.getNome() + " (Instrutor: " + instrutor.getNome() + ")";
    }
}