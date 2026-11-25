package academia.excecoes;

public class PlanoNaoEncontradoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PlanoNaoEncontradoException(int id) {
        super("Plano com ID " + id + " não encontrado.");
    }
}
