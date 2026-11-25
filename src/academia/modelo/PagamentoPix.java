package academia.modelo;

/** Pagamento via Pix: 2% de desconto. */
public class PagamentoPix extends FormaPagamento {

    private static final double DESCONTO = 0.02;

    @Override
    public String getCodigo() {
        return "PIX";
    }

    @Override
    public String getNome() {
        return "Pix";
    }

    @Override
    protected double aplicarRegra(double valorBase) {
        return valorBase * (1 - DESCONTO);
    }

    @Override
    public String getDescricaoRegra() {
        return "2% de desconto";
    }
}
