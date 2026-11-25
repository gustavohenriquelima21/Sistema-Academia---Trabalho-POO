package academia.modelo;
import java.time.LocalDate;

public class Mensalidade {
    /** Multa aplicada quando a parcela é paga depois do vencimento. */
    public static final double PERCENTUAL_MULTA_ATRASO = 0.02;

    private int numeroParcela;
    private LocalDate dataVencimento;
    private double valor;
    private LocalDate dataPagamento;
    private StatusMensalidade status;
    private FormaPagamento formaPagamento;
    private double valorPago;

    public Mensalidade (int numeroParcela, LocalDate dataVencimento, double valor){
        this.dataPagamento = null;
        this.status = StatusMensalidade.PENDENTE;
        this.numeroParcela=numeroParcela;
        this.dataVencimento=dataVencimento;
        this.valor=valor;
    }

    public int getNumeroParcela(){
        return numeroParcela;
    }

    public LocalDate getDataVencimento(){
        return dataVencimento;
    }

    public double getValor(){
        return valor;
    }

    public LocalDate getDataPagamento(){
        return dataPagamento;
    }
    public void setDataPagamento(LocalDate dataPagamento){
        this.dataPagamento = dataPagamento;
    }

    public StatusMensalidade getStatus(){
        return status;
    }
    public void setStatus(StatusMensalidade status){
        this.status = status;
    }

    /** Pode ser nula em parcelas pendentes ou pagas antes desta funcionalidade existir. */
    public FormaPagamento getFormaPagamento(){
        return formaPagamento;
    }
    public void setFormaPagamento(FormaPagamento formaPagamento){
        this.formaPagamento = formaPagamento;
    }

    public double getValorPago(){
        return valorPago;
    }
    public void setValorPago(double valorPago){
        this.valorPago = valorPago;
    }

    @Override
    public String toString(){
        return "Parcela " + numeroParcela + " - R$ " + String.format("%.2f", valor) + " - vence em " + dataVencimento + " - " + status;
    }

    /** Valor da parcela na data informada, com multa se estiver atrasada. */
    public double calcularValorDevido(LocalDate data){
        if (data != null && data.isAfter(dataVencimento)) {
            return Math.round(valor * (1 + PERCENTUAL_MULTA_ATRASO) * 100.0) / 100.0;
        }
        return valor;
    }

    /**
     * Registra o pagamento. O valor final depende da forma escolhida:
     * cada subclasse de FormaPagamento aplica a própria regra (polimorfismo).
     */
    public void pagar(LocalDate data, FormaPagamento forma){
        if(data==null){
            throw new IllegalArgumentException("Data inválida.");
        }
        if(forma==null){
            throw new IllegalArgumentException("Selecione a forma de pagamento.");
        }
        if (status==StatusMensalidade.PAGA){
            throw new IllegalStateException("Mensalidade já está paga.");
        }
        this.valorPago = forma.calcularValorFinal(calcularValorDevido(data));
        this.formaPagamento = forma;
        this.dataPagamento=data;
        this.status = StatusMensalidade.PAGA;
    }

    public boolean estaAtrasada(){
        if(status==StatusMensalidade.PENDENTE && dataVencimento.isBefore(LocalDate.now())){
            return true;
        }
        return false;
    }
}
