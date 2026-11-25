
package academia.servico;

import academia.dados.AlunoDAO;
import academia.dados.MatriculaDAO;
import academia.excecoes.AlunoNaoEncontradoException;
import academia.modelo.Aluno;

import java.time.LocalDate;
import java.util.List;

public class AlunoService {

    private final AlunoDAO alunoDAO;
    private final MatriculaDAO matriculaDAO;
    private final TreinoService treinoService;

    public AlunoService() {
        this(null);
    }

    public AlunoService(TreinoService treinoService) {
        this.alunoDAO = new AlunoDAO();
        this.matriculaDAO = new MatriculaDAO();
        this.treinoService = treinoService;
    }

    public Aluno cadastrar(String nome, String cpf,
                           String telefone, String endereco,
                           LocalDate dataNascimento) {

        cpf = ValidadorCpf.normalizar(cpf);

        validarDados(nome, cpf, telefone, endereco, dataNascimento);

        if (alunoDAO.buscarPorCpf(cpf) != null) {
            throw new IllegalArgumentException(
                "Já existe um aluno cadastrado com este CPF."
            );
        }

        Aluno aluno = new Aluno(
            alunoDAO.proximoId(),
            nome,
            cpf,
            telefone,
            endereco,
            dataNascimento
        );

        alunoDAO.inserir(aluno);

        return aluno;
    }

    public List<Aluno> listarTodos() {
        return alunoDAO.listarTodos();
    }

    public Aluno buscarPorId(int id) {
        Aluno aluno = alunoDAO.buscarPorId(id);

        if (aluno == null) {
            throw new AlunoNaoEncontradoException(
                "Aluno com ID " + id + " não encontrado."
            );
        }

        return aluno;
    }

    public Aluno buscarPorCpf(String cpf) {
        cpf = ValidadorCpf.normalizar(cpf);

        Aluno aluno = alunoDAO.buscarPorCpf(cpf);

        if (aluno == null) {
            throw new AlunoNaoEncontradoException(
                "Aluno com CPF " + cpf + " não encontrado."
            );
        }

        return aluno;
    }

    public void atualizar(Aluno aluno) {

        if (aluno == null) {
            throw new IllegalArgumentException(
                "O aluno informado não pode ser nulo."
            );
        }

        aluno.setCpf(ValidadorCpf.normalizar(aluno.getCpf()));

        buscarPorId(aluno.getId());

        validarDados(
            aluno.getNome(),
            aluno.getCpf(),
            aluno.getTelefone(),
            aluno.getEndereco(),
            aluno.getDataNascimento()
        );

        Aluno alunoComMesmoCpf = alunoDAO.buscarPorCpf(aluno.getCpf());

        if (alunoComMesmoCpf != null &&
            alunoComMesmoCpf.getId() != aluno.getId()) {

            throw new IllegalArgumentException(
                "Este CPF já pertence a outro aluno."
            );
        }

        alunoDAO.atualizar(aluno);
    }

    public void excluir(int id) {
        buscarPorId(id);
        if (!matriculaDAO.listarPorAluno(id).isEmpty()) {
            throw new IllegalStateException(
                "Não é possível excluir este aluno porque ele possui matrículas vinculadas."
            );
        }
        if (treinoService != null && treinoService.possuiTreinoParaAluno(id)) {
            throw new IllegalStateException(
                "Não é possível excluir este aluno porque há treinos vinculados."
            );
        }
        alunoDAO.excluir(id);
    }

    /** Ativa ou inativa o aluno. Aluno inativo não pode ser matriculado nem receber treinos. */
    public void alterarStatus(int id, boolean ativo) {
        Aluno aluno = buscarPorId(id);

        if (!ativo && matriculaDAO.buscarAtivaPorAluno(id) != null) {
            throw new IllegalStateException(
                "Este aluno possui matrícula ativa. Cancele a matrícula antes de inativá-lo."
            );
        }

        aluno.setAtivo(ativo);

        alunoDAO.atualizar(aluno);
    }



    private void validarDados(String nome, String cpf,
                              String telefone, String endereco,
                              LocalDate dataNascimento) {

        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "O nome do aluno é obrigatório."
            );
        }

        if (!ValidadorCpf.isValido(cpf)) {
            throw new IllegalArgumentException(
                "O CPF informado é inválido."
            );
        }

        if (telefone == null || telefone.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "O telefone do aluno é obrigatório."
            );
        }

        if (endereco == null || endereco.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "O endereço do aluno é obrigatório."
            );
        }

        if (dataNascimento == null) {
            throw new IllegalArgumentException(
                "A data de nascimento é obrigatória."
            );
        }

        if (dataNascimento.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                "A data de nascimento não pode ser futura."
            );
        }
    }
}
