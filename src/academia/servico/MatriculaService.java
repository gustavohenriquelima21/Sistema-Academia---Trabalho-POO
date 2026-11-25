package academia.servico;

import academia.dados.MatriculaDAO;
import academia.excecoes.MatriculaInvalidaException;
import academia.modelo.Aluno;
import academia.modelo.FormaPagamento;
import academia.modelo.Matricula;
import academia.modelo.Mensalidade;
import academia.modelo.Plano;
import academia.modelo.StatusMatricula;

import java.time.LocalDate;
import java.util.List;

public class MatriculaService {

    private final MatriculaDAO matriculaDAO;
    private final PlanoService planoService;
    private final AlunoService alunoService;

    public MatriculaService() {
        this(new AlunoService(), new PlanoService());
    }

    public MatriculaService(AlunoService alunoService, PlanoService planoService) {
        this.matriculaDAO = new MatriculaDAO();
        this.alunoService = alunoService;
        this.planoService = planoService;
    }

    public Matricula matricular(int idAluno, int idPlano,
                                LocalDate dataInicio) {

        if (dataInicio == null) {
            throw new MatriculaInvalidaException(
                "A data de início é obrigatória."
            );
        }

        // Lança AlunoNaoEncontradoException se não existir
        Aluno aluno = alunoService.buscarPorId(idAluno);

        if (!aluno.isAtivo()) {
            throw new MatriculaInvalidaException(
                "Não é possível matricular um aluno inativo."
            );
        }

        // Lança PlanoNaoEncontradoException se não existir
        Plano plano = planoService.buscarPorId(idPlano);

        if (!plano.isAtivo()) {
            throw new MatriculaInvalidaException(
                "Este plano está inativo e não aceita novas matrículas."
            );
        }

        if (matriculaDAO.buscarAtivaPorAluno(idAluno) != null) {
            throw new MatriculaInvalidaException(
                "Este aluno já possui uma matrícula ativa."
            );
        }

        Matricula matricula = new Matricula(
            matriculaDAO.proximoId(),
            idAluno,
            idPlano,
            dataInicio,
            dataInicio.plusMonths(plano.getDuracaoMeses())
        );

        matricula.gerarMensalidades(plano);

        matriculaDAO.inserir(matricula);

        return matricula;
    }

    public List<Matricula> listarTodos() {
        return matriculaDAO.listarTodos();
    }

    public List<Matricula> listarPorAluno(int idAluno) {
        return matriculaDAO.listarPorAluno(idAluno);
    }

    public Matricula buscarPorId(int id) {
        Matricula matricula = matriculaDAO.buscarPorId(id);

        if (matricula == null) {
            throw new MatriculaInvalidaException(
                "Matrícula com ID " + id + " não encontrada."
            );
        }

        return matricula;
    }

    public void cancelar(int id) {
        Matricula matricula = buscarPorId(id);

        try {
            matricula.cancelar();
        } catch (IllegalStateException e) {
            throw new MatriculaInvalidaException(e.getMessage());
        }

        matriculaDAO.atualizar(matricula);
    }

    public Mensalidade pagarMensalidade(int idMatricula, int numeroParcela,
                                        LocalDate dataPagamento, FormaPagamento forma) {

        Matricula matricula = buscarPorId(idMatricula);

        if (!matricula.estaAtiva()) {
            throw new MatriculaInvalidaException(
                "Só é possível pagar mensalidades de matrículas ativas."
            );
        }

        Mensalidade alvo = null;

        for (Mensalidade m : matricula.getMensalidades()) {
            if (m.getNumeroParcela() == numeroParcela) {
                alvo = m;
                break;
            }
        }

        if (alvo == null) {
            throw new MatriculaInvalidaException(
                "Parcela " + numeroParcela + " não encontrada."
            );
        }

        try {
            alvo.pagar(dataPagamento, forma);
        } catch (IllegalStateException | IllegalArgumentException e) {
            throw new MatriculaInvalidaException(e.getMessage());
        }

        matriculaDAO.atualizar(matricula);
        return alvo;
    }

    /**
     * Marca como ENCERRADA as matrículas ativas cuja data de fim já passou.
     * Retorna quantas foram encerradas.
     */
    public int encerrarVencidas() {
        int encerradas = 0;

        for (Matricula matricula : matriculaDAO.listarTodos()) {
            if (matricula.estaAtiva()
                && matricula.getDataFim().isBefore(LocalDate.now())) {

                matricula.setStatus(StatusMatricula.ENCERRADA);
                matriculaDAO.atualizar(matricula);
                encerradas++;
            }
        }

        return encerradas;
    }
}