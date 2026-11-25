package academia.modelo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Forma de pagamento de uma mensalidade.
 *
 * Classe abstrata: define o que toda forma de pagamento sabe fazer, mas deixa
 * para cada subclasse a regra de cálculo do valor final (polimorfismo).
 * Quem paga uma mensalidade chama apenas {@link #calcularValorFinal(double)},
 * sem precisar saber qual forma concreta foi escolhida.
 */
public abstract class FormaPagamento {

    private static final List<FormaPagamento> DISPONIVEIS = Collections.unmodifiableList(Arrays.asList(
            new PagamentoDinheiro(),
            new PagamentoPix(),
            new PagamentoCartaoCredito()));

    /** Código gravado no arquivo de dados (não deve mudar). */
    public abstract String getCodigo();

    /** Nome exibido para o usuário. */
    public abstract String getNome();

    /** Regra específica de cada forma: desconto, taxa etc. */
    protected abstract double aplicarRegra(double valorBase);

    /** Texto curto que explica a regra aplicada (ex.: "5% de desconto"). */
    public abstract String getDescricaoRegra();

    /**
     * Método-modelo: valida o valor, aplica a regra da subclasse e
     * arredonda para centavos. As subclasses não podem alterar esse fluxo.
     */
    public final double calcularValorFinal(double valorBase) {
        if (valorBase < 0 || Double.isNaN(valorBase) || Double.isInfinite(valorBase)) {
            throw new IllegalArgumentException("Valor base inválido para pagamento.");
        }
        return Math.round(aplicarRegra(valorBase) * 100.0) / 100.0;
    }

    public static List<FormaPagamento> disponiveis() {
        return DISPONIVEIS;
    }

    /** Reconstrói a forma de pagamento a partir do código salvo no arquivo. */
    public static FormaPagamento porCodigo(String codigo) {
        for (FormaPagamento forma : DISPONIVEIS) {
            if (forma.getCodigo().equals(codigo)) {
                return forma;
            }
        }
        throw new IllegalArgumentException("Forma de pagamento desconhecida: " + codigo);
    }

    @Override
    public String toString() {
        return getNome() + " (" + getDescricaoRegra() + ")";
    }
}
