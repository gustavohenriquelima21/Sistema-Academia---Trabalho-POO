package academia.modelo;

/** Pagamento no cartão de crédito: repassa 3% de taxa da operadora. */
public class PagamentoCartaoCredito extends FormaPagamento {

    private static final double TAXA = 0.03;

    @Override
    public String getCodigo() {
        return "CARTAO_CREDITO";
    }

    @Override
    public String getNome() {
        return "Cartão de crédito";
    }

    @Override
    protected double aplicarRegra(double valorBase) {
        return valorBase * (1 + TAXA);
    }

    @Override
    public String getDescricaoRegra() {
        return "3% de taxa";
    }
}
