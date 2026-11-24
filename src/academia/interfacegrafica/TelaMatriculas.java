package academia.interfacegrafica;

import academia.dados.PlanoDAO;
import academia.modelo.Aluno;
import academia.modelo.Matricula;
import academia.modelo.Plano;
import academia.servico.AlunoService;
import academia.servico.MatriculaService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;

public class TelaMatriculas extends JDialog {

    private JComboBox<Aluno> comboAluno;
    private JComboBox<Plano> comboPlano;
    private JTextField campoData;

    private DefaultTableModel modeloTabela;
    private JTable tabelaMatriculas;

    private final AlunoService alunoService;
    private final PlanoDAO planoDAO;
    private final MatriculaService matriculaService;

    public TelaMatriculas(JFrame telaPrincipal, AlunoService alunoService,
                          PlanoDAO planoDAO, MatriculaService matriculaService) {
        super(telaPrincipal, "Gerenciamento de Matrículas", false);
        this.alunoService = alunoService;
        this.planoDAO = planoDAO;
        this.matriculaService = matriculaService;

        setSize(1000, 650);
        setUndecorated(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(telaPrincipal);

        criarComponentes();

        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                atualizarDados();
            }
        });
    }

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
            atualizarDados();

        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                    "Data inválida! Digite no formato dd/MM/aaaa.");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage());
        }
    }

    private void criarComponentes() {
        JPanel conteudo = new JPanel(new BorderLayout(10, 10));
        conteudo.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel formulario = new JPanel(new GridLayout(4, 2, 10, 10));

        comboAluno = new JComboBox<>();
        comboPlano = new JComboBox<>();
        campoData = new JTextField();

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

        JButton botaoVoltar = new JButton("Voltar ao menu");
        botaoVoltar.addActionListener(e -> {
            dispose();
            getOwner().toFront();
            getOwner().requestFocus();
        });

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rodape.add(botaoVoltar);
        conteudo.add(rodape, BorderLayout.SOUTH);

        setContentPane(conteudo);
    }

    private void atualizarDados() {
        carregarDados();
        atualizarTabela();
    }

    private void carregarDados() {
        Aluno alunoSelecionado = (Aluno) comboAluno.getSelectedItem();
        Plano planoSelecionado = (Plano) comboPlano.getSelectedItem();

        List<Aluno> alunos = alunoService.listarTodos();
        List<Plano> planos = planoDAO.listarTodos();

        // Evita duplicações a cada ganho de foco e restaura a seleção pelo ID.
        comboAluno.removeAllItems();
        comboPlano.removeAllItems();

        for (Aluno aluno : alunos) {
            if (aluno.isAtivo()) {
                comboAluno.addItem(aluno);
                if (alunoSelecionado != null && aluno.getId() == alunoSelecionado.getId()) {
                    comboAluno.setSelectedItem(aluno);
                }
            }
        }

        for (Plano plano : planos) {
            if (plano.isAtivo()) {
                comboPlano.addItem(plano);
                if (planoSelecionado != null && plano.getId() == planoSelecionado.getId()) {
                    comboPlano.setSelectedItem(plano);
                }
            }
        }
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
}
