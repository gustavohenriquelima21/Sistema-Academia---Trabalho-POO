package academia.servico;

/**
 * Normalização e validação de CPF (dígitos verificadores).
 * Antes esse código estava copiado em AlunoService e InstrutorService.
 */
public final class ValidadorCpf {

    private ValidadorCpf() {
    }

    /** Remove tudo o que não for dígito (pontos, traço, espaços). */
    public static String normalizar(String cpf) {
        if (cpf == null) {
            return "";
        }
        return cpf.replaceAll("[^0-9]", "");
    }

    public static boolean isValido(String cpf) {
        if (cpf == null || !cpf.matches("\\d{11}")) {
            return false;
        }
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }
        return calcularDigito(cpf, 9) == Character.getNumericValue(cpf.charAt(9))
            && calcularDigito(cpf, 10) == Character.getNumericValue(cpf.charAt(10));
    }

    private static int calcularDigito(String cpf, int quantidadeDigitos) {
        int soma = 0;
        for (int i = 0; i < quantidadeDigitos; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (quantidadeDigitos + 1 - i);
        }
        int digito = (soma * 10) % 11;
        return digito == 10 ? 0 : digito;
    }
}
