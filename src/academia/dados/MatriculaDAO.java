package academia.dados;

import academia.modelo.Matricula;
import academia.modelo.Mensalidade;
import academia.modelo.StatusMatricula;
import academia.modelo.StatusMensalidade;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MatriculaDAO {

    private final Path arquivo = Paths.get("dados", "matriculas.txt");

    public MatriculaDAO() {
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
                "Erro ao preparar o arquivo de matrículas.", e);
        }
    }

    public List<Matricula> listarTodos() {
        List<Matricula> matriculas = new ArrayList<>();

        try {
            List<String> linhas = Files.readAllLines(
                arquivo, StandardCharsets.UTF_8);

            for (String linha : linhas) {
                if (!linha.trim().isEmpty()) {
                    matriculas.add(converterMatricula(linha));
                }
            }

        } catch (IOException e) {
            throw new IllegalStateException(
                "Erro ao ler as matrículas.", e);
        }

        return matriculas;
    }

    private Matricula converterMatricula(String linha) {
        try {
            String[] dados = linha.split("\\|", -1);

            if (dados.length != 7) {
                throw new IllegalArgumentException("Registro inválido.");
            }

            Matricula matricula = new Matricula(
                Integer.parseInt(dados[0]),
                Integer.parseInt(dados[1]),
                Integer.parseInt(dados[2]),
                LocalDate.parse(dados[3]),
                LocalDate.parse(dados[4])
            );

            matricula.setStatus(StatusMatricula.valueOf(dados[5]));
            matricula.setMensalidades(converterMensalidades(dados[6]));

            return matricula;

        } catch (RuntimeException e) {
            throw new IllegalStateException(
                "Erro ao interpretar um registro de matrícula: " + linha, e);
        }
    }

    private List<Mensalidade> converterMensalidades(String texto) {
        List<Mensalidade> mensalidades = new ArrayList<>();

        if (texto.isEmpty()) {
            return mensalidades;
        }

        for (String item : texto.split(";")) {
            String[] campos = item.split(",", -1);

            if (campos.length != 5) {
                throw new IllegalArgumentException("Mensalidade inválida.");
            }

            Mensalidade mensalidade = new Mensalidade(
                Integer.parseInt(campos[0]),
                LocalDate.parse(campos[1]),
                Double.parseDouble(campos[2])
            );

            if (!campos[3].isEmpty()) {
                mensalidade.setDataPagamento(LocalDate.parse(campos[3]));
            }

            mensalidade.setStatus(StatusMensalidade.valueOf(campos[4]));

            mensalidades.add(mensalidade);
        }

        return mensalidades;
    }

    private String serializarMensalidades(List<Mensalidade> mensalidades) {
        List<String> itens = new ArrayList<>();

        for (Mensalidade m : mensalidades) {
            String dataPagamento = m.getDataPagamento() == null
                ? ""
                : m.getDataPagamento().toString();

            itens.add(
                m.getNumeroParcela() + "," +
                m.getDataVencimento() + "," +
                m.getValor() + "," +
                dataPagamento + "," +
                m.getStatus()
            );
        }

        return String.join(";", itens);
    }

    public Matricula buscarPorId(int id) {
        for (Matricula matricula : listarTodos()) {
            if (matricula.getId() == id) {
                return matricula;
            }
        }

        return null;
    }

    public List<Matricula> listarPorAluno(int idAluno) {
        List<Matricula> resultado = new ArrayList<>();

        for (Matricula matricula : listarTodos()) {
            if (matricula.getIdAluno() == idAluno) {
                resultado.add(matricula);
            }
        }

        return resultado;
    }

    public Matricula buscarAtivaPorAluno(int idAluno) {
        for (Matricula matricula : listarTodos()) {
            if (matricula.getIdAluno() == idAluno && matricula.estaAtiva()) {
                return matricula;
            }
        }

        return null;
    }

    public int proximoId() {
        int maiorId = 0;

        for (Matricula matricula : listarTodos()) {
            if (matricula.getId() > maiorId) {
                maiorId = matricula.getId();
            }
        }

        return maiorId + 1;
    }

    public void inserir(Matricula matricula) {
        List<Matricula> matriculas = listarTodos();
        matriculas.add(matricula);
        salvarTodos(matriculas);
    }

    public void atualizar(Matricula matriculaAtualizada) {
        List<Matricula> matriculas = listarTodos();
        boolean encontrado = false;

        for (int i = 0; i < matriculas.size(); i++) {
            if (matriculas.get(i).getId() == matriculaAtualizada.getId()) {
                matriculas.set(i, matriculaAtualizada);
                encontrado = true;
                break;
            }
        }

        if (encontrado) {
            salvarTodos(matriculas);
        }
    }

    public void excluir(int id) {
        List<Matricula> matriculas = listarTodos();

        matriculas.removeIf(matricula -> matricula.getId() == id);

        salvarTodos(matriculas);
    }

    private void salvarTodos(List<Matricula> matriculas) {
        List<String> linhas = new ArrayList<>();

        for (Matricula matricula : matriculas) {
            String linha =
                matricula.getId() + "|" +
                matricula.getIdAluno() + "|" +
                matricula.getIdPlano() + "|" +
                matricula.getDataInicio() + "|" +
                matricula.getDataFim() + "|" +
                matricula.getStatus() + "|" +
                serializarMensalidades(matricula.getMensalidades());

            linhas.add(linha);
        }

        try {
            ArquivoTexto.salvarAtomico(arquivo, linhas);

        } catch (IOException e) {
            throw new IllegalStateException(
                "Erro ao salvar as matrículas.", e);
        }
    }
}
