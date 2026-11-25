package academia.excecoes;

public class InstrutorNaoEncontradoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InstrutorNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
