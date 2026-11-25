package academia.servico;

import academia.dados.MatriculaDAO;
import academia.dados.PlanoDAO;
import academia.excecoes.PlanoNaoEncontradoException;
import academia.modelo.Plano;

import java.util.List;

/**
 * Regras de negócio de planos. Antes a validação estava repetida em
 * TelaPlanos e TelaMatriculas, e a regra de exclusão ficava no DAO.
 */
public class PlanoService {

    private final PlanoDAO planoDAO;
    private final MatriculaDAO matriculaDAO;

    public PlanoService() {
        this.planoDAO = new PlanoDAO();
        this.matriculaDAO = new MatriculaDAO();
    }

    public Plano cadastrar(String nome, double valorMensal, int duracaoMeses) {
        validarDados(nome, valorMensal, duracaoMeses);
        Plano plano = new Plano(planoDAO.proximoId(), nome.trim(), valorMensal, duracaoMeses);
        planoDAO.inserir(plano);
        return plano;
    }

    public void atualizar(int id, String nome, double valorMensal, int duracaoMeses) {
        validarDados(nome, valorMensal, duracaoMeses);
        Plano plano = buscarPorId(id);
        plano.setNome(nome.trim());
        plano.setValorMensal(valorMensal);
        plano.setDuracaoMeses(duracaoMeses);
        planoDAO.atualizar(plano);
    }

    public List<Plano> listarTodos() {
        return planoDAO.listarTodos();
    }

    public Plano buscarPorId(int id) {
        Plano plano = planoDAO.buscarPorId(id);
        if (plano == null) {
            throw new PlanoNaoEncontradoException(id);
        }
        return plano;
    }

    /** Ativa ou inativa o plano. Plano inativo não aceita novas matrículas. */
    public void alterarStatus(int id, boolean ativo) {
        Plano plano = buscarPorId(id);
        plano.setAtivo(ativo);
        planoDAO.atualizar(plano);
    }

    public void excluir(int id) {
        buscarPorId(id);
        if (!matriculaDAO.listarPorPlano(id).isEmpty()) {
            throw new IllegalStateException(
                "Não é possível excluir este plano porque ele está vinculado a matrículas. "
                + "Use \"Ativar/Inativar\" para impedir novas matrículas.");
        }
        planoDAO.excluir(id);
    }

    private void validarDados(String nome, double valorMensal, int duracaoMeses) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Informe o nome do plano.");
        }
        if (Double.isNaN(valorMensal) || Double.isInfinite(valorMensal) || valorMensal <= 0) {
            throw new IllegalArgumentException("O valor mensal deve ser maior que zero.");
        }
        if (duracaoMeses <= 0 || duracaoMeses > 60) {
            throw new IllegalArgumentException("A duração deve estar entre 1 e 60 meses.");
        }
    }
}
