package academia.dados;

import academia.modelo.Instrutor;

/** Formato: id|nome|cpf|telefone|endereco|especialidade|cref|INSTRUTOR (textos em Base64). */
public class InstrutorDAO extends ArquivoTextoDAO<Instrutor> {

    public InstrutorDAO() {
        super("instrutores.txt", "instrutor", "instrutores");
    }

    @Override
    protected Instrutor converter(String linha) {
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
    }

    @Override
    protected String formatar(Instrutor instrutor) {
        return instrutor.getId() + "|" +
            codificar(instrutor.getNome()) + "|" +
            codificar(instrutor.getCpf()) + "|" +
            codificar(instrutor.getTelefone()) + "|" +
            codificar(instrutor.getEndereco()) + "|" +
            codificar(instrutor.getEspecialidade()) + "|" +
            codificar(instrutor.getCref()) + "|" +
            "INSTRUTOR";
    }

    public Instrutor buscarPorCpf(String cpf) {
        for (Instrutor instrutor : listarTodos()) {
            if (instrutor.getCpf().equals(cpf)) {
                return instrutor;
            }
        }
        return null;
    }
}
