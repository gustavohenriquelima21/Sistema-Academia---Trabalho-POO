package academia.modelo;
import java.time.LocalDate;

public class Mensalidade {
    private int numeroParcela;
    private LocalDate dataVencimento;
    private double valor;
    private LocalDate dataPagamento;
    private StatusMensalidade status;

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

    @Override
    public String toString(){
        return "Parcela " + numeroParcela + " - R$ " + String.format("%.2f", valor) + " - vence em " + dataVencimento + " - " + status;
    }

    public void pagar(LocalDate data){
        if(data==null){
            throw new IllegalArgumentException("Data inválida.");
        }
        if (status==StatusMensalidade.PAGA){
            throw new IllegalStateException("Mensalidade já está paga.");
        }
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
