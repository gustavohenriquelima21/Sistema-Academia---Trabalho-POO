
package academia.modelo;

import java.time.LocalDate;

public class Aluno extends Pessoa {

    private LocalDate dataNascimento;
    private boolean ativo;

    public Aluno(int id, String nome, String cpf,
                 String telefone, String endereco,
                 LocalDate dataNascimento) {

        super(id, nome, cpf, telefone, endereco);

        this.dataNascimento = dataNascimento;
        this.ativo = true;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String getTipoPessoa() {
        return "Aluno";
    }
}

