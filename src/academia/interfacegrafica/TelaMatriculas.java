package academia.interfacegrafica;

import academia.dados.PlanoDAO;
import academia.modelo.Aluno;
import academia.modelo.Matricula;
import academia.modelo.Mensalidade;
import academia.modelo.Plano;
import academia.modelo.StatusMensalidade;
import academia.servico.AlunoService;
import academia.servico.MatriculaService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;

public class TelaMatriculas extends JDialog {

    private JComboBox<Aluno> comboAluno;
    private JComboBox<Plano> comboPlano;
    private JTextField campoData;

    private DefaultTableModel modeloTabela;
    private JTable tabelaMatriculas;
    private DefaultTableModel modeloMensalidades;
    private JTable tabelaMensalidades;
    private JButton botaoCancelarMatricula;
    private JButton botaoPagarMensalidade;

    private Integer idMatriculaSelecionada;
    private Integer numeroParcelaSelecionada;
    private boolean atualizandoTabelaMatriculas;
    private boolean atualizandoTabelaMensalidades;

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
        atualizarDados();

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

        if (aluno == null) {
            JOptionPane.showMessageDialog(this,
                    "Selecione um aluno ativo.",
                    "Aluno não selecionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (plano == null) {
            JOptionPane.showMessageDialog(this,
                    "Não há planos ativos. Use o botão 'Cadastrar plano' para criar um plano antes da matrícula.",
                    "Plano não selecionado", JOptionPane.WARNING_MESSAGE);
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

            idMatriculaSelecionada = matricula.getId();
            numeroParcelaSelecionada = null;

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

        JButton botaoCadastrarPlano = new JButton("Cadastrar plano");
        botaoCadastrarPlano.addActionListener(e -> cadastrarPlano());
        JPanel painelPlano = new JPanel(new BorderLayout(8, 0));
        painelPlano.add(comboPlano, BorderLayout.CENTER);
        painelPlano.add(botaoCadastrarPlano, BorderLayout.EAST);

        formulario.add(new JLabel("Aluno:"));
        formulario.add(comboAluno);

        formulario.add(new JLabel("Plano:"));
        formulario.add(painelPlano);

        formulario.add(new JLabel("Data de início (dd/MM/aaaa):"));
        formulario.add(campoData);

        JButton botaoMatricular = new JButton("Realizar matrícula");
        botaoMatricular.addActionListener(e -> realizarMatricula());

        formulario.add(new JLabel());
        formulario.add(botaoMatricular);

        formulario.setBorder(BorderFactory.createTitledBorder("Nova Matrícula"));

        JSplitPane divisaoTabelas = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                criarPainelTabelaMatriculas(), criarPainelTabelaMensalidades());
        divisaoTabelas.setResizeWeight(0.5);

        conteudo.add(formulario, BorderLayout.NORTH);
        conteudo.add(divisaoTabelas, BorderLayout.CENTER);

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

    private JPanel criarPainelTabelaMatriculas() {
        modeloTabela = new DefaultTableModel(
                new String[]{"ID", "Aluno", "Plano", "Data de início", "Data de término", "Status"}, 0
        ) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };

        tabelaMatriculas = new JTable(modeloTabela);
        tabelaMatriculas.setRowHeight(23);
        tabelaMatriculas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaMatriculas.setFillsViewportHeight(true);
        tabelaMatriculas.getTableHeader().setReorderingAllowed(false);
        tabelaMatriculas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !atualizandoTabelaMatriculas) {
                selecionarMatricula();
            }
        });

        botaoCancelarMatricula = new JButton("Cancelar matrícula");
        botaoCancelarMatricula.setEnabled(false);
        botaoCancelarMatricula.addActionListener(e -> cancelarMatriculaSelecionada());

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botoes.add(botaoCancelarMatricula);

        JPanel painel = new JPanel(new BorderLayout(0, 5));
        painel.setBorder(BorderFactory.createTitledBorder("Matrículas Cadastradas"));
        painel.add(new JScrollPane(tabelaMatriculas), BorderLayout.CENTER);
        painel.add(botoes, BorderLayout.SOUTH);
        return painel;
    }

    private JPanel criarPainelTabelaMensalidades() {
        modeloMensalidades = new DefaultTableModel(
                new String[]{"Parcela", "Vencimento", "Valor", "Data do pagamento", "Status"}, 0
        ) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };

        tabelaMensalidades = new JTable(modeloMensalidades);
        tabelaMensalidades.setRowHeight(23);
        tabelaMensalidades.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaMensalidades.setFillsViewportHeight(true);
        tabelaMensalidades.getTableHeader().setReorderingAllowed(false);
        tabelaMensalidades.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !atualizandoTabelaMensalidades) {
                int linha = tabelaMensalidades.getSelectedRow();
                numeroParcelaSelecionada = linha < 0 ? null
                        : ((Number) modeloMensalidades.getValueAt(linha, 0)).intValue();
                atualizarEstadoBotoes();
            }
        });

        botaoPagarMensalidade = new JButton("Pagar mensalidade selecionada");
        botaoPagarMensalidade.setEnabled(false);
        botaoPagarMensalidade.addActionListener(e -> pagarMensalidadeSelecionada());

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botoes.add(botaoPagarMensalidade);

        JPanel painel = new JPanel(new BorderLayout(0, 5));
        painel.setBorder(BorderFactory.createTitledBorder("Mensalidades da matrícula selecionada"));
        painel.add(new JScrollPane(tabelaMensalidades), BorderLayout.CENTER);
        painel.add(botoes, BorderLayout.SOUTH);
        return painel;
    }

    private void cadastrarPlano() {
        JTextField campoNome = new JTextField();
        JTextField campoValor = new JTextField();
        JTextField campoDuracao = new JTextField();

        JPanel formulario = new JPanel(new GridLayout(3, 2, 8, 8));
        formulario.add(new JLabel("Nome do plano:"));
        formulario.add(campoNome);
        formulario.add(new JLabel("Valor mensal (R$):"));
        formulario.add(campoValor);
        formulario.add(new JLabel("Duração (meses):"));
        formulario.add(campoDuracao);

        int resposta = JOptionPane.showConfirmDialog(this, formulario,
                "Cadastrar plano", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (resposta != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            String nome = campoNome.getText().trim();
            double valorMensal = Double.parseDouble(
                    campoValor.getText().trim().replace(',', '.'));
            int duracaoMeses = Integer.parseInt(campoDuracao.getText().trim());

            if (nome.isEmpty()) {
                throw new IllegalArgumentException("Informe o nome do plano.");
            }
            if (!Double.isFinite(valorMensal) || valorMensal < 0) {
                throw new IllegalArgumentException("Informe um valor mensal válido.");
            }
            if (duracaoMeses <= 0) {
                throw new IllegalArgumentException("A duração deve ser maior que zero.");
            }

            Plano plano = new Plano(planoDAO.proximoId(), nome,
                    valorMensal, duracaoMeses);
            planoDAO.inserir(plano);
            atualizarDados();
            selecionarPlanoPorId(plano.getId());
            JOptionPane.showMessageDialog(this,
                    "Plano cadastrado e selecionado para a matrícula.");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Informe o valor mensal e a duração usando números válidos.",
                    "Dados inválidos", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Dados inválidos", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void selecionarPlanoPorId(int idPlano) {
        for (int i = 0; i < comboPlano.getItemCount(); i++) {
            if (comboPlano.getItemAt(i).getId() == idPlano) {
                comboPlano.setSelectedIndex(i);
                return;
            }
        }
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

        if (comboAluno.getSelectedItem() == null && comboAluno.getItemCount() > 0) {
            comboAluno.setSelectedIndex(0);
        }
        if (comboPlano.getSelectedItem() == null && comboPlano.getItemCount() > 0) {
            comboPlano.setSelectedIndex(0);
        }
    }

    private void atualizarTabela() {
        int linhaSelecionada = -1;
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        atualizandoTabelaMatriculas = true;
        try {
            modeloTabela.setRowCount(0);

            for (Matricula matricula : matriculaService.listarTodos()) {
                Aluno aluno = alunoService.buscarPorId(matricula.getIdAluno());
                Plano plano = planoDAO.buscarPorId(matricula.getIdPlano());

                modeloTabela.addRow(new Object[]{
                        matricula.getId(),
                        aluno.getNome(),
                        plano == null ? "Plano removido" : plano.getNome(),
                        matricula.getDataInicio().format(formato),
                        matricula.getDataFim().format(formato),
                        matricula.getStatus()
                });
                if (idMatriculaSelecionada != null
                        && matricula.getId() == idMatriculaSelecionada) {
                    linhaSelecionada = modeloTabela.getRowCount() - 1;
                }
            }

            if (linhaSelecionada >= 0) {
                tabelaMatriculas.setRowSelectionInterval(linhaSelecionada, linhaSelecionada);
            } else if (idMatriculaSelecionada != null) {
                idMatriculaSelecionada = null;
                numeroParcelaSelecionada = null;
                tabelaMatriculas.clearSelection();
            }
        } finally {
            atualizandoTabelaMatriculas = false;
        }

        atualizarTabelaMensalidades();
    }

    private void selecionarMatricula() {
        int linha = tabelaMatriculas.getSelectedRow();
        Integer novoId = linha < 0 ? null
                : ((Number) modeloTabela.getValueAt(linha, 0)).intValue();

        if (novoId == null || !novoId.equals(idMatriculaSelecionada)) {
            numeroParcelaSelecionada = null;
        }
        idMatriculaSelecionada = novoId;
        atualizarTabelaMensalidades();
    }

    private void atualizarTabelaMensalidades() {
        int linhaSelecionada = -1;
        DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        NumberFormat formatoValor = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

        atualizandoTabelaMensalidades = true;
        try {
            modeloMensalidades.setRowCount(0);
            if (idMatriculaSelecionada != null) {
                Matricula matricula = matriculaService.buscarPorId(idMatriculaSelecionada);
                for (Mensalidade mensalidade : matricula.getMensalidades()) {
                    String status = mensalidade.estaAtrasada()
                            ? "ATRASADA" : mensalidade.getStatus().toString();
                    String dataPagamento = mensalidade.getDataPagamento() == null
                            ? "—" : mensalidade.getDataPagamento().format(formatoData);

                    modeloMensalidades.addRow(new Object[]{
                            mensalidade.getNumeroParcela(),
                            mensalidade.getDataVencimento().format(formatoData),
                            formatoValor.format(mensalidade.getValor()),
                            dataPagamento,
                            status
                    });
                    if (numeroParcelaSelecionada != null
                            && mensalidade.getNumeroParcela() == numeroParcelaSelecionada) {
                        linhaSelecionada = modeloMensalidades.getRowCount() - 1;
                    }
                }
            }

            if (linhaSelecionada >= 0) {
                tabelaMensalidades.setRowSelectionInterval(linhaSelecionada, linhaSelecionada);
            } else {
                numeroParcelaSelecionada = null;
                tabelaMensalidades.clearSelection();
            }
        } finally {
            atualizandoTabelaMensalidades = false;
        }

        atualizarEstadoBotoes();
    }

    private void atualizarEstadoBotoes() {
        Matricula matricula = buscarMatriculaSelecionada();
        boolean ativa = matricula != null && matricula.estaAtiva();
        botaoCancelarMatricula.setEnabled(ativa);

        Mensalidade mensalidade = buscarMensalidadeSelecionada(matricula);
        botaoPagarMensalidade.setEnabled(ativa && mensalidade != null
                && mensalidade.getStatus() != StatusMensalidade.PAGA);
    }

    private Matricula buscarMatriculaSelecionada() {
        if (idMatriculaSelecionada == null) {
            return null;
        }
        try {
            return matriculaService.buscarPorId(idMatriculaSelecionada);
        } catch (RuntimeException e) {
            return null;
        }
    }

    private Mensalidade buscarMensalidadeSelecionada(Matricula matricula) {
        if (matricula == null || numeroParcelaSelecionada == null) {
            return null;
        }
        for (Mensalidade mensalidade : matricula.getMensalidades()) {
            if (mensalidade.getNumeroParcela() == numeroParcelaSelecionada) {
                return mensalidade;
            }
        }
        return null;
    }

    private void cancelarMatriculaSelecionada() {
        Matricula matricula = buscarMatriculaSelecionada();
        if (matricula == null) {
            JOptionPane.showMessageDialog(this,
                    "Selecione uma matrícula para cancelar.",
                    "Matrícula não selecionada", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!matricula.estaAtiva()) {
            atualizarDados();
            JOptionPane.showMessageDialog(this,
                    "Somente matrículas ativas podem ser canceladas.",
                    "Operação não permitida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Aluno aluno = alunoService.buscarPorId(matricula.getIdAluno());
        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Confirma o cancelamento da matrícula de " + aluno.getNome() + "?",
                "Confirmar cancelamento", JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (confirmacao != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            matriculaService.cancelar(matricula.getId());
            atualizarDados();
            JOptionPane.showMessageDialog(this, "Matrícula cancelada com sucesso.");
        } catch (RuntimeException e) {
            atualizarDados();
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Não foi possível cancelar a matrícula", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void pagarMensalidadeSelecionada() {
        Matricula matricula = buscarMatriculaSelecionada();
        Mensalidade mensalidade = buscarMensalidadeSelecionada(matricula);
        if (matricula == null || mensalidade == null) {
            JOptionPane.showMessageDialog(this,
                    "Selecione uma matrícula e uma mensalidade pendente.",
                    "Mensalidade não selecionada", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!matricula.estaAtiva()) {
            atualizarDados();
            JOptionPane.showMessageDialog(this,
                    "Somente mensalidades de matrículas ativas podem ser pagas.",
                    "Operação não permitida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (mensalidade.getStatus() == StatusMensalidade.PAGA) {
            atualizarTabelaMensalidades();
            JOptionPane.showMessageDialog(this,
                    "Esta mensalidade já está paga.",
                    "Pagamento já registrado", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String valor = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"))
                .format(mensalidade.getValor());
        LocalDate dataPagamento = LocalDate.now();
        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Confirmar pagamento da parcela " + mensalidade.getNumeroParcela()
                        + " no valor de " + valor + " em " + dataPagamento.format(formato) + "?",
                "Confirmar pagamento", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (confirmacao != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            matriculaService.pagarMensalidade(matricula.getId(),
                    mensalidade.getNumeroParcela(), dataPagamento);
            atualizarDados();
            JOptionPane.showMessageDialog(this, "Pagamento registrado com sucesso.");
        } catch (RuntimeException e) {
            atualizarDados();
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Não foi possível registrar o pagamento", JOptionPane.ERROR_MESSAGE);
        }
    }
}
