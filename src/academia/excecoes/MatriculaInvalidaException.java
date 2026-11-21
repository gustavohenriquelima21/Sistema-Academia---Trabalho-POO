package academia.excecoes;

public class MatriculaInvalidaException extends RuntimeException {

    public MatriculaInvalidaException(String mensagem) {
        super(mensagem);
    }
}