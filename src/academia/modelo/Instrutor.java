
package academia.modelo;

public class Instrutor extends Pessoa {

    private String especialidade;
    private String cref;

    public Instrutor(int id, String nome, String cpf,
                     String telefone, String endereco,
                     String especialidade, String cref) {

        super(id, nome, cpf, telefone, endereco);

        this.especialidade = especialidade;
        this.cref = cref;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    public String getCref() {
        return cref;
    }

    public void setCref(String cref) {
        this.cref = cref;
    }

    @Override
    public String getTipoPessoa() {
        return "Instrutor";
    }
}

