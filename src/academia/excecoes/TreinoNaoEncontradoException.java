package academia.excecoes;

public class TreinoNaoEncontradoException extends Exception {

    public TreinoNaoEncontradoException(int id) {
        super("Treino com ID " + id + " não encontrado.");
    }
}