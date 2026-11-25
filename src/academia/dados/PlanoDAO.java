package academia.dados;

import academia.modelo.Plano;

/** Formato: id|nome(Base64)|valorMensal|duracaoMeses|ativo. */
public class PlanoDAO extends ArquivoTextoDAO<Plano> {

    public PlanoDAO() {
        super("planos.txt", "plano", "planos");
    }

    @Override
    protected Plano converter(String linha) {
        String[] dados = linha.split("\\|", -1);

        if (dados.length != 5) {
            throw new IllegalArgumentException("Registro inválido.");
        }

        Plano plano = new Plano(
            Integer.parseInt(dados[0]),
            decodificar(dados[1]),
            Double.parseDouble(dados[2]),
            Integer.parseInt(dados[3])
        );
        plano.setAtivo(Boolean.parseBoolean(dados[4]));
        return plano;
    }

    @Override
    protected String formatar(Plano plano) {
        return plano.getId() + "|" +
            codificar(plano.getNome()) + "|" +
            plano.getValorMensal() + "|" +
            plano.getDuracaoMeses() + "|" +
            plano.isAtivo();
    }
}
