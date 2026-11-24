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
import java.util.Objects;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TreinoService {

    private static final Path ARQUIVO = Paths.get("dados", "treinos.dat");

    private List<Treino> treinos;
    private int proximoId;
    private String avisoCarga;

    public TreinoService() {
        carregar();
    }

    public Treino criarTreino(String nome, String objetivo, Instrutor instrutor, Aluno aluno) {
        validarTexto(nome, "O nome do treino é obrigatório.");
        validarTexto(objetivo, "O objetivo do treino é obrigatório.");
        if (instrutor == null) {
            throw new IllegalArgumentException("Selecione um instrutor para o treino.");
        }
        if (aluno == null) {
            throw new IllegalArgumentException("Selecione um aluno para o treino.");
        }
        if (!aluno.isAtivo()) {
            throw new IllegalArgumentException("Não é possível criar treino para um aluno inativo.");
        }
        Treino treino = new Treino(proximoId++, nome.trim(), objetivo.trim(), instrutor, aluno);
        treinos.add(treino);
        salvar();
        return treino;
    }

    public void atualizarTreino(int id, String nome, String objetivo, Instrutor instrutor, Aluno aluno)
            throws TreinoNaoEncontradoException {
        validarTexto(nome, "O nome do treino é obrigatório.");
        validarTexto(objetivo, "O objetivo do treino é obrigatório.");
        if (instrutor == null) {
            throw new IllegalArgumentException("Selecione um instrutor para o treino.");
        }
        if (aluno == null) {
            throw new IllegalArgumentException("Selecione um aluno para o treino.");
        }
        Treino treino = buscarPorId(id);
        if (!aluno.isAtivo() && treino.getAluno().getId() != aluno.getId()) {
            throw new IllegalArgumentException("Não é possível associar um aluno inativo ao treino.");
        }
        treino.setNome(nome.trim());
        treino.setObjetivo(objetivo.trim());
        treino.setInstrutor(instrutor);
        treino.setAluno(aluno);
        salvar();
    }

    public void adicionarMusculacao(int idTreino, String nome, String observacao, String grupoMuscular,
                                    int series, int repeticoes, double carga, int descansoSegundos)
            throws TreinoNaoEncontradoException {
        validarTexto(nome, "O nome do exercício é obrigatório.");
        validarTexto(grupoMuscular, "Selecione o grupo muscular.");
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
        validarTexto(intensidade, "Selecione a intensidade do cardio.");
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

    public boolean possuiTreinoParaAluno(int idAluno) {
        return treinos.stream().anyMatch(t -> t.getAluno().getId() == idAluno);
    }

    public boolean possuiTreinoParaInstrutor(int idInstrutor) {
        return treinos.stream().anyMatch(t -> t.getInstrutor().getId() == idInstrutor);
    }

    /** Atualiza os objetos associados aos treinos sem alterar seus identificadores. */
    public void sincronizarPessoas(List<Aluno> alunos, List<Instrutor> instrutores) {
        boolean alterado = false;
        for (Treino treino : treinos) {
            for (Aluno aluno : alunos) {
                if (treino.getAluno().getId() == aluno.getId() && !mesmoAluno(treino.getAluno(), aluno)) {
                    treino.setAluno(aluno);
                    alterado = true;
                    break;
                }
            }
            for (Instrutor instrutor : instrutores) {
                if (treino.getInstrutor().getId() == instrutor.getId()
                        && !mesmoInstrutor(treino.getInstrutor(), instrutor)) {
                    treino.setInstrutor(instrutor);
                    alterado = true;
                    break;
                }
            }
        }
        if (alterado) {
            salvar();
        }
    }

    public String getAvisoCarga() {
        return avisoCarga;
    }

    private boolean mesmoAluno(Aluno a, Aluno b) {
        return a.getId() == b.getId()
                && Objects.equals(a.getNome(), b.getNome())
                && Objects.equals(a.getCpf(), b.getCpf())
                && Objects.equals(a.getTelefone(), b.getTelefone())
                && Objects.equals(a.getEndereco(), b.getEndereco())
                && Objects.equals(a.getDataNascimento(), b.getDataNascimento())
                && a.isAtivo() == b.isAtivo();
    }

    private boolean mesmoInstrutor(Instrutor a, Instrutor b) {
        return a.getId() == b.getId()
                && Objects.equals(a.getNome(), b.getNome())
                && Objects.equals(a.getCpf(), b.getCpf())
                && Objects.equals(a.getTelefone(), b.getTelefone())
                && Objects.equals(a.getEndereco(), b.getEndereco())
                && Objects.equals(a.getEspecialidade(), b.getEspecialidade())
                && Objects.equals(a.getCref(), b.getCref());
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
        } catch (IOException | ClassNotFoundException | RuntimeException e) {
            Path backup = ARQUIVO.resolveSibling("treinos.dat.corrompido-"
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")));
            try {
                Files.move(ARQUIVO, backup, StandardCopyOption.REPLACE_EXISTING);
                treinos = new ArrayList<>();
                proximoId = 1;
                avisoCarga = "O arquivo de treinos estava inválido e foi preservado em '"
                        + backup.getFileName() + "'. O sistema iniciou sem carregar esses treinos.";
            } catch (IOException falhaBackup) {
                throw new RuntimeException("Não foi possível carregar nem preservar o arquivo de treinos. "
                        + "Arquivo: " + ARQUIVO.toAbsolutePath(), falhaBackup);
            }
        }
    }
}
