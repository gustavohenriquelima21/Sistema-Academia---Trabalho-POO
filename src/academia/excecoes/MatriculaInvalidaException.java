package academia.excecoes;

public class MatriculaInvalidaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public MatriculaInvalidaException(String mensagem) {
        super(mensagem);
    }
}