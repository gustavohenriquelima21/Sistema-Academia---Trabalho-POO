package academia.excecoes;

public class TreinoNaoEncontradoException extends Exception {

    private static final long serialVersionUID = 1L;

    public TreinoNaoEncontradoException(int id) {
        super("Treino com ID " + id + " não encontrado.");
    }
}