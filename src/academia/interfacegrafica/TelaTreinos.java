package academia.interfacegrafica;

import academia.excecoes.TreinoNaoEncontradoException;
import academia.modelo.Aluno;
import academia.modelo.Exercicio;
import academia.modelo.Instrutor;
import academia.modelo.Pessoa;
import academia.modelo.Treino;
import academia.servico.TreinoService;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.function.Supplier;

public class TelaTreinos extends JDialog {

    private static final String[] OBJETIVOS = {
            "Hipertrofia", "Emagrecimento", "Condicionamento", "Força", "Resistência", "Reabilitação"};
    private static final String[] GRUPOS_MUSCULARES = {
            "Peito", "Costas", "Ombros", "Bíceps", "Tríceps", "Pernas", "Glúteos", "Abdômen", "Corpo inteiro"};
    private static final String[] INTENSIDADES = {"Leve", "Moderada", "Intensa"};

    private final TreinoService treinoService;
    private final Supplier<List<Aluno>> fonteAlunos;
    private final Supplier<List<Instrutor>> fonteInstrutores;

    private final JTextField campoNome = new JTextField(20);
    private final JComboBox<String> comboObjetivo = new JComboBox<>(OBJETIVOS);
    private final JComboBox<Aluno> comboAluno = new JComboBox<>();
    private final JComboBox<Instrutor> comboInstrutor = new JComboBox<>();
    private final JComboBox<Object> comboFiltroAluno = new JComboBox<>();

    private final DefaultTableModel modeloTreinos = criarModelo("ID", "Nome", "Aluno", "Instrutor", "Exercícios", "Duração");
    private final JTable tabelaTreinos = new JTable(modeloTreinos);
    private final DefaultTableModel modeloExercicios = criarModelo("Tipo", "Exercício", "Detalhes", "Duração");
    private final JTable tabelaExercicios = new JTable(modeloExercicios);
    private final JLabel rotuloTreinoSelecionado = new JLabel("Nenhum treino selecionado");

    private final JTextField campoNomeMusculacao = new JTextField(15);
    private final JComboBox<String> comboGrupoMuscular = new JComboBox<>(GRUPOS_MUSCULARES);
    private final JSpinner spinnerSeries = new JSpinner(new SpinnerNumberModel(3, 1, 20, 1));
    private final JSpinner spinnerRepeticoes = new JSpinner(new SpinnerNumberModel(12, 1, 100, 1));
    private final JSpinner spinnerCarga = new JSpinner(new SpinnerNumberModel(10.0, 0.0, 500.0, 2.5));
    private final JSpinner spinnerDescanso = new JSpinner(new SpinnerNumberModel(60, 0, 600, 15));
    private final JTextField campoObservacaoMusculacao = new JTextField(15);

    private final JTextField campoNomeCardio = new JTextField(15);
    private final JSpinner spinnerTempo = new JSpinner(new SpinnerNumberModel(20, 1, 300, 5));
    private final JComboBox<String> comboIntensidade = new JComboBox<>(INTENSIDADES);
    private final JTextField campoObservacaoCardio = new JTextField(15);

    private Integer idTreinoSelecionado;
    private boolean atualizandoTabela;

    public TelaTreinos(JFrame telaPrincipal, TreinoService treinoService,
                       Supplier<List<Aluno>> fonteAlunos,
                       Supplier<List<Instrutor>> fonteInstrutores) {
        super(telaPrincipal, "Gerenciamento de Treinos", false);
        this.treinoService = treinoService;
        this.fonteAlunos = fonteAlunos;
        this.fonteInstrutores = fonteInstrutores;

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(1280, 800);
        setLocationRelativeTo(telaPrincipal);

        comboObjetivo.setEditable(true);
        comboAluno.setRenderer(new RenderizadorPessoa());
        comboInstrutor.setRenderer(new RenderizadorPessoa());
        comboFiltroAluno.setRenderer(new RenderizadorPessoa());

        JPanel conteudo = new JPanel(new BorderLayout(10, 10));
        conteudo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        conteudo.add(criarPainelDadosTreino(), BorderLayout.NORTH);

        JSplitPane divisao = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, criarPainelListaTreinos(), criarPainelExercicios());
        divisao.setResizeWeight(0.45);
        divisao.setDividerLocation(570);
        conteudo.add(divisao, BorderLayout.CENTER);
        setContentPane(conteudo);

        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                atualizarDados();
            }
        });
    }

    private void atualizarDados() {
        recarregarPessoas();
        atualizarTabelaTreinos();
        atualizarEstadoExercicios();
    }

    private JPanel criarPainelDadosTreino() {
        JPanel rotulos = new JPanel(new GridLayout(2, 1, 0, 8));
        rotulos.add(new JLabel("Nome do treino:"));
        rotulos.add(new JLabel("Aluno:"));

        JPanel campos = new JPanel(new GridLayout(2, 1, 0, 8));
        campos.add(campoNome);
        campos.add(comboAluno);

        JPanel rotulos2 = new JPanel(new GridLayout(2, 1, 0, 8));
        rotulos2.add(new JLabel("Objetivo:"));
        rotulos2.add(new JLabel("Instrutor:"));

        JPanel campos2 = new JPanel(new GridLayout(2, 1, 0, 8));
        campos2.add(comboObjetivo);
        campos2.add(comboInstrutor);

        JPanel coluna1 = new JPanel(new BorderLayout(10, 0));
        coluna1.add(rotulos, BorderLayout.WEST);
        coluna1.add(campos, BorderLayout.CENTER);

        JPanel coluna2 = new JPanel(new BorderLayout(10, 0));
        coluna2.add(rotulos2, BorderLayout.WEST);
        coluna2.add(campos2, BorderLayout.CENTER);

        JPanel formulario = new JPanel(new GridLayout(1, 2, 25, 0));
        formulario.add(coluna1);
        formulario.add(coluna2);

        JButton botaoNovo = new JButton("Novo");
        JButton botaoSalvar = new JButton("Salvar Treino");
        JButton botaoExcluir = new JButton("Excluir Treino");
        botaoNovo.addActionListener(e -> limparFormulario());
        botaoSalvar.addActionListener(e -> salvarTreino());
        botaoExcluir.addActionListener(e -> excluirTreino());

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botoes.add(botaoNovo);
        botoes.add(botaoSalvar);
        botoes.add(botaoExcluir);

        JPanel painel = new JPanel(new BorderLayout(0, 10));
        painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Dados do Treino"),
                BorderFactory.createEmptyBorder(5, 10, 8, 10)));
        painel.add(formulario, BorderLayout.CENTER);
        painel.add(botoes, BorderLayout.SOUTH);
        return painel;
    }

    private JPanel criarPainelListaTreinos() {
        tabelaTreinos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaTreinos.setRowHeight(24);
        tabelaTreinos.setFillsViewportHeight(true);
        tabelaTreinos.getTableHeader().setReorderingAllowed(false);
        definirLarguras(tabelaTreinos, 35, 170, 130, 120, 75, 70);
        tabelaTreinos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !atualizandoTabela) {
                selecionarTreino();
            }
        });

        comboFiltroAluno.addActionListener(e -> atualizarTabelaTreinos());

        JPanel filtro = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filtro.add(new JLabel("Filtrar por aluno:"));
        filtro.add(comboFiltroAluno);

        JPanel painel = new JPanel(new BorderLayout(0, 8));
        painel.setBorder(BorderFactory.createTitledBorder("Treinos Cadastrados"));
        painel.add(filtro, BorderLayout.NORTH);
        painel.add(new JScrollPane(tabelaTreinos), BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarPainelExercicios() {
        tabelaExercicios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaExercicios.setRowHeight(24);
        tabelaExercicios.setFillsViewportHeight(true);
        tabelaExercicios.getTableHeader().setReorderingAllowed(false);
        definirLarguras(tabelaExercicios, 95, 150, 280, 70);

        rotuloTreinoSelecionado.setFont(rotuloTreinoSelecionado.getFont().deriveFont(Font.BOLD));

        JButton botaoRemover = new JButton("Remover Exercício");
        botaoRemover.addActionListener(e -> removerExercicio());
        JPanel botoesLista = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botoesLista.add(botaoRemover);

        JPanel lista = new JPanel(new BorderLayout(0, 6));
        lista.add(rotuloTreinoSelecionado, BorderLayout.NORTH);
        lista.add(new JScrollPane(tabelaExercicios), BorderLayout.CENTER);
        lista.add(botoesLista, BorderLayout.SOUTH);

        JTabbedPane abasExercicio = new JTabbedPane();
        abasExercicio.addTab("Musculação", criarFormularioMusculacao());
        abasExercicio.addTab("Cardio", criarFormularioCardio());

        JPanel painel = new JPanel(new BorderLayout(0, 10));
        painel.setBorder(BorderFactory.createTitledBorder("Exercícios do Treino"));
        painel.add(lista, BorderLayout.CENTER);
        painel.add(abasExercicio, BorderLayout.SOUTH);
        return painel;
    }

    private JPanel criarFormularioMusculacao() {
        JPanel campos = new JPanel(new GridLayout(4, 4, 8, 6));
        campos.add(new JLabel("Exercício:"));
        campos.add(campoNomeMusculacao);
        campos.add(new JLabel("Grupo muscular:"));
        campos.add(comboGrupoMuscular);
        campos.add(new JLabel("Séries:"));
        campos.add(spinnerSeries);
        campos.add(new JLabel("Repetições:"));
        campos.add(spinnerRepeticoes);
        campos.add(new JLabel("Carga (kg):"));
        campos.add(spinnerCarga);
        campos.add(new JLabel("Descanso (s):"));
        campos.add(spinnerDescanso);
        campos.add(new JLabel("Observação:"));
        campos.add(campoObservacaoMusculacao);
        campos.add(new JLabel());
        campos.add(new JLabel());

        JButton botaoAdicionar = new JButton("Adicionar Musculação");
        botaoAdicionar.addActionListener(e -> adicionarMusculacao());
        return montarFormulario(campos, botaoAdicionar);
    }

    private JPanel criarFormularioCardio() {
        JPanel campos = new JPanel(new GridLayout(4, 4, 8, 6));
        campos.add(new JLabel("Exercício:"));
        campos.add(campoNomeCardio);
        campos.add(new JLabel("Intensidade:"));
        campos.add(comboIntensidade);
        campos.add(new JLabel("Tempo (min):"));
        campos.add(spinnerTempo);
        campos.add(new JLabel("Observação:"));
        campos.add(campoObservacaoCardio);
        for (int i = 0; i < 8; i++) {
            campos.add(new JLabel());
        }

        JButton botaoAdicionar = new JButton("Adicionar Cardio");
        botaoAdicionar.addActionListener(e -> adicionarCardio());
        return montarFormulario(campos, botaoAdicionar);
    }

    private JPanel montarFormulario(JComponent campos, JButton botao) {
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botoes.add(botao);
        JPanel painel = new JPanel(new BorderLayout(0, 8));
        painel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        painel.add(campos, BorderLayout.CENTER);
        painel.add(botoes, BorderLayout.SOUTH);
        return painel;
    }

    private void recarregarPessoas() {
        Aluno alunoAtual = (Aluno) comboAluno.getSelectedItem();
        Instrutor instrutorAtual = (Instrutor) comboInstrutor.getSelectedItem();
        Object filtroAtual = comboFiltroAluno.getSelectedItem();

        List<Aluno> alunos = fonteAlunos.get();
        List<Instrutor> instrutores = fonteInstrutores.get();

        DefaultComboBoxModel<Aluno> modeloAlunos = new DefaultComboBoxModel<>();
        for (Aluno aluno : alunos) {
            modeloAlunos.addElement(aluno);
        }
        comboAluno.setModel(modeloAlunos);
        selecionarPorId(comboAluno, alunoAtual);

        DefaultComboBoxModel<Instrutor> modeloInstrutores = new DefaultComboBoxModel<>();
        for (Instrutor instrutor : instrutores) {
            modeloInstrutores.addElement(instrutor);
        }
        comboInstrutor.setModel(modeloInstrutores);
        selecionarPorId(comboInstrutor, instrutorAtual);

        DefaultComboBoxModel<Object> modeloFiltro = new DefaultComboBoxModel<>();
        modeloFiltro.addElement("Todos os alunos");
        for (Aluno aluno : alunos) {
            modeloFiltro.addElement(aluno);
        }
        comboFiltroAluno.setModel(modeloFiltro);
        if (filtroAtual instanceof Aluno) {
            selecionarPorId(comboFiltroAluno, (Aluno) filtroAtual);
        }
    }

    private <T> void selecionarPorId(JComboBox<T> combo, Pessoa pessoa) {
        if (pessoa == null) {
            return;
        }
        for (int i = 0; i < combo.getItemCount(); i++) {
            T item = combo.getItemAt(i);
            if (item instanceof Pessoa && ((Pessoa) item).getId() == pessoa.getId()) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void atualizarTabelaTreinos() {
        atualizandoTabela = true;
        modeloTreinos.setRowCount(0);
        Object filtro = comboFiltroAluno.getSelectedItem();
        List<Treino> treinos = filtro instanceof Aluno
                ? treinoService.listarPorAluno(((Aluno) filtro).getId())
                : treinoService.listarTodos();
        int linhaSelecionada = -1;
        for (Treino treino : treinos) {
            modeloTreinos.addRow(new Object[]{
                    treino.getId(),
                    treino.getNome(),
                    treino.getAluno().getNome(),
                    treino.getInstrutor().getNome(),
                    treino.getTotalExercicios(),
                    formatarMinutos(treino.calcularDuracaoTotal())});
            if (idTreinoSelecionado != null && treino.getId() == idTreinoSelecionado) {
                linhaSelecionada = modeloTreinos.getRowCount() - 1;
            }
        }
        if (linhaSelecionada >= 0) {
            tabelaTreinos.setRowSelectionInterval(linhaSelecionada, linhaSelecionada);
        }
        atualizandoTabela = false;
    }

    private void atualizarTabelaExercicios() {
        modeloExercicios.setRowCount(0);
        Treino treino = treinoSelecionado();
        if (treino == null) {
            return;
        }
        for (Exercicio exercicio : treino.getExercicios()) {
            modeloExercicios.addRow(new Object[]{
                    exercicio.getTipo(),
                    exercicio.getNome(),
                    exercicio.getDetalhes(),
                    formatarMinutos(exercicio.calcularDuracaoMinutos())});
        }
    }

    private void atualizarEstadoExercicios() {
        Treino treino = treinoSelecionado();
        if (treino == null) {
            rotuloTreinoSelecionado.setText("Nenhum treino selecionado");
        } else {
            rotuloTreinoSelecionado.setText(treino.getNome() + " — " + treino.getAluno().getNome()
                    + " — duração estimada: " + formatarMinutos(treino.calcularDuracaoTotal()));
        }
        atualizarTabelaExercicios();
    }

    private void selecionarTreino() {
        int linha = tabelaTreinos.getSelectedRow();
        if (linha < 0) {
            return;
        }
        idTreinoSelecionado = (Integer) modeloTreinos.getValueAt(linha, 0);
        Treino treino = treinoSelecionado();
        if (treino != null) {
            campoNome.setText(treino.getNome());
            comboObjetivo.setSelectedItem(treino.getObjetivo());
            selecionarPorId(comboAluno, treino.getAluno());
            selecionarPorId(comboInstrutor, treino.getInstrutor());
        }
        atualizarEstadoExercicios();
    }

    private Treino treinoSelecionado() {
        if (idTreinoSelecionado == null) {
            return null;
        }
        try {
            return treinoService.buscarPorId(idTreinoSelecionado);
        } catch (TreinoNaoEncontradoException e) {
            idTreinoSelecionado = null;
            return null;
        }
    }

    private void salvarTreino() {
        String nome = campoNome.getText();
        String objetivo = String.valueOf(comboObjetivo.getSelectedItem());
        Aluno aluno = (Aluno) comboAluno.getSelectedItem();
        Instrutor instrutor = (Instrutor) comboInstrutor.getSelectedItem();
        try {
            if (idTreinoSelecionado == null) {
                Treino treino = treinoService.criarTreino(nome, objetivo, instrutor, aluno);
                idTreinoSelecionado = treino.getId();
                mostrarInformacao("Treino cadastrado com sucesso! Agora adicione os exercícios.");
            } else {
                treinoService.atualizarTreino(idTreinoSelecionado, nome, objetivo, instrutor);
                mostrarInformacao("Treino atualizado com sucesso!");
            }
            atualizarTabelaTreinos();
            atualizarEstadoExercicios();
        } catch (TreinoNaoEncontradoException | RuntimeException e) {
            mostrarErro(e.getMessage());
        }
    }

    private void excluirTreino() {
        if (idTreinoSelecionado == null) {
            mostrarErro("Selecione um treino na tabela para excluir.");
            return;
        }
        int opcao = JOptionPane.showConfirmDialog(this,
                "Deseja excluir o treino selecionado e todos os seus exercícios?",
                "Confirmar exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (opcao != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            treinoService.excluirTreino(idTreinoSelecionado);
            limparFormulario();
            mostrarInformacao("Treino excluído com sucesso!");
        } catch (TreinoNaoEncontradoException | RuntimeException e) {
            mostrarErro(e.getMessage());
        }
    }

    private void adicionarMusculacao() {
        if (!verificarTreinoSelecionado()) {
            return;
        }
        try {
            treinoService.adicionarMusculacao(idTreinoSelecionado,
                    campoNomeMusculacao.getText(),
                    campoObservacaoMusculacao.getText(),
                    String.valueOf(comboGrupoMuscular.getSelectedItem()),
                    (Integer) spinnerSeries.getValue(),
                    (Integer) spinnerRepeticoes.getValue(),
                    ((Number) spinnerCarga.getValue()).doubleValue(),
                    (Integer) spinnerDescanso.getValue());
            campoNomeMusculacao.setText("");
            campoObservacaoMusculacao.setText("");
            atualizarTabelaTreinos();
            atualizarEstadoExercicios();
        } catch (TreinoNaoEncontradoException | RuntimeException e) {
            mostrarErro(e.getMessage());
        }
    }

    private void adicionarCardio() {
        if (!verificarTreinoSelecionado()) {
            return;
        }
        try {
            treinoService.adicionarCardio(idTreinoSelecionado,
                    campoNomeCardio.getText(),
                    campoObservacaoCardio.getText(),
                    (Integer) spinnerTempo.getValue(),
                    String.valueOf(comboIntensidade.getSelectedItem()));
            campoNomeCardio.setText("");
            campoObservacaoCardio.setText("");
            atualizarTabelaTreinos();
            atualizarEstadoExercicios();
        } catch (TreinoNaoEncontradoException | RuntimeException e) {
            mostrarErro(e.getMessage());
        }
    }

    private void removerExercicio() {
        if (!verificarTreinoSelecionado()) {
            return;
        }
        int linha = tabelaExercicios.getSelectedRow();
        if (linha < 0) {
            mostrarErro("Selecione um exercício na tabela para remover.");
            return;
        }
        try {
            treinoService.removerExercicio(idTreinoSelecionado, linha);
            atualizarTabelaTreinos();
            atualizarEstadoExercicios();
        } catch (TreinoNaoEncontradoException | RuntimeException e) {
            mostrarErro(e.getMessage());
        }
    }

    private boolean verificarTreinoSelecionado() {
        if (idTreinoSelecionado == null) {
            mostrarErro("Selecione ou salve um treino antes de gerenciar os exercícios.");
            return false;
        }
        return true;
    }

    private void limparFormulario() {
        idTreinoSelecionado = null;
        campoNome.setText("");
        comboObjetivo.setSelectedIndex(0);
        if (comboAluno.getItemCount() > 0) {
            comboAluno.setSelectedIndex(0);
        }
        if (comboInstrutor.getItemCount() > 0) {
            comboInstrutor.setSelectedIndex(0);
        }
        tabelaTreinos.clearSelection();
        atualizarTabelaTreinos();
        atualizarEstadoExercicios();
        campoNome.requestFocusInWindow();
    }

    private String formatarMinutos(double minutos) {
        return String.format("%.0f min", Math.ceil(minutos));
    }

    private void mostrarInformacao(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Treinos", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Atenção", JOptionPane.WARNING_MESSAGE);
    }

    private static void definirLarguras(JTable tabela, int... larguras) {
        for (int i = 0; i < larguras.length; i++) {
            tabela.getColumnModel().getColumn(i).setPreferredWidth(larguras[i]);
        }
    }

    private static DefaultTableModel criarModelo(String... colunas) {
        return new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };
    }

    private static class RenderizadorPessoa extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> lista, Object valor, int indice,
                                                      boolean selecionado, boolean foco) {
            Object texto = valor instanceof Pessoa ? ((Pessoa) valor).getNome() : valor;
            return super.getListCellRendererComponent(lista, texto, indice, selecionado, foco);
        }
    }
}