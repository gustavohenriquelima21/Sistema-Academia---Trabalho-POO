
package academia.interfacegrafica;

import academia.dados.PlanoDAO;
import academia.modelo.Aluno;
import academia.modelo.Plano;
import academia.servico.AlunoService;
import academia.servico.MatriculaService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import academia.modelo.Matricula;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

public class TelaMatriculas extends JFrame {

    private JComboBox<Aluno> comboAluno;
    private JComboBox<Plano> comboPlano;
    private JTextField campoData;

    private DefaultTableModel modeloTabela;
    private JTable tabelaMatriculas;

    private final AlunoService alunoService = new AlunoService();
    private final PlanoDAO planoDAO = new PlanoDAO();
    private final MatriculaService matriculaService = new MatriculaService();

    private void realizarMatricula() {

    Aluno aluno = (Aluno) comboAluno.getSelectedItem();
    Plano plano = (Plano) comboPlano.getSelectedItem();

    if (aluno == null || plano == null) {
        JOptionPane.showMessageDialog(this,
                "Selecione um aluno e um plano.");
        return;
    }

    try {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/uuuu")
        .withResolverStyle(ResolverStyle.STRICT);

        LocalDate data = LocalDate.parse(campoData.getText(), formato);

        Matricula matricula = matriculaService.matricular(
                aluno.getId(),
                plano.getId(),
                data
        );

        JOptionPane.showMessageDialog(this,
                "Matrícula realizada com sucesso! ID: " + matricula.getId());

        campoData.setText("");
        atualizarTabela();

    } catch (DateTimeParseException e) {
        JOptionPane.showMessageDialog(this,
                "Data inválida! Digite no formato dd/MM/aaaa.");

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this,
                e.getMessage());
    }
}

    public TelaMatriculas() {
        super("Gerenciamento de Matrículas");

        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        criarComponentes();
        carregarDados();
        atualizarTabela();
    }

    private void criarComponentes() {

        JPanel conteudo = new JPanel(new BorderLayout(10, 10));
        conteudo.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel formulario = new JPanel(new GridLayout(4, 2, 10, 10));

        comboAluno = new JComboBox<>();
        comboPlano = new JComboBox<>();
        campoData = new JTextField();

        formulario.add(new JLabel("Aluno:"));
        formulario.add(comboAluno);

        formulario.add(new JLabel("Plano:"));
        formulario.add(comboPlano);

        formulario.add(new JLabel("Data de início (dd/MM/aaaa):"));
        formulario.add(campoData);

        JButton botaoMatricular = new JButton("Realizar matrícula");
        botaoMatricular.addActionListener(e -> realizarMatricula());

        formulario.add(new JLabel());
        formulario.add(botaoMatricular);

        formulario.setBorder(BorderFactory.createTitledBorder("Nova Matrícula"));

        modeloTabela = new DefaultTableModel(
                new String[]{"ID", "Aluno", "Plano", "Data de início", "Data de término", "Status"}, 0
        );

        tabelaMatriculas = new JTable(modeloTabela);
        tabelaMatriculas.setRowHeight(25);

        JPanel painelTabela = new JPanel(new BorderLayout());
        painelTabela.setBorder(BorderFactory.createTitledBorder("Matrículas Cadastradas"));
        painelTabela.add(new JScrollPane(tabelaMatriculas), BorderLayout.CENTER);

        conteudo.add(formulario, BorderLayout.NORTH);
        conteudo.add(painelTabela, BorderLayout.CENTER);

        setContentPane(conteudo);
    }

private void carregarDados() {

    List<Aluno> alunos = alunoService.listarTodos();

    for (Aluno aluno : alunos) {
        if (aluno.isAtivo()) {
            comboAluno.addItem(aluno);
        }
    }

    List<Plano> planos = planoDAO.listarTodos();

    for (Plano plano : planos) {
        if (plano.isAtivo()) {
            comboPlano.addItem(plano);
        }
    }

    comboAluno.setRenderer(new DefaultListCellRenderer() {
        @Override
        public Component getListCellRendererComponent(
                JList<?> lista, Object valor, int indice,
                boolean selecionado, boolean foco) {

            if (valor instanceof Aluno) {
                valor = ((Aluno) valor).getNome();
            }

            return super.getListCellRendererComponent(
                    lista, valor, indice, selecionado, foco);
        }
    });

    comboPlano.setRenderer(new DefaultListCellRenderer() {
        @Override
        public Component getListCellRendererComponent(
                JList<?> lista, Object valor, int indice,
                boolean selecionado, boolean foco) {

            if (valor instanceof Plano) {
                valor = ((Plano) valor).getNome();
            }

            return super.getListCellRendererComponent(
                    lista, valor, indice, selecionado, foco);
        }
    });
}

private void atualizarTabela() {

    modeloTabela.setRowCount(0);

    DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    for (Matricula matricula : matriculaService.listarTodos()) {

        Aluno aluno = alunoService.buscarPorId(matricula.getIdAluno());
        Plano plano = planoDAO.buscarPorId(matricula.getIdPlano());

        modeloTabela.addRow(new Object[]{
                matricula.getId(),
                aluno.getNome(),
                plano.getNome(),
                matricula.getDataInicio().format(formato),
                matricula.getDataFim().format(formato),
                matricula.getStatus()
        });
    }
}


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TelaMatriculas tela = new TelaMatriculas();
            tela.setVisible(true);
        });
    }
}
