package academia.modelo;

/** Pagamento em dinheiro: 5% de desconto. */
public class PagamentoDinheiro extends FormaPagamento {

    private static final double DESCONTO = 0.05;

    @Override
    public String getCodigo() {
        return "DINHEIRO";
    }

    @Override
    public String getNome() {
        return "Dinheiro";
    }

    @Override
    protected double aplicarRegra(double valorBase) {
        return valorBase * (1 - DESCONTO);
    }

    @Override
    public String getDescricaoRegra() {
        return "5% de desconto";
    }
}
