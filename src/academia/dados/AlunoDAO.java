package academia.dados;

import academia.modelo.Aluno;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class AlunoDAO {

    private final Path arquivo = Paths.get("dados", "alunos.txt");

    public AlunoDAO() {
        prepararArquivo();
    }

    private void prepararArquivo() {
        try {
            Files.createDirectories(arquivo.getParent());

            if (!Files.exists(arquivo)) {
                Files.createFile(arquivo);
            }

        } catch (IOException e) {
            throw new IllegalStateException(
                "Erro ao preparar o arquivo de alunos.", e);
        }
    }

    private String codificar(String texto) {
        return Base64.getEncoder().encodeToString(
            texto.getBytes(StandardCharsets.UTF_8));
    }

    private String decodificar(String texto) {
        return new String(
            Base64.getDecoder().decode(texto),
            StandardCharsets.UTF_8);
    }

    public List<Aluno> listarTodos() {
        List<Aluno> alunos = new ArrayList<>();

        try {
            List<String> linhas = Files.readAllLines(
                arquivo, StandardCharsets.UTF_8);

            for (String linha : linhas) {
                if (!linha.trim().isEmpty()) {
                    alunos.add(converterAluno(linha));
                }
            }

        } catch (IOException e) {
            throw new IllegalStateException(
                "Erro ao ler os alunos.", e);
        }

        return alunos;
    }

    private Aluno converterAluno(String linha) {
        try {
            String[] dados = linha.split("\\|", -1);

            if (dados.length != 8) {
                throw new IllegalArgumentException("Registro inválido.");
            }

            Aluno aluno = new Aluno(
                Integer.parseInt(dados[0]),
                decodificar(dados[1]),
                decodificar(dados[2]),
                decodificar(dados[3]),
                decodificar(dados[4]),
                LocalDate.parse(dados[5])
            );

            aluno.setAtivo(Boolean.parseBoolean(dados[6]));

            // Campo reservado para manter o formato preparado
            // para futuras extensões do registro.
            if (!dados[7].equals("ALUNO")) {
                throw new IllegalArgumentException("Tipo inválido.");
            }

            return aluno;

        } catch (RuntimeException e) {
            throw new IllegalStateException(
                "Erro ao interpretar um registro de aluno: " + linha, e);
        }
    }

    public Aluno buscarPorId(int id) {
        for (Aluno aluno : listarTodos()) {
            if (aluno.getId() == id) {
                return aluno;
            }
        }

        return null;
    }

    public Aluno buscarPorCpf(String cpf) {
        for (Aluno aluno : listarTodos()) {
            if (aluno.getCpf().equals(cpf)) {
                return aluno;
            }
        }

        return null;
    }

    public int proximoId() {
        int maiorId = 0;

        for (Aluno aluno : listarTodos()) {
            if (aluno.getId() > maiorId) {
                maiorId = aluno.getId();
            }
        }

        return maiorId + 1;
    }

    public void inserir(Aluno aluno) {
        List<Aluno> alunos = listarTodos();
        alunos.add(aluno);
        salvarTodos(alunos);
    }

    public void atualizar(Aluno alunoAtualizado) {
        List<Aluno> alunos = listarTodos();
        boolean encontrado = false;

        for (int i = 0; i < alunos.size(); i++) {
            if (alunos.get(i).getId() == alunoAtualizado.getId()) {
                alunos.set(i, alunoAtualizado);
                encontrado = true;
                break;
            }
        }

        if (encontrado) {
            salvarTodos(alunos);
        }
    }

    public void excluir(int id) {
        List<Aluno> alunos = listarTodos();

        alunos.removeIf(aluno -> aluno.getId() == id);

        salvarTodos(alunos);
    }

    private void salvarTodos(List<Aluno> alunos) {
        List<String> linhas = new ArrayList<>();

        for (Aluno aluno : alunos) {
            String linha =
                aluno.getId() + "|" +
                codificar(aluno.getNome()) + "|" +
                codificar(aluno.getCpf()) + "|" +
                codificar(aluno.getTelefone()) + "|" +
                codificar(aluno.getEndereco()) + "|" +
                aluno.getDataNascimento() + "|" +
                aluno.isAtivo() + "|" +
                "ALUNO";

            linhas.add(linha);
        }

        try {
            ArquivoTexto.salvarAtomico(arquivo, linhas);

        } catch (IOException e) {
            throw new IllegalStateException(
                "Erro ao salvar os alunos.", e);
        }
    }
}
