package academia.dados;

import academia.modelo.FormaPagamento;
import academia.modelo.Matricula;
import academia.modelo.Mensalidade;
import academia.modelo.StatusMatricula;
import academia.modelo.StatusMensalidade;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Formato: id|idAluno|idPlano|inicio|fim|status|mensalidades
 * Mensalidades separadas por ";" e campos por ",":
 * parcela,vencimento,valor,dataPagamento,status[,formaPagamento,valorPago]
 * (os dois últimos campos são opcionais para manter compatibilidade com
 * arquivos gravados antes da funcionalidade de formas de pagamento).
 */
public class MatriculaDAO extends ArquivoTextoDAO<Matricula> {

    public MatriculaDAO() {
        super("matriculas.txt", "matrícula", "matrículas");
    }

    @Override
    protected Matricula converter(String linha) {
        String[] dados = linha.split("\\|", -1);

        if (dados.length != 7) {
            throw new IllegalArgumentException("Registro inválido.");
        }

        Matricula matricula = new Matricula(
            Integer.parseInt(dados[0]),
            Integer.parseInt(dados[1]),
            Integer.parseInt(dados[2]),
            LocalDate.parse(dados[3]),
            LocalDate.parse(dados[4])
        );
        matricula.setStatus(StatusMatricula.valueOf(dados[5]));
        matricula.setMensalidades(converterMensalidades(dados[6]));
        return matricula;
    }

    private List<Mensalidade> converterMensalidades(String texto) {
        List<Mensalidade> mensalidades = new ArrayList<>();

        if (texto.isEmpty()) {
            return mensalidades;
        }

        for (String item : texto.split(";")) {
            String[] campos = item.split(",", -1);

            if (campos.length != 5 && campos.length != 7) {
                throw new IllegalArgumentException("Mensalidade inválida.");
            }

            Mensalidade mensalidade = new Mensalidade(
                Integer.parseInt(campos[0]),
                LocalDate.parse(campos[1]),
                Double.parseDouble(campos[2])
            );

            if (!campos[3].isEmpty()) {
                mensalidade.setDataPagamento(LocalDate.parse(campos[3]));
            }
            mensalidade.setStatus(StatusMensalidade.valueOf(campos[4]));

            if (campos.length == 7 && !campos[5].isEmpty()) {
                mensalidade.setFormaPagamento(FormaPagamento.porCodigo(campos[5]));
                mensalidade.setValorPago(Double.parseDouble(campos[6]));
            }

            mensalidades.add(mensalidade);
        }

        return mensalidades;
    }

    private String formatarMensalidades(List<Mensalidade> mensalidades) {
        List<String> itens = new ArrayList<>();

        for (Mensalidade m : mensalidades) {
            String dataPagamento = m.getDataPagamento() == null ? "" : m.getDataPagamento().toString();
            String forma = m.getFormaPagamento() == null ? "" : m.getFormaPagamento().getCodigo();

            itens.add(
                m.getNumeroParcela() + "," +
                m.getDataVencimento() + "," +
                m.getValor() + "," +
                dataPagamento + "," +
                m.getStatus() + "," +
                forma + "," +
                m.getValorPago()
            );
        }

        return String.join(";", itens);
    }

    @Override
    protected String formatar(Matricula matricula) {
        return matricula.getId() + "|" +
            matricula.getIdAluno() + "|" +
            matricula.getIdPlano() + "|" +
            matricula.getDataInicio() + "|" +
            matricula.getDataFim() + "|" +
            matricula.getStatus() + "|" +
            formatarMensalidades(matricula.getMensalidades());
    }

    public List<Matricula> listarPorAluno(int idAluno) {
        List<Matricula> resultado = new ArrayList<>();
        for (Matricula matricula : listarTodos()) {
            if (matricula.getIdAluno() == idAluno) {
                resultado.add(matricula);
            }
        }
        return resultado;
    }

    public List<Matricula> listarPorPlano(int idPlano) {
        List<Matricula> resultado = new ArrayList<>();
        for (Matricula matricula : listarTodos()) {
            if (matricula.getIdPlano() == idPlano) {
                resultado.add(matricula);
            }
        }
        return resultado;
    }

    public Matricula buscarAtivaPorAluno(int idAluno) {
        for (Matricula matricula : listarTodos()) {
            if (matricula.getIdAluno() == idAluno && matricula.estaAtiva()) {
                return matricula;
            }
        }
        return null;
    }
}
