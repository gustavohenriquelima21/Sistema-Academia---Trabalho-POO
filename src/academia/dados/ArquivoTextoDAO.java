package academia.dados;

import academia.modelo.Identificavel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * DAO genérico para entidades gravadas em arquivo texto, uma por linha.
 *
 * Concentra tudo o que era igual nos DAOs de aluno, instrutor, plano e
 * matrícula (ler, buscar por ID, gerar próximo ID, inserir, atualizar,
 * excluir e gravar). Cada subclasse implementa apenas o que muda:
 * como transformar uma linha em objeto ({@link #converter}) e um objeto
 * em linha ({@link #formatar}).
 *
 * @param <T> tipo da entidade; precisa ter um ID ({@link Identificavel})
 */
public abstract class ArquivoTextoDAO<T extends Identificavel> {

    private final Path arquivo;
    private final String descricaoPlural;
    private final String descricaoSingular;

    protected ArquivoTextoDAO(String nomeArquivo, String descricaoSingular, String descricaoPlural) {
        this.arquivo = Paths.get("dados", nomeArquivo);
        this.descricaoSingular = descricaoSingular;
        this.descricaoPlural = descricaoPlural;
        prepararArquivo();
    }

    /** Converte uma linha do arquivo em objeto. Pode lançar RuntimeException se a linha for inválida. */
    protected abstract T converter(String linha);

    /** Converte o objeto em uma linha do arquivo. */
    protected abstract String formatar(T entidade);

    private void prepararArquivo() {
        try {
            Files.createDirectories(arquivo.getParent());
            if (!Files.exists(arquivo)) {
                Files.createFile(arquivo);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Erro ao preparar o arquivo de " + descricaoPlural + ".", e);
        }
    }

    public List<T> listarTodos() {
        List<T> entidades = new ArrayList<>();
        try {
            for (String linha : Files.readAllLines(arquivo, StandardCharsets.UTF_8)) {
                if (!linha.trim().isEmpty()) {
                    entidades.add(converterComTratamento(linha));
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Erro ao ler os " + descricaoPlural + ".", e);
        }
        return entidades;
    }

    private T converterComTratamento(String linha) {
        try {
            return converter(linha);
        } catch (RuntimeException e) {
            throw new IllegalStateException(
                    "Erro ao interpretar um registro de " + descricaoSingular + ": " + linha, e);
        }
    }

    /** Retorna a entidade ou {@code null}; a camada de serviço decide qual exceção lançar. */
    public T buscarPorId(int id) {
        for (T entidade : listarTodos()) {
            if (entidade.getId() == id) {
                return entidade;
            }
        }
        return null;
    }

    public int proximoId() {
        int maiorId = 0;
        for (T entidade : listarTodos()) {
            maiorId = Math.max(maiorId, entidade.getId());
        }
        return maiorId + 1;
    }

    public void inserir(T entidade) {
        List<T> entidades = listarTodos();
        entidades.add(entidade);
        salvarTodos(entidades);
    }

    public void atualizar(T entidadeAtualizada) {
        List<T> entidades = listarTodos();
        for (int i = 0; i < entidades.size(); i++) {
            if (entidades.get(i).getId() == entidadeAtualizada.getId()) {
                entidades.set(i, entidadeAtualizada);
                salvarTodos(entidades);
                return;
            }
        }
    }

    public void excluir(int id) {
        List<T> entidades = listarTodos();
        entidades.removeIf(entidade -> entidade.getId() == id);
        salvarTodos(entidades);
    }

    protected void salvarTodos(List<T> entidades) {
        List<String> linhas = new ArrayList<>();
        for (T entidade : entidades) {
            linhas.add(formatar(entidade));
        }
        try {
            ArquivoTexto.salvarAtomico(arquivo, linhas);
        } catch (IOException e) {
            throw new IllegalStateException("Erro ao salvar os " + descricaoPlural + ".", e);
        }
    }

    // Texto livre (nome, endereço...) vai em Base64 para que caracteres como "|" não quebrem o formato.
    protected static String codificar(String texto) {
        return Base64.getEncoder().encodeToString(texto.getBytes(StandardCharsets.UTF_8));
    }

    protected static String decodificar(String texto) {
        return new String(Base64.getDecoder().decode(texto), StandardCharsets.UTF_8);
    }
}
