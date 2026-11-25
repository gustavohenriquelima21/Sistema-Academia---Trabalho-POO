package academia.servico;

import academia.dados.InstrutorDAO;
import academia.excecoes.InstrutorNaoEncontradoException;
import academia.modelo.Instrutor;

import java.util.List;

public class InstrutorService {

    private final InstrutorDAO instrutorDAO;
    private final TreinoService treinoService;

    public InstrutorService() {
        this(null);
    }

    public InstrutorService(TreinoService treinoService) {
        this.instrutorDAO = new InstrutorDAO();
        this.treinoService = treinoService;
    }

    public Instrutor cadastrar(String nome, String cpf,
                               String telefone, String endereco,
                               String especialidade, String cref) {

        cpf = ValidadorCpf.normalizar(cpf);

        validarDados(nome, cpf, telefone, endereco, especialidade, cref);

        if (instrutorDAO.buscarPorCpf(cpf) != null) {
            throw new IllegalArgumentException(
                "Já existe um instrutor cadastrado com este CPF."
            );
        }

        Instrutor instrutor = new Instrutor(
            instrutorDAO.proximoId(),
            nome,
            cpf,
            telefone,
            endereco,
            especialidade,
            cref
        );

        instrutorDAO.inserir(instrutor);

        return instrutor;
    }

    public List<Instrutor> listarTodos() {
        return instrutorDAO.listarTodos();
    }

    public Instrutor buscarPorId(int id) {
        Instrutor instrutor = instrutorDAO.buscarPorId(id);

        if (instrutor == null) {
            throw new InstrutorNaoEncontradoException(
                "Instrutor com ID " + id + " não encontrado."
            );
        }

        return instrutor;
    }

    public Instrutor buscarPorCpf(String cpf) {
        cpf = ValidadorCpf.normalizar(cpf);

        Instrutor instrutor = instrutorDAO.buscarPorCpf(cpf);

        if (instrutor == null) {
            throw new InstrutorNaoEncontradoException(
                "Instrutor com CPF " + cpf + " não encontrado."
            );
        }

        return instrutor;
    }

    public void atualizar(Instrutor instrutor) {

        if (instrutor == null) {
            throw new IllegalArgumentException(
                "O instrutor informado não pode ser nulo."
            );
        }

        instrutor.setCpf(ValidadorCpf.normalizar(instrutor.getCpf()));

        buscarPorId(instrutor.getId());

        validarDados(
            instrutor.getNome(),
            instrutor.getCpf(),
            instrutor.getTelefone(),
            instrutor.getEndereco(),
            instrutor.getEspecialidade(),
            instrutor.getCref()
        );

        Instrutor comMesmoCpf = instrutorDAO.buscarPorCpf(instrutor.getCpf());

        if (comMesmoCpf != null &&
            comMesmoCpf.getId() != instrutor.getId()) {

            throw new IllegalArgumentException(
                "Este CPF já pertence a outro instrutor."
            );
        }

        instrutorDAO.atualizar(instrutor);
    }

    public void excluir(int id) {
        buscarPorId(id);
        if (treinoService != null && treinoService.possuiTreinoParaInstrutor(id)) {
            throw new IllegalStateException(
                "Não é possível excluir este instrutor porque há treinos vinculados."
            );
        }
        instrutorDAO.excluir(id);
    }



    private void validarDados(String nome, String cpf,
                              String telefone, String endereco,
                              String especialidade, String cref) {

        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "O nome do instrutor é obrigatório."
            );
        }

        if (!ValidadorCpf.isValido(cpf)) {
            throw new IllegalArgumentException(
                "O CPF informado é inválido."
            );
        }

        if (telefone == null || telefone.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "O telefone do instrutor é obrigatório."
            );
        }

        if (endereco == null || endereco.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "O endereço do instrutor é obrigatório."
            );
        }

        if (especialidade == null || especialidade.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "A especialidade do instrutor é obrigatória."
            );
        }

        if (cref == null || cref.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "O CREF do instrutor é obrigatório."
            );
        }
    }
}
