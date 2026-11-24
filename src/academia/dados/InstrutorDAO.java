package academia.dados;

import academia.modelo.Instrutor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class InstrutorDAO {

    private final Path arquivo = Paths.get("dados", "instrutores.txt");

    public InstrutorDAO() {
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
                "Erro ao preparar o arquivo de instrutores.", e);
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

    public List<Instrutor> listarTodos() {
        List<Instrutor> instrutores = new ArrayList<>();

        try {
            List<String> linhas = Files.readAllLines(
                arquivo, StandardCharsets.UTF_8);

            for (String linha : linhas) {
                if (!linha.trim().isEmpty()) {
                    instrutores.add(converterInstrutor(linha));
                }
            }

        } catch (IOException e) {
            throw new IllegalStateException(
                "Erro ao ler os instrutores.", e);
        }

        return instrutores;
    }

    private Instrutor converterInstrutor(String linha) {
        try {
            String[] dados = linha.split("\\|", -1);

            if (dados.length != 8) {
                throw new IllegalArgumentException("Registro inválido.");
            }

            if (!dados[7].equals("INSTRUTOR")) {
                throw new IllegalArgumentException("Tipo inválido.");
            }

            return new Instrutor(
                Integer.parseInt(dados[0]),
                decodificar(dados[1]),
                decodificar(dados[2]),
                decodificar(dados[3]),
                decodificar(dados[4]),
                decodificar(dados[5]),
                decodificar(dados[6])
            );

        } catch (RuntimeException e) {
            throw new IllegalStateException(
                "Erro ao interpretar um registro de instrutor: " + linha, e);
        }
    }

    public Instrutor buscarPorId(int id) {
        for (Instrutor instrutor : listarTodos()) {
            if (instrutor.getId() == id) {
                return instrutor;
            }
        }

        return null;
    }

    public Instrutor buscarPorCpf(String cpf) {
        for (Instrutor instrutor : listarTodos()) {
            if (instrutor.getCpf().equals(cpf)) {
                return instrutor;
            }
        }

        return null;
    }

    public int proximoId() {
        int maiorId = 0;

        for (Instrutor instrutor : listarTodos()) {
            if (instrutor.getId() > maiorId) {
                maiorId = instrutor.getId();
            }
        }

        return maiorId + 1;
    }

    public void inserir(Instrutor instrutor) {
        List<Instrutor> instrutores = listarTodos();
        instrutores.add(instrutor);
        salvarTodos(instrutores);
    }

    public void atualizar(Instrutor instrutorAtualizado) {
        List<Instrutor> instrutores = listarTodos();
        boolean encontrado = false;

        for (int i = 0; i < instrutores.size(); i++) {
            if (instrutores.get(i).getId() == instrutorAtualizado.getId()) {
                instrutores.set(i, instrutorAtualizado);
                encontrado = true;
                break;
            }
        }

        if (encontrado) {
            salvarTodos(instrutores);
        }
    }

    public void excluir(int id) {
        List<Instrutor> instrutores = listarTodos();

        instrutores.removeIf(instrutor -> instrutor.getId() == id);

        salvarTodos(instrutores);
    }

    private void salvarTodos(List<Instrutor> instrutores) {
        List<String> linhas = new ArrayList<>();

        for (Instrutor instrutor : instrutores) {
            String linha =
                instrutor.getId() + "|" +
                codificar(instrutor.getNome()) + "|" +
                codificar(instrutor.getCpf()) + "|" +
                codificar(instrutor.getTelefone()) + "|" +
                codificar(instrutor.getEndereco()) + "|" +
                codificar(instrutor.getEspecialidade()) + "|" +
                codificar(instrutor.getCref()) + "|" +
                "INSTRUTOR";

            linhas.add(linha);
        }

        try {
            ArquivoTexto.salvarAtomico(arquivo, linhas);

        } catch (IOException e) {
            throw new IllegalStateException(
                "Erro ao salvar os instrutores.", e);
        }
    }
}
