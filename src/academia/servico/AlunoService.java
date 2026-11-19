
package academia.servico;

import academia.dados.AlunoDAO;
import academia.excecoes.AlunoNaoEncontradoException;
import academia.modelo.Aluno;

import java.time.LocalDate;
import java.util.List;

public class AlunoService {

    private final AlunoDAO alunoDAO;

    public AlunoService() {
        this.alunoDAO = new AlunoDAO();
    }

    public Aluno cadastrar(String nome, String cpf,
                           String telefone, String endereco,
                           LocalDate dataNascimento) {

        cpf = normalizarCpf(cpf);

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
        cpf = normalizarCpf(cpf);

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

        aluno.setCpf(normalizarCpf(aluno.getCpf()));

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
        alunoDAO.excluir(id);
    }

    public void alterarStatus(int id, boolean ativo) {
        Aluno aluno = buscarPorId(id);

        aluno.setAtivo(ativo);

        alunoDAO.atualizar(aluno);
    }

    private String normalizarCpf(String cpf) {
        if (cpf == null) {
            return "";
        }

        return cpf.replaceAll("[^0-9]", "");
    }

    private boolean cpfValido(String cpf) {

        if (cpf == null || !cpf.matches("\\d{11}")) {
            return false;
        }

        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        int soma = 0;

        for (int i = 0; i < 9; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
        }

        int primeiroDigito = (soma * 10) % 11;

        if (primeiroDigito == 10) {
            primeiroDigito = 0;
        }

        if (primeiroDigito != Character.getNumericValue(cpf.charAt(9))) {
            return false;
        }

        soma = 0;

        for (int i = 0; i < 10; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
        }

        int segundoDigito = (soma * 10) % 11;

        if (segundoDigito == 10) {
            segundoDigito = 0;
        }

        return segundoDigito == Character.getNumericValue(cpf.charAt(10));
    }

    private void validarDados(String nome, String cpf,
                              String telefone, String endereco,
                              LocalDate dataNascimento) {

        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "O nome do aluno é obrigatório."
            );
        }

        if (!cpfValido(cpf)) {
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
