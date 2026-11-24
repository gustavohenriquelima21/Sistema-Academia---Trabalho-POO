package academia.servico;

import academia.excecoes.TreinoNaoEncontradoException;
import academia.modelo.Aluno;
import academia.modelo.Instrutor;
import academia.modelo.Treino;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class TreinoService {

    private static final Path ARQUIVO = Paths.get("dados", "treinos.dat");

    private List<Treino> treinos;
    private int proximoId;

    public TreinoService() {
        carregar();
    }

    public Treino criarTreino(String nome, String objetivo, Instrutor instrutor, Aluno aluno) {
        validarTexto(nome, "O nome do treino é obrigatório.");
        if (instrutor == null) {
            throw new IllegalArgumentException("Selecione um instrutor para o treino.");
        }
        if (aluno == null) {
            throw new IllegalArgumentException("Selecione um aluno para o treino.");
        }
        Treino treino = new Treino(proximoId++, nome.trim(), objetivo, instrutor, aluno);
        treinos.add(treino);
        salvar();
        return treino;
    }

    public void atualizarTreino(int id, String nome, String objetivo, Instrutor instrutor)
            throws TreinoNaoEncontradoException {
        validarTexto(nome, "O nome do treino é obrigatório.");
        if (instrutor == null) {
            throw new IllegalArgumentException("Selecione um instrutor para o treino.");
        }
        Treino treino = buscarPorId(id);
        treino.setNome(nome.trim());
        treino.setObjetivo(objetivo);
        treino.setInstrutor(instrutor);
        salvar();
    }

    public void adicionarMusculacao(int idTreino, String nome, String observacao, String grupoMuscular,
                                    int series, int repeticoes, double carga, int descansoSegundos)
            throws TreinoNaoEncontradoException {
        validarTexto(nome, "O nome do exercício é obrigatório.");
        if (series <= 0 || repeticoes <= 0) {
            throw new IllegalArgumentException("Séries e repetições devem ser maiores que zero.");
        }
        if (carga < 0 || descansoSegundos < 0) {
            throw new IllegalArgumentException("Carga e descanso não podem ser negativos.");
        }
        buscarPorId(idTreino).adicionarMusculacao(nome.trim(), observacao, grupoMuscular,
                series, repeticoes, carga, descansoSegundos);
        salvar();
    }

    public void adicionarCardio(int idTreino, String nome, String observacao, int tempoMinutos, String intensidade)
            throws TreinoNaoEncontradoException {
        validarTexto(nome, "O nome do exercício é obrigatório.");
        if (tempoMinutos <= 0) {
            throw new IllegalArgumentException("O tempo do exercício deve ser maior que zero.");
        }
        buscarPorId(idTreino).adicionarCardio(nome.trim(), observacao, tempoMinutos, intensidade);
        salvar();
    }

    public void removerExercicio(int idTreino, int indice) throws TreinoNaoEncontradoException {
        buscarPorId(idTreino).removerExercicio(indice);
        salvar();
    }

    public void excluirTreino(int id) throws TreinoNaoEncontradoException {
        treinos.remove(buscarPorId(id));
        salvar();
    }

    public Treino buscarPorId(int id) throws TreinoNaoEncontradoException {
        for (Treino treino : treinos) {
            if (treino.getId() == id) {
                return treino;
            }
        }
        throw new TreinoNaoEncontradoException(id);
    }

    public List<Treino> listarTodos() {
        return Collections.unmodifiableList(treinos);
    }

    public List<Treino> listarPorAluno(int idAluno) {
        List<Treino> resultado = new ArrayList<>();
        for (Treino treino : treinos) {
            if (treino.getAluno().getId() == idAluno) {
                resultado.add(treino);
            }
        }
        return resultado;
    }

    public List<Treino> listarPorInstrutor(int idInstrutor) {
        List<Treino> resultado = new ArrayList<>();
        for (Treino treino : treinos) {
            if (treino.getInstrutor().getId() == idInstrutor) {
                resultado.add(treino);
            }
        }
        return resultado;
    }

    private void validarTexto(String texto, String mensagem) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException(mensagem);
        }
    }

    private void salvar() {
        Path temporario = null;
        try {
            Files.createDirectories(ARQUIVO.toAbsolutePath().getParent());
            temporario = Files.createTempFile(ARQUIVO.toAbsolutePath().getParent(), "treinos", ".tmp");
            try (ObjectOutputStream saida = new ObjectOutputStream(Files.newOutputStream(temporario))) {
                saida.writeObject(new ArrayList<>(treinos));
            }
            try { Files.move(temporario, ARQUIVO, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING); }
            catch (java.nio.file.AtomicMoveNotSupportedException e) { Files.move(temporario, ARQUIVO, StandardCopyOption.REPLACE_EXISTING); }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar os treinos: " + e.getMessage(), e);
        } finally {
            if (temporario != null) try { Files.deleteIfExists(temporario); } catch (IOException ignored) { }
        }
    }

    @SuppressWarnings("unchecked")
    private void carregar() {
        treinos = new ArrayList<>();
        proximoId = 1;
        if (!Files.exists(ARQUIVO)) {
            return;
        }
        try (ObjectInputStream entrada = new ObjectInputStream(Files.newInputStream(ARQUIVO))) {
            treinos = (List<Treino>) entrada.readObject();
            for (Treino treino : treinos) {
                proximoId = Math.max(proximoId, treino.getId() + 1);
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Erro ao carregar os treinos: " + e.getMessage(), e);
        }
    }
}
