package academia.modelo;

/**
 * Contrato para entidades que possuem um identificador numérico único.
 * Permite que a camada de dados trate qualquer entidade de forma genérica
 * (ver {@code academia.dados.ArquivoTextoDAO}).
 */
public interface Identificavel {
    int getId();
}
