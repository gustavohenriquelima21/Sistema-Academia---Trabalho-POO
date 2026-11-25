package academia.excecoes;

public class AlunoNaoEncontradoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AlunoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}

