package academia.modelo;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Matricula{
    private int id;
    private int idAluno;
    private int idPlano;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private StatusMatricula status;
    private List<Mensalidade> mensalidades;

    public Matricula(int id, int idAluno, int idPlano, LocalDate dataInicio, LocalDate dataFim){
        this.id=id;
        this.idAluno=idAluno;
        this.idPlano=idPlano;
        this.dataInicio=dataInicio;
        this.dataFim=dataFim;
        this.status = StatusMatricula.ATIVA;
        this.mensalidades = new ArrayList<>();
    }
    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }

    public int getIdAluno(){
        return idAluno;
    }

    public int getIdPlano(){
        return idPlano;
    }

    public LocalDate getDataInicio(){
        return dataInicio;
    }

    public LocalDate getDataFim(){
        return dataFim;
    }

    public StatusMatricula getStatus(){
        return status;
    }
    public void setStatus(StatusMatricula status){
        this.status = status;
    }

    public List<Mensalidade> getMensalidades(){
        return mensalidades;
    }
    public void setMensalidades(List<Mensalidade> mensalidades){
        this.mensalidades = mensalidades;
    }

    public boolean estaAtiva(){
        return status==StatusMatricula.ATIVA;
    }
    public void cancelar(){
        if(status!=StatusMatricula.ATIVA){
            throw new IllegalStateException("Só é possível cancelar uma matrícula ativa");
        } else{
            status = StatusMatricula.CANCELADA;
        }
    }
    public void gerarMensalidades(Plano plano){
        if(!mensalidades.isEmpty()){
            throw new IllegalStateException("Mensalidades já foram geradas");
        }
        for(int i = 0; i < plano.getDuracaoMeses(); i++){
            mensalidades.add(new Mensalidade(i + 1, dataInicio.plusMonths(i), plano.getValorMensal()));
        }
    }

    @Override
    public String toString(){
        return "Matrícula " + id + " - Aluno " + idAluno + " - Plano " + idPlano + " - " + dataInicio + " até " + dataFim + " - " + status;
    }
    }
