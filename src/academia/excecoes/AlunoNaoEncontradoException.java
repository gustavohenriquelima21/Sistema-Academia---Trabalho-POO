package academia.excecoes;

public class AlunoNaoEncontradoException extends RuntimeException {

    public AlunoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}

