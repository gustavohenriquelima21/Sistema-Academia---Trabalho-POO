package academia.dados;

import academia.modelo.Aluno;

import java.time.LocalDate;

/** Formato: id|nome|cpf|telefone|endereco|nascimento|ativo|ALUNO (textos em Base64). */
public class AlunoDAO extends ArquivoTextoDAO<Aluno> {

    public AlunoDAO() {
        super("alunos.txt", "aluno", "alunos");
    }

    @Override
    protected Aluno converter(String linha) {
        String[] dados = linha.split("\\|", -1);

        if (dados.length != 8) {
            throw new IllegalArgumentException("Registro inválido.");
        }
        if (!dados[7].equals("ALUNO")) {
            throw new IllegalArgumentException("Tipo inválido.");
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
        return aluno;
    }

    @Override
    protected String formatar(Aluno aluno) {
        return aluno.getId() + "|" +
            codificar(aluno.getNome()) + "|" +
            codificar(aluno.getCpf()) + "|" +
            codificar(aluno.getTelefone()) + "|" +
            codificar(aluno.getEndereco()) + "|" +
            aluno.getDataNascimento() + "|" +
            aluno.isAtivo() + "|" +
            "ALUNO";
    }

    public Aluno buscarPorCpf(String cpf) {
        for (Aluno aluno : listarTodos()) {
            if (aluno.getCpf().equals(cpf)) {
                return aluno;
            }
        }
        return null;
    }
}
